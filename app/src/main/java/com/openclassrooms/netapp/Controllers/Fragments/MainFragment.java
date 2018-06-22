package com.openclassrooms.netapp.Controllers.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.openclassrooms.netapp.R;
import com.openclassrooms.netapp.Utils.GithubCalls;
import com.openclassrooms.netapp.Utils.NetworkAsyncTask;
import com.openclassrooms.netapp.models.GithubUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements GithubCalls.Callbacks {

    // FOR DESIGN
    @BindView(R.id.fragment_main_textview) TextView textView;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    // 4 - Declare Subscription
    private Disposable disposable;

    public MainFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    // -----------------
    // ACTIONS
    // -----------------

    @OnClick(R.id.fragment_main_button)
    public void submit(View view) {
        this.executeHttpRequestWithRetrofit();
    }

    @OnClick(R.id.fragment_rxjava_button)
    public void click(View view) {
        //this.executeHttpRequestWithRetrofit();
        this.streamShowString();
    }

    // ------------------------------
    //  Reactive X
    // ------------------------------

    // 1 - Create Observable
    private Observable<String> getObservable(){
        return Observable.just("Cool !");
    }

    // 2 - Create Subscriber
    private DisposableObserver<String> getSubscriber(){
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String item) {
                textView.setText("Observable emits : "+item);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAG","On Error"+Log.getStackTraceString(e));
            }

            @Override
            public void onComplete() {
                Toast.makeText(getContext(), "Complete", Toast.LENGTH_SHORT).show();
                Log.e("TAG", "On Complete !!");
            }
        };
    }

    // 3 - Create Stream and execute it
    private void streamShowString(){
        this.disposable = this.getObservable()
                .map(getFunctionUpperCase())
                .flatMap(getSecondObservable())
                .subscribeWith(getSubscriber());
    }

    // 1 - Create function to Uppercase a string
    private Function<String, String> getFunctionUpperCase(){
        return new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                return s.toUpperCase();
            }
        };
    }

    // 1 - Create a function that will calling a new observable
    private Function<String, Observable<String>> getSecondObservable(){
        return new Function<String, Observable<String>>() {
            @Override
            public Observable<String> apply(String previousString) throws Exception {
                return Observable.just(previousString+" I love Openclassrooms !");
            }
        };
    }

    // 5 - Dispose subscription
    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    // ------------------
    //  UPDATE UI
    // ------------------

    private void updateUIWhenStartingHTTPRequest(){
        this.textView.setText("Downloading...");
        progressBar.setVisibility(View.VISIBLE);
    }

    private void updateUIWhenStopingHTTPRequest(String response){
        this.textView.setText(response);
        progressBar.setVisibility(View.GONE);
    }

    // 3 - Update UI showing only name of users
    private void updateUIWithListOfUsers(List<GithubUser> users){
        StringBuilder stringBuilder = new StringBuilder();
        for (GithubUser user : users){
            stringBuilder.append("-"+user.getLogin().toUpperCase()+"\n");
        }
        updateUIWhenStopingHTTPRequest(stringBuilder.toString());
    }

    // 4 - Execute HTTP request and update UI
    private void executeHttpRequestWithRetrofit(){
        this.updateUIWhenStartingHTTPRequest();
        GithubCalls.fetchUserFollowing(this, "JakeWharton");
    }

    // 2 - Override callback methods

    @Override
    public void onResponse(@Nullable List<GithubUser> users) {
        // 2.1 - When getting response, we update UI
        if (users != null) this.updateUIWithListOfUsers(users);
    }

    @Override
    public void onFailure() {
        // 2.2 - When getting error, we update UI
        this.updateUIWhenStopingHTTPRequest("An error happened !");
    }
}
