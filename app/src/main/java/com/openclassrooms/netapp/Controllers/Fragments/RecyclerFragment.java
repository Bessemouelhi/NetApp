package com.openclassrooms.netapp.Controllers.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.openclassrooms.netapp.R;
import com.openclassrooms.netapp.Utils.GithubStreams;
import com.openclassrooms.netapp.Utils.ItemClickSupport;
import com.openclassrooms.netapp.Views.GithubUserAdapter;
import com.openclassrooms.netapp.models.GithubUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecyclerFragment extends Fragment implements GithubUserAdapter.Listener {

    // FOR DESIGN
    @BindView(R.id.fragment_main_recycler_view)
    RecyclerView recyclerView;
    // 1 - Declare the SwipeRefreshLayout
    @BindView(R.id.fragment_main_swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    //FOR DATA
    private Disposable disposable;
    // 2 - Declare list of users (GithubUser) & Adapter
    private List<GithubUser> githubUsers;
    private GithubUserAdapter adapter;

    public RecyclerFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        ButterKnife.bind(this, view);
        this.configureRecyclerView(); // - 4 Call during UI creation
        this.executeHttpRequestWithRetrofit(); // 5 - Execute stream after UI creation
        // 4 - Configure the SwipeRefreshLayout
        this.configureSwipeRefreshLayout();
        // 2 - Calling the method that configuring click on RecyclerView
        this.configureOnClickRecyclerView();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    // -----------------
    // ACTION
    // -----------------

    // 1 - Configure item click on RecyclerView
    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_main_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // 1 - Get user from adapter
                        GithubUser user = adapter.getUser(position);
                        // 2 - Show result in a Toast
                        Toast.makeText(getContext(), "You clicked on user : "+user.getLogin(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 2 - Configure the SwipeRefreshLayout
    private void configureSwipeRefreshLayout(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeHttpRequestWithRetrofit();
            }
        });
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    // 3 - Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView(){
        // 3.1 - Reset list
        this.githubUsers = new ArrayList<>();
        // 3.2 - Create adapter passing the list of users
        this.adapter = new GithubUserAdapter(
                this.githubUsers,
                Glide.with(this),
                this
        );
        // 3.3 - Attach the adapter to the recyclerview to populate items
        this.recyclerView.setAdapter(this.adapter);
        // 3.4 - Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // -------------------
    // HTTP (RxJAVA)
    // -------------------

    private void executeHttpRequestWithRetrofit(){
        this.disposable = GithubStreams.streamFetchUserFollowing("JakeWharton").subscribeWith(new DisposableObserver<List<GithubUser>>() {
            @Override
            public void onNext(List<GithubUser> users) {
                // 6 - Update RecyclerView after getting results from Github API
                updateUI(users);
            }

            @Override
            public void onError(Throwable e) { }

            @Override
            public void onComplete() { }
        });
    }

    private void disposeWhenDestroy(){
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    // -------------------
    // UPDATE UI
    // -------------------

    private void updateUI(List<GithubUser> users){
        // 3 - Stop refreshing and clear actual list of users
        swipeRefreshLayout.setRefreshing(false);
        githubUsers.clear();
        githubUsers.addAll(users);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClickDeleteButton(int position) {
        GithubUser user = adapter.getUser(position);
        Toast.makeText(getContext(), "You are trying to delete user : "+user.getLogin(), Toast.LENGTH_SHORT).show();
    }
}

