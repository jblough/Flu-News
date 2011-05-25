package com.josephblough.fluchallenge.tasks;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.transport.DataRetriever;
import com.josephblough.fluchallenge.data.Feed;

import android.os.AsyncTask;
import android.util.Log;

public class FluUpdatesFeedDownloaderTask extends AsyncTask<Void, Void, Feed> {

	private static final String TAG = "FluUpdatesFeedDownloaderTask";
	
	private ApplicationController app;
	
	public FluUpdatesFeedDownloaderTask(ApplicationController app) {
	    this.app = app;
	}
	
	@Override
	protected Feed doInBackground(Void... param) {
	    return DataRetriever.getFluUpdates();
	}

	protected void onPostExecute(Feed result) {
	    app.fluUpdatesFeed = result;
	}
}
