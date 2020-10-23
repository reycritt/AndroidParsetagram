package com.example.androidparsetagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidparsetagram.Post;
import com.example.androidparsetagram.PostsAdapter;
import com.example.androidparsetagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment {
    private static final String TAG = "PostsFragment";
    private RecyclerView rvPosts;
    protected PostsAdapter adapter;
    protected List<Post> allPosts;

    public PostsFragment () {
        //Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.rvPosts);
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts);

        //Creating recyclerview:
        //0. create layout for one row (see item_post.xml)
        //1. create adapter (see PostsAdapter and PostsAdapter adapter)
        //2. Create data source
        //3. set adapter on recyclerview
        rvPosts.setAdapter(adapter);
        //4. Set layout manager
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));



        queryPosts();
    }

    //Accessible by classes in same package, ie for inheritance
    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);//Will automatically retrieve the key/column
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
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}