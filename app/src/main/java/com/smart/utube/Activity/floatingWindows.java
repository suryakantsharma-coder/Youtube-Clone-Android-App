package com.smart.utube.Activity;

import androidx.appcompat.app.AppCompatActivity;
import io.hamed.floatinglayout.FloatingLayout;
import io.hamed.floatinglayout.callback.FloatingListener;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.smart.utube.R;

import org.jetbrains.annotations.NotNull;

public class floatingWindows extends AppCompatActivity {
    private String VideoId;
    private ImageView btn;
    private Float time;
    private FloatingLayout floatingLayout;
    private FloatingListener floatingListener = new FloatingListener() {
        @Override
        public void onCreateListener(View view) {
            btn = view.findViewById(R.id.btn_close);
            YouTubePlayerView player = view.findViewById(R.id.youtubePlayerFloatingWIndows);
            loadVideo(VideoId, player);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    floatingLayout.destroy();
                }
            });
        }

        @Override
        public void onCloseListener() {
            Toast.makeText(getApplicationContext(), "Close", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating_windows);

        getSupportActionBar().hide();

        VideoId = getIntent().getStringExtra("VideoId");
        time = getIntent().getFloatExtra("SkipTime",0f);

        Toast.makeText(getApplicationContext(), ""+time,Toast.LENGTH_SHORT).show();
        ActicvateWindow();

    }

    public void ActicvateWindow() {

        if (!isNeedPermission())
            showFloating();

        if (isNeedPermission())
            requestPermission();
    }

    private boolean isNeedPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this);
    }

    private void requestPermission() {
        Intent intent = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName())
        );
        startActivityForResult(intent, 25);
    }

    private void showFloating() {
        floatingLayout = new FloatingLayout(this, R.layout.sample_layout);
        floatingLayout.setFloatingListener(floatingListener);
        floatingLayout.create();
    }

    private void loadVideo(String videoId, YouTubePlayerView youTubePlayerView) {

        youTubePlayerView.addYouTubePlayerListener(new YouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId , time);
                youTubePlayer.play();
            }

            @Override
            public void onStateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerState playerState) {

            }

            @Override
            public void onPlaybackQualityChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackQuality playbackQuality) {

            }

            @Override
            public void onPlaybackRateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackRate playbackRate) {

            }

            @Override
            public void onError(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerError playerError) {

            }

            @Override
            public void onCurrentSecond(@NotNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoDuration(@NotNull YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoLoadedFraction(@NotNull YouTubePlayer youTubePlayer, float v) {
                    btn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onVideoId(@NotNull YouTubePlayer youTubePlayer, @NotNull String s) {

            }

            @Override
            public void onApiChange(@NotNull YouTubePlayer youTubePlayer) {

            }
        });


//        binding.youtubePlayer.addYouTubePlayerListener(object : YouTubePlayerListener {
//            override fun onApiChange(youTubePlayer: YouTubePlayer) {
//
//            }
//
//            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
//
//            }
//
//            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
//
//            }
//
//            override fun onPlaybackQualityChange(
//                    youTubePlayer: YouTubePlayer, playbackQuality: PlayerConstants.PlaybackQuality
//            ) {
//
//            }
//
//            override fun onPlaybackRateChange(
//                    youTubePlayer: YouTubePlayer,
//                    playbackRate: PlayerConstants.PlaybackRate
//            ) {
//
//            }
//
//            override fun onReady(youTubePlayer: YouTubePlayer) {
//                youtubeplayer = youTubePlayer
//                youTubePlayer.loadVideo(videoId, 0f)
//                youTubePlayer.play()
//            }
//
//            override fun onStateChange(
//                    youTubePlayer: YouTubePlayer,
//                    state: PlayerConstants.PlayerState
//            ) {
//
//            }
//
//            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
//
//            }
//
//            override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
//
//            }
//
//            override fun onVideoLoadedFraction(
//                    youTubePlayer: YouTubePlayer,
//                    loadedFraction: Float
//            ) {
//                backIsAvailable = true
//            }
//
//        })
    }
}