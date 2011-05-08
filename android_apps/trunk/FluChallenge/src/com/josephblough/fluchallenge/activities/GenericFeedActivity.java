package com.josephblough.fluchallenge.activities;

import android.os.Bundle;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.data.Feed;
import com.josephblough.fluchallenge.data.SyndicatedFeed;

public class GenericFeedActivity extends FeedListActivity {

    public final static String FEED = "GenericFeedActivity.feed";
    
    private int feed;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        this.feed = getIntent().getIntExtra(FEED, Feed.FEED_FLU_UPDATES);
        renderFeed();
    }

    private void renderFeed() {
	ApplicationController app = (ApplicationController)getApplicationContext();
	RssFeedEntryAdapter adapter = null;
	switch (feed) {
	case Feed.FEED_FLU_UPDATES:
	    adapter = new RssFeedEntryAdapter(this, app.fluUpdatesFeed.items);
	    break;
	case Feed.FEED_FLU_PODCASTS:
	    adapter = new RssFeedEntryAdapter(this, app.fluPodcastsFeed.items);
	    break;
	case Feed.FEED_FLU_PAGES:
	    adapter = new RssFeedEntryAdapter(this, app.syndicatedFeeds.get(SyndicatedFeed.FLU_PAGES_TOPIC_ID).items);
	    break;
	case Feed.FEED_CDC_PAGES:
	    adapter = new RssFeedEntryAdapter(this, app.syndicatedFeeds.get(SyndicatedFeed.CDC_PAGES_TOPIC_ID).items);
	    break;
	};
	
	if (adapter != null)
	    setListAdapter(adapter);
    }
}
