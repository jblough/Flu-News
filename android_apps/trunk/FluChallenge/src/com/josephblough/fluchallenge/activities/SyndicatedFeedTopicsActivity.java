package com.josephblough.fluchallenge.activities;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.data.SyndicatedFeed;
import com.josephblough.fluchallenge.data.SyndicatedFeedEntry;
import com.josephblough.fluchallenge.data.SyndicatedFeedEntryTopic;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class SyndicatedFeedTopicsActivity extends ListActivity implements OnItemSelectedListener, OnItemClickListener {

    public final static String FEED_TOPIC_EXTRA = "SyndicatedFeedTopicsActivity.feed_topic";
    public final static String FEED_INDEX_EXTRA = "SyndicatedFeedTopicsActivity.feed_index";
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int feedTopic = 
            getIntent().getIntExtra(SyndicatedFeedTopicsActivity.FEED_TOPIC_EXTRA, SyndicatedFeed.FLU_PAGES_TOPIC_ID);
        int feedIndex = 
            getIntent().getIntExtra(SyndicatedFeedTopicsActivity.FEED_INDEX_EXTRA, 0);
        
	ApplicationController app = (ApplicationController)getApplicationContext();
	SyndicatedFeedEntry entry = (SyndicatedFeedEntry)app.syndicatedFeeds.get(feedTopic).items.get(feedIndex);
	SyndicatedFeedTopicAdapter adapter = new SyndicatedFeedTopicAdapter(this, entry.topics);
	setListAdapter(adapter);

	getListView().setOnItemSelectedListener(this);
	getListView().setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	SyndicatedFeedEntryTopic topic = (SyndicatedFeedEntryTopic)getListAdapter().getItem(position);
	Intent intent = new Intent(this, GenericFeedActivity.class);
	intent.putExtra(GenericFeedActivity.FEED_EXTRA, topic.id);
	intent.putExtra(GenericFeedActivity.TITLE_EXTRA, topic.name);
	startActivity(intent);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub
	
    }

    public void onNothingSelected(AdapterView<?> parent) {
	// TODO Auto-generated method stub
	
    }
}
