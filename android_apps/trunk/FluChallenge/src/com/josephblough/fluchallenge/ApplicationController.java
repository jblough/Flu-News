package com.josephblough.fluchallenge;

import java.util.HashMap;
import java.util.Map;

import com.josephblough.fluchallenge.data.Feed;
import com.josephblough.fluchallenge.data.FluReport;
import com.josephblough.fluchallenge.data.SyndicatedFeed;

import android.app.Application;
import android.media.MediaPlayer;
import android.util.Log;

public class ApplicationController extends Application {

    private final static String TAG = "ApplicationController";

    public FluReport fluReport;
    public Feed fluUpdatesFeed;
    public Feed fluPodcastsFeed;
    public Map<Integer, SyndicatedFeed> syndicatedFeeds = new HashMap<Integer, SyndicatedFeed>();

    private MediaPlayer player = new MediaPlayer();

    public void playUrl(final String url) {
	try {
	    Log.d(TAG, "Downloading " + url);
	    player.setDataSource(url);
	    player.prepare();
	    player.start();
	}
	catch (Exception e) {
	    Log.e(TAG, e.getMessage(), e);
	}
    }
}
