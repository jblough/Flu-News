package com.josephblough.fluchallenge.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.R;
import com.josephblough.fluchallenge.data.Feed;
import com.josephblough.fluchallenge.data.SyndicatedFeed;
import com.josephblough.fluchallenge.services.FluActivityReportDownloaderService;
import com.josephblough.fluchallenge.services.FluPodcastsFeedDownloaderService;
import com.josephblough.fluchallenge.services.FluUpdatesFeedDownloaderService;
import com.josephblough.fluchallenge.services.SyndicatedFeedDownloaderService;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    
    private final String ERROR_MSG = "There was an error downloading the %s feed";
    
    private List<String> feeds = new ArrayList<String>();
    
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	getListView().setOnItemSelectedListener(this);
	getListView().setOnItemClickListener(this);
	
	populateList();
	loadFeeds();
    }

    public void loadFeeds() {
	// Flu activity report
	Intent intent = new Intent(this, FluActivityReportDownloaderService.class);
	intent.putExtra(FluActivityReportDownloaderService.EXTRA_MESSENGER,
		new Messenger(new Handler() {
		    @Override
		    public void handleMessage(Message message) {
			if (message.arg1 == Activity.RESULT_OK) {
			    done();
			} else {
			    error(FLU_ACTIVIY_TITLE);
			}
		    }
		}));
	startService(intent);
	
	// Flu updates
	intent = new Intent(this, FluUpdatesFeedDownloaderService.class);
	intent.putExtra(FluUpdatesFeedDownloaderService.EXTRA_MESSENGER,
		new Messenger(new Handler() {
		    @Override
		    public void handleMessage(Message message) {
			if (message.arg1 == Activity.RESULT_OK) {
			    done();
			} else {
			    error(FLU_ACTIVIY_TITLE);
			}
		    }
		}));
	startService(intent);
	
	// Flu podcasts
	intent = new Intent(this, FluPodcastsFeedDownloaderService.class);
	intent.putExtra(FluPodcastsFeedDownloaderService.EXTRA_MESSENGER,
		new Messenger(new Handler() {
		    @Override
		    public void handleMessage(Message message) {
			if (message.arg1 == Activity.RESULT_OK) {
			    done();
			} else {
			    error(FLU_ACTIVIY_TITLE);
			}
		    }
		}));
	startService(intent);
	
	// Flu pages
	intent = new Intent(this, SyndicatedFeedDownloaderService.class);
	intent.putExtra(SyndicatedFeedDownloaderService.TOPIC_ID, SyndicatedFeed.FLU_PAGES_TOPIC_ID);
	intent.putExtra(SyndicatedFeedDownloaderService.EXTRA_MESSENGER,
		new Messenger(new Handler() {
		    @Override
		    public void handleMessage(Message message) {
			if (message.arg1 == Activity.RESULT_OK) {
			    done();
			} else {
			    error(FLU_ACTIVIY_TITLE);
			}
		    }
		}));
	startService(intent);
	
	// CDC pages
	intent = new Intent(this, SyndicatedFeedDownloaderService.class);
	intent.putExtra(SyndicatedFeedDownloaderService.TOPIC_ID, SyndicatedFeed.CDC_PAGES_TOPIC_ID);
	intent.putExtra(SyndicatedFeedDownloaderService.EXTRA_MESSENGER,
		new Messenger(new Handler() {
		    @Override
		    public void handleMessage(Message message) {
			if (message.arg1 == Activity.RESULT_OK) {
			    done();
			} else {
			    error(FLU_ACTIVIY_TITLE);
			}
		    }
		}));
	startService(intent);
    }
    
    public void populateList() {
	feeds.add(FLU_ACTIVIY_TITLE);
	feeds.add(FLU_UPDATES_TITLE);
	feeds.add(FLU_PODCASTS_TITLE);
	feeds.add(FLU_PAGES_TITLE);
	feeds.add(CDC_FEATURE_TITLE);

	//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, feeds);
	FeedListingAdapter adapter = new FeedListingAdapter(this, R.layout.row_with_progress, R.id.row_label, feeds);
	setListAdapter(adapter);
    }
    
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	final String feedTitle = feeds.get(position);
	Log.d(TAG, "onItemClick: " + feedTitle);
	if (FLU_ACTIVIY_TITLE.equals(feedTitle)) {
	    ApplicationController app = (ApplicationController)getApplicationContext();
	    if (app.fluReport == null)
		Toast.makeText(this, feedTitle + " still loading...", Toast.LENGTH_LONG).show();
	    else {
		Intent intent = new Intent(this, FluActivityReport.class);
		startActivity(intent);
	    }
	}
	else {
	    Integer feed = getFeed(position);
	    if (feed != null) {
		Intent intent = new Intent(this, GenericFeedActivity.class);
		intent.putExtra(GenericFeedActivity.FEED_EXTRA, feed);
		intent.putExtra(GenericFeedActivity.TITLE_EXTRA, feedTitle);
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
		Toast.makeText(this, title + " still loading...", Toast.LENGTH_LONG).show();
	    //else
		//return app.fluReport;
	}
	else if (FLU_UPDATES_TITLE.equals(title)) {
	    if (app.fluUpdatesFeed == null)
		Toast.makeText(this, title + " still loading...", Toast.LENGTH_LONG).show();
	    else
		return Feed.FEED_FLU_UPDATES;
	}
	else if (FLU_PODCASTS_TITLE.equals(title)) {
	    if (app.fluPodcastsFeed == null)
		Toast.makeText(this, title + " still loading...", Toast.LENGTH_LONG).show();
	    else
		return Feed.FEED_FLU_PODCASTS;
	}
	else if (FLU_PAGES_TITLE.equals(title)) {
	    if (app.syndicatedFeeds.get(SyndicatedFeed.FLU_PAGES_TOPIC_ID) == null)
		Toast.makeText(this, title + " still loading...", Toast.LENGTH_LONG).show();
	    else
		return Feed.FEED_FLU_PAGES;
	}
	else if (CDC_FEATURE_TITLE.equals(title)) {
	    if (app.syndicatedFeeds.get(SyndicatedFeed.CDC_PAGES_TOPIC_ID) == null)
		Toast.makeText(this, title + " still loading...", Toast.LENGTH_LONG).show();
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

    private boolean isFinishedLoading(final String rowLabel) {
	Log.d(TAG, "Is '" + rowLabel + "' finished loading?");
	ApplicationController app = (ApplicationController)getApplicationContext();
	if (FLU_ACTIVIY_TITLE.equals(rowLabel)) {
	    return (app.fluReport != null);
	}
	else if (FLU_UPDATES_TITLE.equals(rowLabel)) {
	    return (app.fluUpdatesFeed != null);
	}
	else if (FLU_PODCASTS_TITLE.equals(rowLabel)) {
	    return (app.fluUpdatesFeed != null);
	}
	else if (FLU_PAGES_TITLE.equals(rowLabel)) {
	    return (app.syndicatedFeeds.get(SyndicatedFeed.FLU_PAGES_TOPIC_ID) != null);
	}
	else if (CDC_FEATURE_TITLE.equals(rowLabel)) {
	    return (app.syndicatedFeeds.get(SyndicatedFeed.CDC_PAGES_TOPIC_ID) != null);
	}
	else {
	    for (Entry<Integer, SyndicatedFeed> entry : app.syndicatedFeeds.entrySet()) {
		if (rowLabel.equals(entry.getValue().title)) {
		    return true;
		}
	    }
	}
	return false;
    }
    
    private void done() {
	((FeedListingAdapter)getListAdapter()).notifyDataSetChanged();
    }
    
    private void error(final String rowLabel) {
	Toast.makeText(this, ERROR_MSG.replace("%s", rowLabel), Toast.LENGTH_LONG).show();
    }
    
    private class FeedListingAdapter extends ArrayAdapter<String> {

	public FeedListingAdapter(Context context, int resource,
		int textViewResourceId, List<String> objects) {
	    super(context, resource, textViewResourceId, objects);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
	    View row = convertView;

	    if (row == null) {
		LayoutInflater inflater = getLayoutInflater();
		row = inflater.inflate(R.layout.row_with_progress, null);
	    }
	    String feed = super.getItem(position);
	    ((TextView)row.findViewById(R.id.row_label)).setText(feed);
	    ((ProgressBar)row.findViewById(R.id.row_indicator)).setVisibility((isFinishedLoading(feed)) ? View.INVISIBLE : View.VISIBLE);
	    
	    return row;
	}
    }
}
