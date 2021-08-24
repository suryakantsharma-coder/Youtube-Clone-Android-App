package com.smart.utube.Activity;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.github.kiulian.downloader.OnYoutubeDownloadListener;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.VideoDetails;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.github.kiulian.downloader.model.formats.AudioFormat;
import com.github.kiulian.downloader.model.formats.AudioVideoFormat;
import com.github.kiulian.downloader.model.formats.Format;
import com.github.kiulian.downloader.model.quality.AudioQuality;
import com.smart.utube.R;

import java.io.File;
import java.io.IOException;
import java.io.NotActiveException;
import java.util.List;
import java.util.concurrent.Future;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class DownloadAudioAndVideo extends AppCompatActivity {
    YoutubeVideo video ;
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManagerCompat;
    Context context;
    boolean permission = false;
    int max = 100;
    int progress = 0;
    String error = "Start Download";

    public void AudioDownloader(Context context,String Videoid) {
        this.context = context;
        // init downloader
        YoutubeDownloader downloader = new YoutubeDownloader();
        // downloader configurations
        downloader.setParserRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        downloader.setParserRetryOnFailure(1);

        // parsing data
        String videoId = "0JJIl44hBBQ"; // for url https://www.youtube.com/watch?v=abc12345


        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    video = downloader.getVideo(Videoid);
                } catch (YoutubeException e) {
                    e.printStackTrace();
                }

                // video details
                if (video != null) {
                    VideoDetails details = video.details();
                    System.out.println(details.title());
                    System.out.println(details.viewCount());
                    details.thumbnails().forEach(image -> System.out.println("Thumbnail: " + image));

//                // get videos with audio
//                List<AudioVideoFormat> videoWithAudioFormats = video.videoWithAudioFormats();
//                videoWithAudioFormats.forEach(it -> {
//                    System.out.println(it.audioQuality() + " : " + it.url());
//                });


                    List<AudioFormat> audioFormats = video.audioFormats();
                    audioFormats.forEach(it -> {
                                System.out.println(it.audioQuality() + " : " + it.url());
                            }

                    );

                    Format formatByItag = video.findFormatByItag(136);
                    if (formatByItag != null) {
                        System.out.println(formatByItag.url());
                    }

                    if (audioFormats.size() > 0) {
                        downloadAudio(audioFormats);
                        //ShowNotification(details.title());
                    }

                    // async downloading with callback

                } else {
                    error = "Audio Is Not Available For Download";
                    getResponse();
                }

            }

        });

        thread.start();

    }

    public void  VideoDownloader(Context context,String Videoid) {
        this.context = context;


        // init downloader
        YoutubeDownloader downloader = new YoutubeDownloader();
        // downloader configurations
        downloader.setParserRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        downloader.setParserRetryOnFailure(1);

        // parsing data
        String videoId = "0JJIl44hBBQ"; // for url https://www.youtube.com/watch?v=abc12345


        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    video = downloader.getVideo(Videoid);
                } catch (YoutubeException e) {
                    e.printStackTrace();
                }

                // video details

                if (video != null) {
                    VideoDetails details = video.details();
                    System.out.println(details.title());
                    System.out.println(details.viewCount());
                    details.thumbnails().forEach(image -> System.out.println("Thumbnail: " + image));

                    // get videos with audio
                    List<AudioVideoFormat> videoWithAudioFormats = video.videoWithAudioFormats();
                    videoWithAudioFormats.forEach(it -> {
                        System.out.println(it.audioQuality() + " : " + it.url());
                    });


                    Format formatByItag = video.findFormatByItag(136);
                    if (formatByItag != null) {
                        System.out.println(formatByItag.url());
                    }


                    File outputDir = new File(Environment.getExternalStorageDirectory().toString() + "/Youtube_Videos");

                    if (videoWithAudioFormats.size() > 0) {
                        downloadVideo(videoWithAudioFormats);
                        //ShowNotification(details.title());
                    }

                    // async downloading with callback

                } else {
                    error = "Video Is Not Available For Download";
                    getResponse();
                }

            }
        });

        thread.start();
    }

    private void downloadAudio (List<AudioFormat> format){
        File outputDir = new File(Environment.getExternalStorageDirectory().toString()+"/UTUBE/Music");

        try {
            Future<File> future = video.downloadAsync(format.get(0), outputDir, new OnYoutubeDownloadListener() {
                @Override
                public void onDownloading(int progress) {
                    System.out.printf("Downloaded %d%%\n", progress);
                }

                @Override
                public void onFinished(File file) {
                    System.out.println("Finished file: " + file);
                }

                @Override
                public void onError(Throwable throwable) {
                    System.out.println("Error: " + throwable.getLocalizedMessage());
                }
            });

            if (future.isDone()){
                System.out.println("Done Download");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (YoutubeException e) {
            e.printStackTrace();
        }

    }

    private void downloadVideo (List<AudioVideoFormat> format){
        File outputDir = new File(Environment.getExternalStorageDirectory().toString()+"/uTube_Downloads/Videos");

        try {
            Future<File> future = video.downloadAsync(format.get(0), outputDir, new OnYoutubeDownloadListener() {
                @Override
                public void onDownloading(int progress) {
                    System.out.printf("Downloaded %d%%\n", progress);
                }

                @Override
                public void onFinished(File file) {
                    System.out.println("Finished file: " + file);
                }

                @Override
                public void onError(Throwable throwable) {
                    System.out.println("Error: " + throwable.getLocalizedMessage());
                }
            });

            if (future.isDone()){
                System.out.println("Done Download");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (YoutubeException e) {
            e.printStackTrace();
        }

    }

    private void  ShowNotification(String title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("myNotification","myNotification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

         builder = new NotificationCompat.Builder(getApplicationContext(),"myNotification")
                .setContentText(title)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true)
                 .setOngoing(true)
                 .setOnlyAlertOnce(true)
                 .setPriority(NotificationCompat.PRIORITY_LOW)
                .setProgress(100, progress, false);

        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(999,builder.build());
    }

    public String getResponse() {
        return error;
    }
}

