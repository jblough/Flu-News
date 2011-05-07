package com.josephblough.fluchallenge.services;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.data.Feed;
import com.josephblough.fluchallenge.transport.DataRetriever;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class FluPodcastsFeedDownloaderService extends IntentService {

    private final static String TAG = "FluPodcastsFeedDownloaderService";

    public static final String EXTRA_MESSENGER = 
	"FluPodcastsFeedDownloaderService";
    
    public FluPodcastsFeedDownloaderService() {
	super("FluPodcastsFeedDownloaderService");
    }
    
    @Override
    protected void onHandleIntent(Intent intent) {

	int result = Activity.RESULT_CANCELED;
	Feed feed = null;
	
	feed = DataRetriever.getFluPodcasts();
	if (feed != null && feed.title != null && !feed.title.equals("")) {
	    ((ApplicationController)getApplicationContext()).fluPodcastsFeed = feed;
	    result = Activity.RESULT_OK;
	}
	
	Bundle extras = intent.getExtras();
	
	if (extras != null) {
	    Messenger messenger = (Messenger) extras.get(EXTRA_MESSENGER);
	    if (messenger != null) {
		Message message = Message.obtain();
		message.arg1 = result;
		
		try {
		    messenger.send(message);
		} catch (android.os.RemoteException e) {
		    Log.e(TAG, e.getMessage(), e);
		}
	    }
	}
    }

}
