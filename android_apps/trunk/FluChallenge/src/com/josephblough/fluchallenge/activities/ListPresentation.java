package com.josephblough.fluchallenge.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.data.Feed;
import com.josephblough.fluchallenge.data.SyndicatedFeed;
import com.josephblough.fluchallenge.services.SyndicatedFeedDownloaderService;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class ListPresentation extends ListActivity implements OnItemSelectedListener, OnItemClickListener {

    private final static String TAG = "ListPresentation";
    private final static String FLU_ACTIVIY_TITLE = "Flu Activity Report"; 
    private final static String FLU_UPDATES_TITLE = "Flu Updates"; 
    private final static String FLU_PODCASTS_TITLE = "Flu Podcasts"; 
    private final static String FLU_PAGES_TITLE = "Flu Pages"; 
    private final static String CDC_FEATURE_TITLE = "CDC Feature Pages"; 
    
    private List<String> feeds = new ArrayList<String>();
    
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	getListView().setOnItemSelectedListener(this);
	getListView().setOnItemClickListener(this);
	
	loadFeeds();
	populateList();
    }

    public void loadFeeds() {
	// Flu activity report
	Intent intent = new Intent(this, FluActivity.class);
	startService(intent);
	feeds.add(FLU_ACTIVIY_TITLE);
	
	// Flu updates
	intent = new Intent(this, FluUpdates.class);
	startService(intent);
	feeds.add(FLU_UPDATES_TITLE);
	
	// Flu podcasts
	intent = new Intent(this, FluPodcasts.class);
	startService(intent);
	feeds.add(FLU_PODCASTS_TITLE);
	
	// Flu pages
	intent = new Intent(this, SyndicatedFeedDownloaderService.class);
	intent.putExtra(SyndicatedFeedDownloaderService.TOPIC_ID, SyndicatedFeed.FLU_PAGES_TOPIC_ID);
	startService(intent);
	feeds.add(FLU_PAGES_TITLE);
	
	// CDC pages
	intent = new Intent(this, SyndicatedFeedDownloaderService.class);
	intent.putExtra(SyndicatedFeedDownloaderService.TOPIC_ID, SyndicatedFeed.CDC_PAGES_TOPIC_ID);
	startService(intent);
	feeds.add(CDC_FEATURE_TITLE);
    }
    
    public void populateList() {
	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, feeds);
	setListAdapter(adapter);
    }
    
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	Log.d(TAG, "onItemClick: " + feeds.get(position));
	if (FLU_ACTIVIY_TITLE.equals(feeds.get(position))) {
	    ApplicationController app = (ApplicationController)getApplicationContext();
	    if (app.fluReport == null)
		Toast.makeText(this, FLU_ACTIVIY_TITLE + " still loading...", Toast.LENGTH_LONG).show();
	    else {
		Intent intent = new Intent(this, FluActivity.class);
		startActivity(intent);
	    }
	}
	else {
	    Integer feed = getFeed(position);
	    if (feed != null) {
		Intent intent = new Intent(this, GenericFeedActivity.class);
		intent.putExtra(GenericFeedActivity.FEED, feed);
		startActivity(intent);
	    }
	}
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	Log.d(TAG, "onItemSelected: " + feeds.get(position));
    }

    public void onNothingSelected(AdapterView<?> parent) {
	// TODO Auto-generated method stub
    }
    
    private Integer getFeed(final int position) {
	final String title = feeds.get(position);
	if (title == null) {
	    return null;
	}

	ApplicationController app = (ApplicationController)getApplicationContext();
	
	if (FLU_ACTIVIY_TITLE.equals(title)) {
	    if (app.fluReport == null)
		Toast.makeText(this, FLU_ACTIVIY_TITLE + " still loading...", Toast.LENGTH_LONG).show();
	    //else
		//return app.fluReport;
	}
	else if (FLU_UPDATES_TITLE.equals(title)) {
	    if (app.fluUpdatesFeed == null)
		Toast.makeText(this, FLU_UPDATES_TITLE + " still loading...", Toast.LENGTH_LONG).show();
	    else
		return Feed.FEED_FLU_UPDATES;
	}
	else if (FLU_PODCASTS_TITLE.equals(title)) {
	    if (app.fluUpdatesFeed == null)
		Toast.makeText(this, FLU_PODCASTS_TITLE + " still loading...", Toast.LENGTH_LONG).show();
	    else
		return Feed.FEED_FLU_PODCASTS;
	}
	else if (FLU_PAGES_TITLE.equals(title)) {
	    if (app.syndicatedFeeds.get(SyndicatedFeed.FLU_PAGES_TOPIC_ID) == null)
		Toast.makeText(this, FLU_PAGES_TITLE + " still loading...", Toast.LENGTH_LONG).show();
	    else
		return Feed.FEED_FLU_PAGES;
	}
	else if (CDC_FEATURE_TITLE.equals(title)) {
	    if (app.syndicatedFeeds.get(SyndicatedFeed.CDC_PAGES_TOPIC_ID) == null)
		Toast.makeText(this, CDC_FEATURE_TITLE + " still loading...", Toast.LENGTH_LONG).show();
	    else
		return Feed.FEED_CDC_PAGES;
	}
	else {
	    for (Entry<Integer, SyndicatedFeed> entry : app.syndicatedFeeds.entrySet()) {
		if (title.equals(entry.getValue().title)) {
		    return entry.getKey();
		}
	    }
	}
	
	return null;
    }
}
