package com.josephblough.fluchallenge.activities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.josephblough.fluchallenge.R;
import com.josephblough.fluchallenge.data.FeedEntry;
import com.josephblough.fluchallenge.data.PodcastFeedEntry;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PodcastFeedEntryAdapter extends ArrayAdapter<FeedEntry> {

    private final String TAG = "RssFeedEntryAdapter";

    SimpleDateFormat inputFormatter1 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    SimpleDateFormat inputFormatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    SimpleDateFormat outputFormatter = new SimpleDateFormat();
    Activity activity = null;

    PodcastFeedEntryAdapter(Activity activity, List<FeedEntry> entries) {
	super(activity, R.layout.podcast_row, entries);
	this.activity = activity;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
	View row = convertView;

	if (row == null) {
	    LayoutInflater inflater = activity.getLayoutInflater();
	    row = inflater.inflate(R.layout.podcast_row, null);
	}
	PodcastFeedEntry entry = (PodcastFeedEntry)super.getItem(position);
	((TextView)row.findViewById(R.id.podcast_title)).setText(entry.title);
	synchronized (this) {
	    Date date = null;
	    try {
		date = inputFormatter1.parse(entry.date);
	    }
	    catch (Exception e1) {
		try {
		    date = inputFormatter2.parse(entry.date);
		}
		catch (Exception e2) {
		    Log.e(TAG, "Feed date in unrecognized format");
		}
	    }

	    if (date != null) {
		((TextView) row.findViewById(R.id.podcast_updated)).setText(outputFormatter.format(date));
	    }
	    else {
		((TextView) row.findViewById(R.id.podcast_updated)).setText(entry.date);
	    }
	    
	    ((TextView) row.findViewById(R.id.podcast_duration)).setText(entry.duration);
	}
	return row;
    }
}
