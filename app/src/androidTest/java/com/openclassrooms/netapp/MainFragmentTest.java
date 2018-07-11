package com.openclassrooms.netapp;

import android.support.test.runner.AndroidJUnit4;

import com.openclassrooms.netapp.Utils.GithubStreams;
import com.openclassrooms.netapp.models.GithubUser;
import com.openclassrooms.netapp.models.GithubUserInfo;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by AppDevloop on 11/07/2018.
 */
@RunWith(AndroidJUnit4.class)
public class MainFragmentTest {

    @Test
    public void fetchUserFollowingTest() throws Exception {
        //1 - Get the stream
        Observable<List<GithubUser>> observableUsers = GithubStreams.streamFetchUserFollowing("JakeWharton");
        //2 - Create a new TestObserver
        TestObserver<List<GithubUser>> testObserver = new TestObserver<>();
        //3 - Launch observable
        observableUsers.subscribeWith(testObserver)
                .assertNoErrors() // 3.1 - Check if no errors
                .assertNoTimeout() // 3.2 - Check if no Timeout
                .awaitTerminalEvent(); // 3.3 - Await the stream terminated before continue

        // 4 - Get list of user fetched
        List<GithubUser> usersFetched = testObserver.values().get(0);

        // 5 - Verify if Jake Wharton follows only 12 users...
        assertThat("Jake Wharton follows only 12 users.",usersFetched.size() == 12);
    }

    @Test
    public void fetchUserInfosTest() throws Exception {
        Observable<GithubUserInfo> observableUser = GithubStreams.streamFetchUserInfos("JakeWharton");
        TestObserver<GithubUserInfo> testObserver = new TestObserver<>();

        observableUser.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();

        GithubUserInfo userInfo = testObserver.values().get(0);

        assertThat("Jake Wharton Github's ID is 66577.",userInfo.getId() == 66577);
    }
}
