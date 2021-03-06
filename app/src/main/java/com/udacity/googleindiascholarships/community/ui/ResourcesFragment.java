package com.udacity.googleindiascholarships.community.ui;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.udacity.googleindiascholarships.R;
import com.udacity.googleindiascholarships.community.ui.adapter.BlogAdapter;
import com.udacity.googleindiascholarships.community.ui.entities.ExternalLinks;
import com.udacity.googleindiascholarships.utils.Constants;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResourcesFragment extends Fragment {


    private RecyclerView resourcesRecyclerView;
    private ArrayList<ExternalLinks> resourcesLinks;
    private BlogAdapter blogAdapter;

    ProgressBar mProgressBar;

    public ResourcesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_blog, container, false);
        resourcesRecyclerView = rootView.findViewById(R.id.blogs_recyclerView);
        mProgressBar = rootView.findViewById(R.id.progress_barBlog);
        resourcesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        resourcesLinks = new ArrayList<ExternalLinks>();

        if(checkInternetConnectivity()){
            readFromFirebase();
            blogAdapter = new BlogAdapter(getContext(),resourcesLinks);
            resourcesRecyclerView.setAdapter(blogAdapter);
            mProgressBar.setVisibility(View.GONE);
        }else{
            Toast.makeText(getContext(),"No internet connection",Toast.LENGTH_LONG).show();

        }




        return rootView;
    }

    private void readFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.DATABASE_URL);
        DatabaseReference mExternalResourcesRef = database.getReference("external_resources").child("resources");
        mExternalResourcesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                resourcesLinks.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    ExternalLinks currentResource = postSnapshot.getValue(ExternalLinks.class);
                    resourcesLinks.add(currentResource);
                }

                blogAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("TAG", "onDataChange: "+databaseError.getMessage());

                Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
            }

        });

    }

    public boolean checkInternetConnectivity(){
        //Check internet connection//
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network//
        NetworkInfo netInformation = connectivityManager.getActiveNetworkInfo();
        // If there is a network connection, then fetch data//
        if(netInformation!=null && netInformation.isConnected()){
            return true;
        }else{
            return false;
        }
    }


}
