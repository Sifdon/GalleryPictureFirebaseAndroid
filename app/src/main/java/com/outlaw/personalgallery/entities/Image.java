/*
 *
 *   Developed by Ben Slama Jihed
 *   ben.slama.jihed@gmail.com
 *   https://github.com/benslamajihed
 *   Copyright (c) 2016
 *
 */

package com.outlaw.personalgallery.entities;

/**
 * Created by jihedbenslama on 19/03/2016.
 */
public class Image {
    String stringImage;
    String id;


    public Image() {
    }


    public Image(String stringImage) {
        this.stringImage = stringImage;
    }

    public Image(String stringImage, String id) {
        this.stringImage = stringImage;
        this.id = id;
    }

    public String getStringImage() {
        return stringImage;
    }

    public String getId() {
        return id;
    }
}
