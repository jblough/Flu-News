package com.josephblough.fluchallenge.activities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.R;
import com.josephblough.fluchallenge.data.FeedEntry;
import com.josephblough.fluchallenge.data.PodcastFeedEntry;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class PodcastFeedEntryAdapter extends ArrayAdapter<FeedEntry> {

    private final String TAG = "RssFeedEntryAdapter";

    SimpleDateFormat inputFormatter1 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    SimpleDateFormat inputFormatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    SimpleDateFormat outputFormatter = new SimpleDateFormat();
    FluPodcasts activity = null;
    final Bitmap playImage;
    final Bitmap stopImage;

    PodcastFeedEntryAdapter(FluPodcasts activity, List<FeedEntry> entries) {
	super(activity, R.layout.podcast_row, entries);
	this.activity = activity;
	this.playImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_media_play);
	this.stopImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_media_pause);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
	View row = convertView;

	if (row == null) {
	    LayoutInflater inflater = activity.getLayoutInflater();
	    row = inflater.inflate(R.layout.podcast_row, null);
	}
	final PodcastFeedEntry entry = (PodcastFeedEntry)super.getItem(position);
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
	    
	    final ImageButton control = (ImageButton) row.findViewById(R.id.podcast_control);
	    control.setBackgroundDrawable(null);
	    final ApplicationController app = (ApplicationController)activity.getApplicationContext();
	    PodcastFeedEntry playingPodcast = app.getCurrentlyPlayingPodcast();
	    if (playingPodcast != null && playingPodcast.mp3url.equals(entry.mp3url)) {
		control.setImageBitmap(this.stopImage);
	    }
	    else {
		control.setImageBitmap(this.playImage);
	    }
	    control.setOnClickListener(new View.OnClickListener() {
	        
	        public void onClick(View v) {
	            Log.d(TAG, "Podcast control tapped for " + position);
		    PodcastFeedEntry playingPodcast = app.getCurrentlyPlayingPodcast();
		    if (playingPodcast != null && playingPodcast.mp3url.equals(entry.mp3url)) {
			// This podcast is currently being played, stop it
			Log.d(TAG, "Changing image back to play image");
			control.setImageBitmap(playImage);
			new Thread(new Runnable() {
			    
			    public void run() {
				app.stopPodcast();
			    }
			}).start();
		    }
		    else {
			// This podcast is NOT being played, play it
			control.setImageBitmap(stopImage);
			//activity.playPodcast(position);
			new Thread(new Runnable() {
			    
			    public void run() {
				app.playPodcast(position);
			    }
			}).start();
		    }
		    activity.refreshList();
	        }
	    });

	    // One downside is that this does not hilight the selected row 
	    row.setOnClickListener(new View.OnClickListener() {
	        
	        public void onClick(View v) {
	            activity.visitLink(entry);
	        }
	    });
	}
	return row;
    }
}
