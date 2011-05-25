package com.josephblough.fluchallenge.tasks;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.transport.DataRetriever;
import com.josephblough.fluchallenge.data.SyndicatedFeed;

import android.os.AsyncTask;
import android.util.Log;

public class SyndicatedFeedDownloaderTask extends AsyncTask<Void, Void, SyndicatedFeed> {

	private static final String TAG = "XmlNewsFeedDownloaderTask";
	
	private ApplicationController app;
	private int topic;
	
	public SyndicatedFeedDownloaderTask(ApplicationController app, int topic) {
	    this.app = app;
	    this.topic = topic;
	}
	
	@Override
	protected SyndicatedFeed doInBackground(Void... param) {
	    Log.d(TAG, "Retrieving topic " + this.topic);
	    return DataRetriever.retrieveSyndicatedFeed(this.topic);
	}

	protected void onPostExecute(SyndicatedFeed result) {
	    app.syndicatedFeeds.put(this.topic, result);
	}
}
