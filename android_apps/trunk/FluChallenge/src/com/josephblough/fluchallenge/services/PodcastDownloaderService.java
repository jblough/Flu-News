package com.josephblough.fluchallenge.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class PodcastDownloaderService extends IntentService implements MediaScannerConnectionClient {

    private final static String TAG = "PodcastDownloaderService";

    public static final int NOTIFICATION = 1112;

    public static final String EXTRA_URL = "PodcastDownloaderService.url";
    public static final String EXTRA_TITLE = "PodcastDownloaderService.title";
    
    private MediaScannerConnection scanner = null;
    private String fileToScan = null;

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
	    File podcastDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS);
	    podcastDirectory.mkdirs();
	    File outputFile = new File(podcastDirectory, filename);
	    //Log.d(TAG, "Saving " + url + " to " + outputFile.getAbsolutePath());

	    URL u = new URL(url);
	    HttpURLConnection c = (HttpURLConnection) u.openConnection();
	    c.setRequestMethod("GET");
	    c.setDoOutput(true);
	    c.connect();
	    FileOutputStream f = new FileOutputStream(outputFile);
	    InputStream in = c.getInputStream();

	    try {
		byte[] buffer = new byte[1024];
		int len = 0;
		while ( (len = in.read(buffer)) != -1 ) {
		    f.write(buffer,0, len);
		}
	    }
	    finally {
		f.flush();
		f.close();
	    }

	    int notificationId = 1;
	    try {
		notificationId = Integer.parseInt(podcastId);
	    }
	    catch (NumberFormatException e) {
		
	    }
	    
	    this.fileToScan = outputFile.getAbsolutePath();
	    this.scanner = new MediaScannerConnection(getApplication(), this);
	    scanner.connect();
	    
	    sendNotification(notificationId, title);
	}
	catch (Exception e) {
	    Log.e(TAG, e.getMessage(), e);
	}
    }

    private void sendNotification(final int id, final String title) {
	NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	Notification notification = new Notification(
		android.R.drawable.ic_btn_speak_now, "Finished downloading",
		System.currentTimeMillis());
	PendingIntent notifier = PendingIntent.getActivity(this, 0, new Intent(
		this, com.josephblough.fluchallenge.activities.NotificationActivity.class), 0);
	notification.setLatestEventInfo(this, "Download complete", "Downloaded "
		+ title, notifier);
	notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
	mgr.notify(NOTIFICATION + id, notification);
    }

    public void onMediaScannerConnected() {
	this.scanner.scanFile(this.fileToScan, "audio/mpeg");
    }

    public void onScanCompleted(String path, Uri uri) {
	    if (this.scanner.isConnected()) {
		//Log.d(TAG, "onScanCompleted - Disconnecting");
		this.scanner.disconnect();
	    }
    }
}
