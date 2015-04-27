/*
 * Copyright (c) 2014 Rotunda Software. All rights reserved.
 */

package io.github.froger.instamaterial;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * Created by santiagomartinez on 25/12/14.
 */
public class SongArrangementFragment extends Fragment
        implements
        MediaPlayer.OnPreparedListener,
        SurfaceHolder.Callback {
    private VideoView videoSurface;
    private MediaController controller;
    private TextView nowPlaying;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.song_arrangement_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = new MediaController(getActivity());
        controller.setAnchorView(videoSurface);
        videoSurface = (VideoView) view.findViewById(R.id.videoSurface);
        videoSurface.setVideoURI(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"));
//        videoSurface.setVideoURI(Uri.parse(getResourceUrl()));
        videoSurface.setMediaController(controller);
        videoSurface.setOnPreparedListener(this);
        SurfaceHolder videoHolder = videoSurface.getHolder();

        nowPlaying = (TextView) view.findViewById(R.id.now_playing);
        nowPlaying.setText("big_buck_bunny.mp4");


        videoHolder.addCallback(this);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                controller.show();
                return false;
            }
        });

        videoSurface.start();
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_fade_out);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                nowPlaying.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        nowPlaying.startAnimation(a);

        controller.show();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }
}
