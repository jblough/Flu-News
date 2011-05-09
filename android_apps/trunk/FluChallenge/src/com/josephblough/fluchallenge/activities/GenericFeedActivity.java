package com.josephblough.fluchallenge.activities;

import android.os.Bundle;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.data.Feed;
import com.josephblough.fluchallenge.data.SyndicatedFeed;

public class GenericFeedActivity extends FeedListActivity {

    public final static String FEED_EXTRA = "GenericFeedActivity.feed";
    public final static String TITLE_EXTRA = "GenericFeedActivity.title";
    
    private int feed;
    private String title;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        this.feed = getIntent().getIntExtra(FEED_EXTRA, Feed.FEED_FLU_UPDATES);
        this.title = getIntent().getStringExtra(TITLE_EXTRA);
        if (this.title != null)
            setTitle(this.title);
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
	case Feed.FEED_FLU_PAGES:
	    adapter = new RssFeedEntryAdapter(this, app.syndicatedFeeds.get(SyndicatedFeed.FLU_PAGES_TOPIC_ID).items);
	    if (this.title == null)
		setTitle(app.syndicatedFeeds.get(SyndicatedFeed.FLU_PAGES_TOPIC_ID).title);
	    break;
	case Feed.FEED_CDC_PAGES:
	    adapter = new RssFeedEntryAdapter(this, app.syndicatedFeeds.get(SyndicatedFeed.CDC_PAGES_TOPIC_ID).items);
	    if (this.title == null)
		setTitle(app.syndicatedFeeds.get(SyndicatedFeed.CDC_PAGES_TOPIC_ID).title);
	    break;
	};
	
	if (adapter != null)
	    setListAdapter(adapter);
    }
}
