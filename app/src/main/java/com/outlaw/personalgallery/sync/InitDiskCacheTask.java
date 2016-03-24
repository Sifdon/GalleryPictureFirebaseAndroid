/*
 *
 *   Developed by Ben Slama Jihed
 *   ben.slama.jihed@gmail.com
 *   https://github.com/benslamajihed
 *   Copyright (c) 2016
 *
 */

package com.outlaw.personalgallery.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.outlaw.personalgallery.utils.DiskLruCache;

import java.io.File;
import java.io.IOException;

/**
 * Created by jihedbenslama on 20/03/2016.
 */
public class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";
    private final Object mDiskCacheLock = new Object();
    Context context;
    // Initialize disk cache on background thread
    private DiskLruCache mDiskLruCache;
    private boolean mDiskCacheStarting = true;
    private String TAG = InitDiskCacheTask.class.getSimpleName();

    public InitDiskCacheTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(File... params) {
        synchronized (mDiskCacheLock) {
            File cacheDir = params[0];
            try {
                mDiskLruCache = DiskLruCache.open(
                        cacheDir, 1, 1, DISK_CACHE_SIZE);
            } catch (final IOException e) {
                Log.e(TAG, "initDiskCache - " + DISK_CACHE_SIZE + e);
            }
            mDiskCacheStarting = false; // Finished initialization
            mDiskCacheLock.notifyAll(); // Wake any waiting threads
        }
        return null;


    }
}