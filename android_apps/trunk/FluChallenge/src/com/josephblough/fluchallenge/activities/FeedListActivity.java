package com.josephblough.fluchallenge.activities;

import com.josephblough.fluchallenge.adapters.RssFeedEntryAdapter;
import com.josephblough.fluchallenge.data.FeedEntry;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class FeedListActivity extends ListActivity implements OnItemSelectedListener, OnItemClickListener {

    private final String TAG = "FeedListActivity";
    
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	getListView().setOnItemSelectedListener(this);
	getListView().setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	FeedEntry entry = ((RssFeedEntryAdapter)getListAdapter()).getItem(position);
	visitLink(entry);
    }
    
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	FeedEntry entry = ((RssFeedEntryAdapter)getListAdapter()).getItem(position);
	visitLink(entry);
    }
    
    public void onNothingSelected(AdapterView<?> parent) {
	// TODO Auto-generated method stub
    }

    protected void visitLink(final FeedEntry entry) {
	Log.d(TAG, "Selected: " + entry.link);
	final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(entry.link));
	startActivity(intent);
    }
}
