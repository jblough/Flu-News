package com.josephblough.fluchallenge.tasks;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.transport.DataRetriever;
import com.josephblough.fluchallenge.data.Feed;

import android.os.AsyncTask;
import android.util.Log;

public class FluPodcastsFeedDownloaderTask extends AsyncTask<Void, Void, Feed> {

	private static final String TAG = "FluPodcastsFeedDownloaderTask";
	
	private ApplicationController app;
	
	public FluPodcastsFeedDownloaderTask(ApplicationController app) {
	    this.app = app;
	}
	
	@Override
	protected Feed doInBackground(Void... param) {
	    return DataRetriever.getFluPodcasts();
	}

	protected void onPostExecute(Feed result) {
	    app.fluPodcastsFeed = result;
	}
}
