package com.example.androidparsetagram.fragments;

import android.util.Log;

import com.example.androidparsetagram.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/*
This is a Fragment, but since PostsFragment contains the same code required,
this was added as a stand alone class extending PostsFragment
 */
public class ProfileFragment extends PostsFragment{
    private static final String TAG = "ProfileFragment";

    @Override
    protected void queryPosts() {
        super.queryPosts();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);//Will automatically retrieve the key/column

        //New to show only what current user has posted
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());

        query.setLimit(20);//Limit FOR THE WEAK

        //Orders elements in descending order by a key
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with retrieving posts", e);
                    return;
                }

                for (Post post: posts) {
                    Log.i(TAG, "Desc: " + post.getDescription() + ", user: " + post.getUser().getUsername());
                }
                //To fix errors with private values, make them protected!
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
