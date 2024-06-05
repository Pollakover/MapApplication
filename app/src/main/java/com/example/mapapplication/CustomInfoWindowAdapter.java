package com.example.mapapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private LayoutInflater inflater;
    private ImageCache imageCache;
    private Context mContext;

    public CustomInfoWindowAdapter(Context context) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.imageCache = new ImageCache();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = inflater.inflate(R.layout.custom_info_window, null);
        renderWindowText(marker, view);
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void renderWindowText(Marker marker, View view) {
        String title = marker.getTitle();
        String snippet = marker.getSnippet();
        String imageUrl = (String) marker.getTag();

        TextView titleUi = view.findViewById(R.id.title);
        TextView snippetUi = view.findViewById(R.id.snippet);
        ImageView imageView = view.findViewById(R.id.image);

        titleUi.setText(title);
        snippetUi.setText(snippet);

        Bitmap cachedImage = imageCache.getImageFromCache(imageUrl);
        if (cachedImage != null) {
            imageView.setImageBitmap(cachedImage);
        } else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(imageUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            imageView.setImageBitmap(resource);
                            imageCache.addImageToCache(imageUrl, resource);
                            if (marker.isInfoWindowShown()) {
                                marker.hideInfoWindow();
                                marker.showInfoWindow();
                            }
                        }

                        @Override
                        public void onLoadCleared(Drawable placeholder) {
                        }
                    });
        }
    }
}