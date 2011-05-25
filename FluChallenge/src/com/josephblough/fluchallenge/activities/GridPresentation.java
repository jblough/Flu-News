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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GridPresentation extends Activity implements OnItemClickListener {

    private final static String TAG = "GridPresentation";
    public final static String FLU_ACTIVIY_TITLE = "Flu Activity Report"; 
    public final static String FLU_UPDATES_TITLE = "Flu Updates"; 
    public final static String FLU_PODCASTS_TITLE = "Flu Podcasts"; 
    public final static String FLU_PAGES_TITLE = "Flu Pages"; 
    public final static String CDC_FEATURE_TITLE = "CDC Feature Pages"; 
    
    private final String ERROR_MSG = "There was an error downloading the %s feed";
    
    private GridView gridView;
    private List<String> feeds = new ArrayList<String>();
    
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.main_grid_layout);
	
	gridView = (GridView) findViewById(R.id.gridview);
	//getListView().setOnItemSelectedListener(this);
	//getListView().setOnItemClickListener(this);
	gridView.setOnItemClickListener(this);
	
	registerForContextMenu(gridView);
	
	populateList();
	loadFeeds();
    }

    public void loadFeeds() {
	loadFluActivityReport();
	loadFluUpdatesFeed();
	loadFluPodcastsFeed();
	loadFluPagesFeed();
	loadCdcFeaturePagesFeed();
    }
    
    public void populateList() {
	feeds.add(FLU_ACTIVIY_TITLE);
	feeds.add(FLU_UPDATES_TITLE);
	feeds.add(FLU_PODCASTS_TITLE);
	feeds.add(FLU_PAGES_TITLE);
	feeds.add(CDC_FEATURE_TITLE);

	FeedListingAdapter adapter = new FeedListingAdapter(this, R.layout.main_grid_cell, R.id.grid_cell_title, feeds);
	gridView.setAdapter(adapter);
    }
    
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	final String feedTitle = feeds.get(position);
	Log.d(TAG, "onItemClick: " + feedTitle);
	if (FLU_ACTIVIY_TITLE.equals(feedTitle)) {
	    ApplicationController app = (ApplicationController)getApplicationContext();
	    if (app.fluReport == null) {
		Toast toast = Toast.makeText(this, feedTitle + " still loading...", Toast.LENGTH_LONG);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.show();
	    }
	    else {
		Intent intent = new Intent(this, FluActivityReport.class);
		startActivity(intent);
	    }
	}
	else if (FLU_PODCASTS_TITLE.equals(feedTitle)) {
	    ApplicationController app = (ApplicationController)getApplicationContext();
	    if (app.fluPodcastsFeed == null) {
		Toast toast = Toast.makeText(this, feedTitle + " still loading...", Toast.LENGTH_LONG);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.show();
	    }
	    else {
		Intent intent = new Intent(this, FluPodcasts.class);
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
	
	if (app.failedFeedRetrievals.contains(title)) {
	    Toast toast = Toast.makeText(this, "There was an error loading the " + title + " feed", Toast.LENGTH_LONG);
	    toast.setGravity(Gravity.BOTTOM, 0, 0);
	    toast.show();
	    Toast toast2 = Toast.makeText(this, "Select 'Refresh' from the menu to re-attempt loading", Toast.LENGTH_LONG);
	    toast2.setGravity(Gravity.BOTTOM, 0, 0);
	    toast2.show();
	}
	else {
	    if (FLU_ACTIVIY_TITLE.equals(title)) {
		if (app.fluReport == null) {
		    Toast toast = Toast.makeText(this, title + " still loading...", Toast.LENGTH_LONG);
		    toast.setGravity(Gravity.BOTTOM, 0, 0);
		    toast.show();
		}
		//else
		//return app.fluReport;
	    }
	    else if (FLU_UPDATES_TITLE.equals(title)) {
		if (app.fluUpdatesFeed == null) {
		    Toast toast = Toast.makeText(this, title + " still loading...", Toast.LENGTH_LONG);
		    toast.setGravity(Gravity.BOTTOM, 0, 0);
		    toast.show();
		}
		else
		    return Feed.FEED_FLU_UPDATES;
	    }
	    else if (FLU_PODCASTS_TITLE.equals(title)) {
		if (app.fluPodcastsFeed == null) {
		    Toast toast = Toast.makeText(this, title + " still loading...", Toast.LENGTH_LONG);
		    toast.setGravity(Gravity.BOTTOM, 0, 0);
		    toast.show();
		}
		else
		    return Feed.FEED_FLU_PODCASTS;
	    }
	    else if (FLU_PAGES_TITLE.equals(title)) {
		if (app.syndicatedFeeds.get(SyndicatedFeed.FLU_PAGES_TOPIC_ID) == null) {
		    Toast toast = Toast.makeText(this, title + " still loading...", Toast.LENGTH_LONG);
		    toast.setGravity(Gravity.BOTTOM, 0, 0);
		    toast.show();
		}
		else
		    return SyndicatedFeed.FLU_PAGES_TOPIC_ID;
	    }
	    else if (CDC_FEATURE_TITLE.equals(title)) {
		if (app.syndicatedFeeds.get(SyndicatedFeed.CDC_PAGES_TOPIC_ID) == null) {
		    Toast toast = Toast.makeText(this, title + " still loading...", Toast.LENGTH_LONG);
		    toast.setGravity(Gravity.BOTTOM, 0, 0);
		    toast.show();
		}
		else
		    return SyndicatedFeed.CDC_PAGES_TOPIC_ID;
	    }
	    else {
		for (Entry<Integer, SyndicatedFeed> entry : app.syndicatedFeeds.entrySet()) {
		    if (title.equals(entry.getValue().title)) {
			return entry.getKey();
		    }
		}
	    }
	}
	
	return null;
    }

    private boolean isFinishedLoading(final String rowLabel) {
	ApplicationController app = (ApplicationController)getApplicationContext();
	if (FLU_ACTIVIY_TITLE.equals(rowLabel)) {
	    return (app.fluReport != null || app.failedFeedRetrievals.contains(FLU_ACTIVIY_TITLE));
	}
	else if (FLU_UPDATES_TITLE.equals(rowLabel)) {
	    return (app.fluUpdatesFeed != null || app.failedFeedRetrievals.contains(FLU_UPDATES_TITLE));
	}
	else if (FLU_PODCASTS_TITLE.equals(rowLabel)) {
	    return (app.fluPodcastsFeed != null || app.failedFeedRetrievals.contains(FLU_PODCASTS_TITLE));
	}
	else if (FLU_PAGES_TITLE.equals(rowLabel)) {
	    return (app.syndicatedFeeds.get(SyndicatedFeed.FLU_PAGES_TOPIC_ID) != null || 
		    app.failedFeedRetrievals.contains(FLU_PAGES_TITLE));
	}
	else if (CDC_FEATURE_TITLE.equals(rowLabel)) {
	    return (app.syndicatedFeeds.get(SyndicatedFeed.CDC_PAGES_TOPIC_ID) != null || 
		    app.failedFeedRetrievals.contains(CDC_FEATURE_TITLE));
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
	((FeedListingAdapter)gridView.getAdapter()).notifyDataSetChanged();
    }
    
    private void error(final String rowLabel) {
	ApplicationController app = (ApplicationController)getApplicationContext();
	app.failedFeedRetrievals.add(rowLabel);
	((FeedListingAdapter)gridView.getAdapter()).notifyDataSetChanged();
	
	Toast toast = Toast.makeText(this, ERROR_MSG.replace("%s", rowLabel), Toast.LENGTH_LONG);
	toast.setGravity(Gravity.BOTTOM, 0, 0);
	toast.show();
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
		row = inflater.inflate(R.layout.main_grid_cell, null);
	    }
	    String feed = super.getItem(position);
	    ((TextView)row.findViewById(R.id.grid_cell_title)).setText(feed);
	    ((ProgressBar)row.findViewById(R.id.grid_cell_indicator)).setVisibility((isFinishedLoading(feed)) ? View.INVISIBLE : View.VISIBLE);
	    ((ImageView)row.findViewById(R.id.grid_cell_image)).setImageResource(getImageId(feed));
	    return row;
	}
	
	private int getImageId(final String title) {
	    if (FLU_ACTIVIY_TITLE.equals(title))
		return R.drawable.flu_activity_report2;
	    else if (FLU_UPDATES_TITLE.equals(title))
		return R.drawable.flu_updates2;
	    else if (FLU_PODCASTS_TITLE.equals(title))
		return R.drawable.flu_podcasts2;
	    else if (FLU_PAGES_TITLE.equals(title))
		return R.drawable.flu_pages2;
	    else if (CDC_FEATURE_TITLE.equals(title))
		return R.drawable.cdc_feature_pages2;
	    return R.drawable.flu_podcasts2;
	}
	
    }

    public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.feed_menu, menu);
	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.menu_item_refresh:
	    refreshFeeds();
	    break;
	}
	return super.onOptionsItemSelected(item);
    }
    
    private void refreshFeeds() {
	ApplicationController app = (ApplicationController)getApplicationContext();
	app.fluReport = null;
	app.fluUpdatesFeed = null;
	app.fluPodcastsFeed = null;
	app.syndicatedFeeds.clear();
	app.failedFeedRetrievals.clear();
	((FeedListingAdapter)gridView.getAdapter()).notifyDataSetChanged();
	loadFeeds();
    }

    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, view, menuInfo);
        getMenuInflater().inflate(R.menu.feed_menu, menu);

        final String title = (String)this.gridView.getAdapter().getItem(((AdapterContextMenuInfo) menuInfo).position);
        menu.findItem(R.id.menu_item_refresh).setTitle("Refresh " + title);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
        case R.id.menu_item_refresh:
            final String title = (String)this.gridView.getAdapter().getItem(info.position);
            refreshFeed(title);
            return true;
        };

        return super.onContextItemSelected(item);
    }

    private void refreshFeed(final String title) {
	ApplicationController app = (ApplicationController)getApplicationContext();
	if (FLU_ACTIVIY_TITLE.equals(title)) {
	    app.fluReport = null;
	    loadFluActivityReport();
	}
	else if (FLU_UPDATES_TITLE.equals(title)) {
	    app.fluUpdatesFeed = null;
	    loadFluUpdatesFeed();
	}
	else if (FLU_PODCASTS_TITLE.equals(title)) {
	    app.fluPodcastsFeed = null;
	    loadFluPodcastsFeed();
	}
	else if (FLU_PAGES_TITLE.equals(title)) {
	    app.syndicatedFeeds.remove(SyndicatedFeed.FLU_PAGES_TOPIC_ID);
	    loadFluPagesFeed();
	}
	else if (CDC_FEATURE_TITLE.equals(title)) {
	    app.syndicatedFeeds.remove(SyndicatedFeed.CDC_PAGES_TOPIC_ID);
	    loadCdcFeaturePagesFeed();
	}
	
	app.failedFeedRetrievals.remove(title);
	((FeedListingAdapter)gridView.getAdapter()).notifyDataSetChanged();
    }

    public void loadFluActivityReport() {
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
    }
    
    public void loadFluUpdatesFeed() {
	// Flu updates
	Intent intent = new Intent(this, FluUpdatesFeedDownloaderService.class);
	intent.putExtra(FluUpdatesFeedDownloaderService.EXTRA_MESSENGER,
		new Messenger(new Handler() {
		    @Override
		    public void handleMessage(Message message) {
			if (message.arg1 == Activity.RESULT_OK) {
			    done();
			} else {
			    error(FLU_UPDATES_TITLE);
			}
		    }
		}));
	startService(intent);
    }

    public void loadFluPodcastsFeed() {
	// Flu podcasts
	Intent intent = new Intent(this, FluPodcastsFeedDownloaderService.class);
	intent.putExtra(FluPodcastsFeedDownloaderService.EXTRA_MESSENGER,
		new Messenger(new Handler() {
		    @Override
		    public void handleMessage(Message message) {
			if (message.arg1 == Activity.RESULT_OK) {
			    done();
			} else {
			    error(FLU_PODCASTS_TITLE);
			}
		    }
		}));
	startService(intent);
    }
    
    public void loadFluPagesFeed() {
	// Flu pages
	Intent intent = new Intent(this, SyndicatedFeedDownloaderService.class);
	intent.putExtra(SyndicatedFeedDownloaderService.TOPIC_ID, SyndicatedFeed.FLU_PAGES_TOPIC_ID);
	intent.putExtra(SyndicatedFeedDownloaderService.EXTRA_MESSENGER,
		new Messenger(new Handler() {
		    @Override
		    public void handleMessage(Message message) {
			if (message.arg1 == Activity.RESULT_OK) {
			    done();
			} else {
			    error(FLU_PAGES_TITLE);
			}
		    }
		}));
	startService(intent);
    }
    
    public void loadCdcFeaturePagesFeed() {
	// CDC pages
	Intent intent = new Intent(this, SyndicatedFeedDownloaderService.class);
	intent.putExtra(SyndicatedFeedDownloaderService.TOPIC_ID, SyndicatedFeed.CDC_PAGES_TOPIC_ID);
	intent.putExtra(SyndicatedFeedDownloaderService.EXTRA_MESSENGER,
		new Messenger(new Handler() {
		    @Override
		    public void handleMessage(Message message) {
			if (message.arg1 == Activity.RESULT_OK) {
			    done();
			} else {
			    error(CDC_FEATURE_TITLE);
			}
		    }
		}));
	startService(intent);
    }
}
