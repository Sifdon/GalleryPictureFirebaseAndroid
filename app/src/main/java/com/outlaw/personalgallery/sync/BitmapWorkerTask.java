/*
 *
 *   Developed by Ben Slama Jihed
 *   ben.slama.jihed@gmail.com
 *   https://github.com/benslamajihed
 *   Copyright (c) 2016
 *
 */

package com.outlaw.personalgallery.sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.outlaw.personalgallery.utils.Utils;

import java.lang.ref.WeakReference;

/**
 * Created by jihedbenslama on 20/03/2016.
 */
public class BitmapWorkerTask extends AsyncTask<String, Integer, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    ProgressDialog progress;
    Context context;
    private String data;

    public BitmapWorkerTask(Context context, ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = ProgressDialog.show(context, "Picture",
                "Loading...", true);
        progress.show();
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... params) {
        data = params[0];
        Bitmap bitmap = Utils.stringToBmp(data);
        Utils.addBitmapToMemoryCache(data, bitmap);
        return bitmap;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {


        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
        progress.dismiss();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progress.setProgress(values[0]);
    }
}
