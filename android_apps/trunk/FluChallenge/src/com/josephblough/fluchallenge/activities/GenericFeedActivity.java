package com.josephblough.fluchallenge.activities;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.R;
import com.josephblough.fluchallenge.adapters.RssFeedEntryAdapter;
import com.josephblough.fluchallenge.data.Feed;
import com.josephblough.fluchallenge.data.FeedEntry;
import com.josephblough.fluchallenge.services.SyndicatedFeedDownloaderService;

public class GenericFeedActivity extends FeedListActivity {

    public final static String TAG = "GenericFeedActivity";
    public final static String FEED_EXTRA = "GenericFeedActivity.feed";
    public final static String TITLE_EXTRA = "GenericFeedActivity.title";
    
    private ProgressDialog progress = null;
    private final String ERROR_MSG = "There was an error downloading the feed";

    private int feed;
    private String title;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        registerForContextMenu(getListView());
        
        this.feed = getIntent().getIntExtra(FEED_EXTRA, Feed.FEED_FLU_UPDATES);
        this.title = getIntent().getStringExtra(TITLE_EXTRA);
        if (this.title != null)
            setTitle(this.title);
        
        if (getFeed() == null)
            retrieveFeed();
        else
            renderFeed();
    }

    private void renderFeed() {
	ApplicationController app = (ApplicationController)getApplicationContext();
	RssFeedEntryAdapter adapter = null;
	switch (feed) {
	case Feed.FEED_FLU_UPDATES:
	    adapter = new RssFeedEntryAdapter(this, app.fluUpdatesFeed.items);
	    if (this.title == null)
		setTitle(app.fluUpdatesFeed.title);
	    break;
	case Feed.FEED_FLU_PODCASTS:
	    adapter = new RssFeedEntryAdapter(this, app.fluPodcastsFeed.items);
	    if (this.title == null)
		setTitle(app.fluPodcastsFeed.title);
	    break;
	default: 
	    adapter = new RssFeedEntryAdapter(this, app.syndicatedFeeds.get(feed).items);
	    if (this.title == null)
		setTitle(app.syndicatedFeeds.get(feed).title);
	    break;
	};
	
	if (adapter != null)
	    setListAdapter(adapter);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, view, menuInfo);
        switch (feed) {
        case Feed.FEED_FLU_UPDATES:
            getMenuInflater().inflate(R.menu.feed_list_item_menu, menu);
            break;
        default:
            getMenuInflater().inflate(R.menu.syndicated_feed_list_item_menu, menu);
            break;
        };
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	FeedEntry entry = ((RssFeedEntryAdapter)getListAdapter()).getItem(info.position);

	switch (item.getItemId()) {
	case R.id.context_menu_share_link:
	    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
	    sharingIntent.setType("text/plain");
	    sharingIntent.putExtra(Intent.EXTRA_TEXT, entry.link);
	    startActivity(Intent.createChooser(sharingIntent,"Share using"));
	    return true;
	case R.id.context_menu_view:
	    visitLink(entry);
	    return true;
	case R.id.context_menu_related_topics:
	    Intent topicIntent = new Intent(this, SyndicatedFeedTopicsActivity.class);
	    topicIntent.putExtra(SyndicatedFeedTopicsActivity.FEED_TOPIC_EXTRA, feed);
	    topicIntent.putExtra(SyndicatedFeedTopicsActivity.FEED_INDEX_EXTRA, findFeedEntryPosition(entry));
	    startActivity(topicIntent);
	    return true;
	};

	return super.onContextItemSelected(item);
    }
    
    private List<FeedEntry> getFeed() {
	ApplicationController app = (ApplicationController)getApplicationContext();

	switch (feed) {
	case Feed.FEED_FLU_UPDATES:
	    return app.fluUpdatesFeed.items;
	case Feed.FEED_FLU_PODCASTS:
	    return app.fluPodcastsFeed.items;
	default:
	    return (app.syndicatedFeeds.containsKey(feed)) ? app.syndicatedFeeds.get(feed).items : null;
	}
    }
    
    private int findFeedEntryPosition(final FeedEntry entry) {
	List<FeedEntry> entries = getFeed();
	for (int i=0; i<entries.size(); i++) {
	    if (entries.get(i).equals(entry))
		return i;
	}
	return -1;
    }
    
    private void retrieveFeed() {
	Intent intent = new Intent(this, SyndicatedFeedDownloaderService.class);
	intent.putExtra(SyndicatedFeedDownloaderService.TOPIC_ID, this.feed);
	intent.putExtra(SyndicatedFeedDownloaderService.EXTRA_MESSENGER,
		new Messenger(new Handler() {
		    @Override
		    public void handleMessage(Message message) {
			if (message.arg1 == Activity.RESULT_OK) {
			    done();
			} else {
			    error(ERROR_MSG);
			}
		    }
		}));
	startService(intent);

	progress = ProgressDialog.show(this, "", "Downloading " + title + " feed");
    }
    
    private void done() {
	if (progress != null)
	    progress.dismiss();
	
	renderFeed();
    }
    
    private void error(final String error) {
	if (progress != null)
	    progress.dismiss();
	
	Toast toast = Toast.makeText(this, error, Toast.LENGTH_LONG);
	toast.setGravity(Gravity.BOTTOM, 0, 0);
	toast.show();
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
	retrieveFeed();
    }
}
