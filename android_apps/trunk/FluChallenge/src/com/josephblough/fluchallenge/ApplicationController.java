package com.josephblough.fluchallenge;

import java.util.HashMap;
import java.util.Map;

import com.josephblough.fluchallenge.data.Feed;
import com.josephblough.fluchallenge.data.FluReport;
import com.josephblough.fluchallenge.data.SyndicatedFeed;

import android.app.Application;

public class ApplicationController extends Application {

	public FluReport fluReport;
	public Feed fluUpdatesFeed;
	public Feed fluPodcastsFeed;
	public Map<Integer, SyndicatedFeed> syndicatedFeeds = new HashMap<Integer, SyndicatedFeed>();
}
