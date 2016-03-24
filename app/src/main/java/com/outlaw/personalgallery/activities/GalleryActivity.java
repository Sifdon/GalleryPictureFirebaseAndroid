
/*
 *
 *   Developed by Ben Slama Jihed
 *   ben.slama.jihed@gmail.com
 *   https://github.com/benslamajihed
 *   Copyright (c) 2016
 *
 */

package com.outlaw.personalgallery.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.outlaw.personalgallery.BuildConfig;
import com.outlaw.personalgallery.R;
import com.outlaw.personalgallery.adapter.GalleryRecycleViewAdapter;
import com.outlaw.personalgallery.entities.Image;
import com.outlaw.personalgallery.utils.Utils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    private final static String SAVED_ADAPTER_ITEMS = "SAVED_ADAPTER_ITEMS";
    private final static String SAVED_ADAPTER_KEYS = "SAVED_ADAPTER_KEYS";
    private static final int SELECT_PHOTO = 7;
    /* Data from the authenticated user */
    public AuthData mAuthData;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Image> images = new ArrayList<Image>();
    private ArrayList<String> mAdapterKeys;
    private Firebase mFirebaseRef;
    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;
    private FloatingActionButton _fabAddImage;
    private Toolbar _toolbar;
    private CoordinatorLayout _cordinatorLayout;
    private RecyclerView _mRecyclerView;

    private String mUsernameMail = BuildConfig.MAIL_User;
    private String mUsernamePwd = BuildConfig.PWD_User;
    private String urlFirebase = BuildConfig.FIREBASE_URL;
    private String child = "gallery";

    private String TAG = GalleryActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        handleInstanceState(savedInstanceState);
        _toolbar = (Toolbar) findViewById(R.id.toolbar_gallery);
        setSupportActionBar(_toolbar);
        setTitle("Gallery");
        _cordinatorLayout = (CoordinatorLayout) findViewById(R.id.cordinator_layout);
        _fabAddImage = (FloatingActionButton) findViewById(R.id.fab_add_image);
        _fabAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });
        (Snackbar.make(_cordinatorLayout, "Welcome to Experiemental Gallery", Snackbar.LENGTH_LONG)).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                    UploadImage(yourSelectedImage);
                }
        }
    }


    /**
     * ***************************************************************************
     */
    private Firebase settingFirebase(String urlFirebase, String child) {
        Firebase firebase = new Firebase(urlFirebase).child(child);

        firebase.authWithPassword(mUsernameMail, mUsernamePwd, new AuthResultHandler("password"));
        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                setAuthenticatedUser(authData);
            }
        };
        firebase.addAuthStateListener(mAuthStateListener);

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return firebase;

    }

    private void initRecycleViewVertical() {
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        _mRecyclerView = (RecyclerView) findViewById(R.id.recycle_vertical);
        _mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new GalleryRecycleViewAdapter(this, mFirebaseRef, Image.class, images, mAdapterKeys);
        _mRecyclerView.setNestedScrollingEnabled(true);
        _mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * ***************************************************************************
     */
    private void UploadImage(Bitmap bitmap) {
        // Create our 'model', a Chat object
        Image imageToUpload = new Image(Utils.bmpToString(bitmap));
        // Create a new, auto-generated child of that chat location, and save our chat data there
        mFirebaseRef.push().setValue(imageToUpload);
    }

    private void handleInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(SAVED_ADAPTER_ITEMS) &&
                savedInstanceState.containsKey(SAVED_ADAPTER_KEYS)) {
            mAdapterKeys = savedInstanceState.getStringArrayList(SAVED_ADAPTER_KEYS);
        } else {
            images = new ArrayList<Image>();
            mAdapterKeys = new ArrayList<String>();
        }
    }

    /**
     * ***************************************************************************
     */
    @Override
    public void onBackPressed() {
    }

    /**
     * ***************************************************************************
     */
    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseRef = settingFirebase(urlFirebase, child);
        initRecycleViewVertical();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(GalleryActivity.class.getSimpleName(), "onStop");
        Utils.deleteCache(this);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    /**
     * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.
     */
    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {
            /* Hide all the login buttons */

            /* show a provider specific status text */
            String name = null;
            if (authData.getProvider().equals("password")) {
                name = authData.getUid();
            } else {
                Log.e(TAG, "Invalid provider: " + authData.getProvider());
            }
            if (name != null) {
                Log.d(TAG, "Logged in as " + name + " (" + authData.getProvider() + ")");
            }
        } else {

        }
        this.mAuthData = authData;
    }

    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * ***************************************************************************
     */
    public class AuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;
        private Context context;
        private String TAG = AuthResultHandler.class.getSimpleName();


        public AuthResultHandler(String provider) {
            this.provider = provider;

        }

        @Override
        public void onAuthenticated(AuthData authData) {
            Log.i(TAG, provider + " auth successful");
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            showErrorDialog(firebaseError.toString());
        }
    }
}
