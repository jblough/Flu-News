package com.josephblough.fluchallenge.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.josephblough.fluchallenge.R;
import com.josephblough.fluchallenge.activities.NotificationActivity;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;

public class PodcastDownloaderService extends IntentService {

    private final static String TAG = "PodcastDownloaderService";

    public static final int NOTIFICATION = 1112;

    public static final String EXTRA_URL = "PodcastDownloaderService.url";
    public static final String EXTRA_TITLE = "PodcastDownloaderService.title";
    
    public PodcastDownloaderService() {
	super("PodcastDownloaderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
	Log.d(TAG, "PodcastDownloaderService started");

	String url = intent.getStringExtra(EXTRA_URL);
	String title = intent.getStringExtra(EXTRA_TITLE);

	String podcastId = url.substring(url.lastIndexOf('=')+1);
	String filename = podcastId + ".mp3";

	try {
	    //File podcastDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS);
	    File podcastDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + File.separator + "CDC");
	    podcastDirectory.mkdirs();
	    File outputFile = new File(podcastDirectory, filename);
	    //Log.d(TAG, "Saving " + url + " to " + outputFile.getAbsolutePath());
	    String savedAudioFilename = outputFile.getAbsolutePath();

	    // Create a notification to the user
	    int notificationId = 1;
	    try {
		notificationId = Integer.parseInt(podcastId);
	    }
	    catch (NumberFormatException e) {
	    }
	    notificationId += NOTIFICATION;
	    
	    final Notification notification = new Notification(R.drawable.stat_sys_download_anim0, "Downloading " + title, System.currentTimeMillis());
	    notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_AUTO_CANCEL;
	    notification.contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.download_with_progress);
	    Intent i = new Intent(this, NotificationActivity.class);
	    i.putExtra(NotificationActivity.EXTRA_FILE_URL, savedAudioFilename);
	    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
	    notification.contentIntent = pendingIntent;
	    notification.contentView.setImageViewResource(R.id.download_status_icon, R.drawable.status_bar_icon);
	    notification.contentView.setTextViewText(R.id.download_status_text, "Downloading " + title);
	    notification.contentView.setProgressBar(R.id.download_status_progress, 100, 0, false);
	    final NotificationManager mgr = (NotificationManager) getApplicationContext().
	    	getSystemService(NOTIFICATION_SERVICE);
	    mgr.notify(notificationId, notification);
	    
	    URL u = new URL(url);
	    HttpURLConnection c = (HttpURLConnection) u.openConnection();
	    c.setRequestMethod("GET");
	    c.setDoOutput(true);
	    c.connect();
	    int downloadSize = c.getContentLength();
	    Log.d(TAG, "Download size: " + downloadSize);
	    FileOutputStream f = new FileOutputStream(outputFile);
	    InputStream in = c.getInputStream();

	    try {
		byte[] buffer = new byte[1024];
		int len = 0;
		int totalBytesRead = 0;
		int lastPercentReported = 0;
		while ( (len = in.read(buffer)) != -1 ) {
		    f.write(buffer,0, len);
		    totalBytesRead += len;
		    
		    int percentComplete = (int)(((float)totalBytesRead / (float)downloadSize) * 100);
		    if (percentComplete > lastPercentReported + 4) { // Don't update for every chunk
			lastPercentReported = percentComplete;
			//notification.contentView.setTextViewText(R.id.download_status_text, "Downloading: " + percentComplete + "%");
			notification.contentView.setProgressBar(R.id.download_status_progress, 100, percentComplete, false);
			mgr.notify(notificationId, notification);
		    }
		}
		
		//notification.contentView.setTextViewText(R.id.download_status_text, "Downloading: 100%");
		notification.contentView.setProgressBar(R.id.download_status_progress, 100, 100, false);
		mgr.notify(notificationId, notification);
	    }
	    finally {
		f.flush();
		f.close();
	    }

	    MediaScannerConnection.scanFile(getApplication(), new String[] { savedAudioFilename }, null, null);
	    mgr.cancel(notificationId);
	    
	    sendNotification(notificationId, title, savedAudioFilename);
	}
	catch (Exception e) {
	    Log.e(TAG, e.getMessage(), e);
	}
    }

    private void sendNotification(final int id, final String title, final String filename) {
	NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	Notification notification = new Notification(R.drawable.status_bar_icon, "Finished downloading", System.currentTimeMillis());
	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("file://" + filename));

	PendingIntent notifier = PendingIntent.getActivity(this, 0, intent, 0);
	notification.setLatestEventInfo(this, "Download complete", "Downloaded " + title, notifier);
	notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
	mgr.notify(id, notification);
    }
}
