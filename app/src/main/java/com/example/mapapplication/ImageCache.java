package com.example.mapapplication;

import android.graphics.Bitmap;
import android.util.LruCache;

public class ImageCache {
    private LruCache<String, Bitmap> mCache;

    public ImageCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mCache = new LruCache<>(cacheSize);
    }

    public void addImageToCache(String key, Bitmap image) {
        if (getImageFromCache(key) == null) {
            mCache.put(key, image);
        }
    }

    public Bitmap getImageFromCache(String key) {
        return mCache.get(key);
    }
}