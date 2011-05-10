package com.josephblough.fluchallenge.activities;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.R;
import com.josephblough.fluchallenge.data.Feed;
import com.josephblough.fluchallenge.data.FeedEntry;
import com.josephblough.fluchallenge.data.SyndicatedFeed;

public class GenericFeedActivity extends FeedListActivity {

    public final static String FEED_EXTRA = "GenericFeedActivity.feed";
    public final static String TITLE_EXTRA = "GenericFeedActivity.title";
    
    private int feed;
    private String title;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerForContextMenu(getListView());
        
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
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, view, menuInfo);
        switch (feed) {
        case Feed.FEED_FLU_UPDATES:
            getMenuInflater().inflate(R.menu.feed_list_item_menu, menu);
            break;
        case Feed.FEED_FLU_PAGES:
        case Feed.FEED_CDC_PAGES:
            getMenuInflater().inflate(R.menu.syndicated_feed_list_item_menu, menu);
            break;
        default:
            getMenuInflater().inflate(R.menu.syndicated_feed_list_item_menu, menu);
            break;
        };
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
        case R.id.context_menu_share_link:
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getFeed().get(info.position).link);
            startActivity(Intent.createChooser(intent,"Share using"));
            return true;
        case R.id.context_menu_view:
            visitLink(getFeed().get(info.position));
            return true;
        case R.id.context_menu_related_topics:
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
	case Feed.FEED_FLU_PAGES:
	    return app.syndicatedFeeds.get(SyndicatedFeed.FLU_PAGES_TOPIC_ID).items;
	case Feed.FEED_CDC_PAGES:
	    return app.syndicatedFeeds.get(SyndicatedFeed.CDC_PAGES_TOPIC_ID).items;
	default:
	    return (app.syndicatedFeeds.containsKey(feed)) ? app.syndicatedFeeds.get(feed).items : null;
	}
    }
}
