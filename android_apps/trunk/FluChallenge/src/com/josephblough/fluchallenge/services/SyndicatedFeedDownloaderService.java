package com.josephblough.fluchallenge.services;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.data.SyndicatedFeed;
import com.josephblough.fluchallenge.transport.DataRetriever;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class SyndicatedFeedDownloaderService extends IntentService {

    private final static String TAG = "SyndicatedFeedDownloaderService";

    public static final String EXTRA_MESSENGER = "SyndicatedFeedDownloaderService";
    public static final String TOPIC_ID = "SyndicatedFeedDownloaderService.topic";
    
    public SyndicatedFeedDownloaderService() {
	super("SyndicatedFeedDownloaderService");
    }
    
    @Override
    protected void onHandleIntent(Intent intent) {

	int result = Activity.RESULT_CANCELED;
	SyndicatedFeed feed = null;

	int topic = intent.getIntExtra(TOPIC_ID, SyndicatedFeed.FLU_PAGES_TOPIC_ID);
	
	feed = DataRetriever.retrieveSyndicatedFeed(topic);
	if (feed != null && feed.title != null && !feed.title.equals("")) {
	    ((ApplicationController)getApplicationContext()).syndicatedFeeds.put(topic, feed);
	    result = Activity.RESULT_OK;
	}
	
	Bundle extras = intent.getExtras();
	
	if (extras != null) {
	    Messenger messenger = (Messenger) extras.get(EXTRA_MESSENGER);
	    if (messenger != null) {
		Message message = Message.obtain();
		message.arg1 = result;
		
		Bundle bundle = new Bundle();
		bundle.putInt(TOPIC_ID, topic);
		message.setData(bundle);
		
		try {
		    messenger.send(message);
		} catch (android.os.RemoteException e) {
		    Log.e(TAG, e.getMessage(), e);
		}
	    }
	}
    }

}
