
/*
 *
 *   Developed by Ben Slama Jihed
 *   ben.slama.jihed@gmail.com
 *   https://github.com/benslamajihed
 *   Copyright (c) 2016
 *
 */

package com.outlaw.personalgallery;

import android.app.Application;

import com.firebase.client.Firebase;
import com.outlaw.personalgallery.sync.InitDiskCacheTask;
import com.outlaw.personalgallery.utils.Utils;

import java.io.File;

/**
 * Created by jihedbenslama on 19/03/2016.
 */
public class Myapplication extends Application {
    private static final String DISK_CACHE_SUBDIR = "thumbnails";

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        Firebase gallery = new Firebase(BuildConfig.FIREBASE_URL + "gallery");
        gallery.keepSynced(true);
        File cacheDir = Utils.getDiskCacheDir(this, DISK_CACHE_SUBDIR);
        new InitDiskCacheTask(this).execute(cacheDir);
        Utils.settingCache();
    }
}
