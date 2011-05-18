package com.josephblough.fluchallenge;

import java.util.HashMap;
import java.util.Map;

import com.josephblough.fluchallenge.activities.FluPodcasts;
import com.josephblough.fluchallenge.data.Feed;
import com.josephblough.fluchallenge.data.FluReport;
import com.josephblough.fluchallenge.data.PodcastFeedEntry;
import com.josephblough.fluchallenge.data.SyndicatedFeed;

import android.app.Application;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;
import android.widget.Toast;

public class ApplicationController extends Application implements OnCompletionListener, OnPreparedListener, OnBufferingUpdateListener {

    private final static String TAG = "ApplicationController";

    public FluReport fluReport;
    public Feed fluUpdatesFeed;
    public Feed fluPodcastsFeed;
    public Map<Integer, SyndicatedFeed> syndicatedFeeds = new HashMap<Integer, SyndicatedFeed>();
    public Integer currentlyPlayingPodcast = null;
    public FluPodcasts activityToUpdateOnPlayCompletion = null;

    private MediaPlayer player = null;
    private boolean playerIsPrepared = false;

    public void onCreate() {
	super.onCreate();
	
	//Do Application initialization over here
	Log.d(TAG, "onCreate");
	player = new MediaPlayer();
	player.setOnCompletionListener(this);
	player.setOnPreparedListener(this);
	player.setOnBufferingUpdateListener(this);
    }

    @Override
    public void onTerminate () {
	if (player != null) {
	    if (player.isPlaying()) {
		player.stop();
	    }
	    player.release();
	}
    }

    public void playPodcast(final int position) {
	//Log.d(TAG, "playing podcast " + position);
	if (fluPodcastsFeed != null && fluPodcastsFeed.items != null && fluPodcastsFeed.items.size() > 0) {
	    PodcastFeedEntry entry = (PodcastFeedEntry)fluPodcastsFeed.items.get(position);
	    playPodcast(entry);
	}
    }
    
    public void playPodcast(final PodcastFeedEntry entry) {
	try {
	    currentlyPlayingPodcast = findPodcast(entry);
	    //Log.d(TAG, "Downloading " + entry.mp3url);
	    if (player.isPlaying()) {
		player.stop();
	    }
	    player.reset();
	    playerIsPrepared = false;
	    player.setDataSource(entry.mp3url);
	    player.prepareAsync();
	}
	catch (Exception e) {
	    Log.e(TAG, e.getMessage(), e);
	}
    }
    
    public void stopPodcast() {
	//Log.d(TAG, "stopPodcast");
	currentlyPlayingPodcast = null;
	if (player.isPlaying()) {
	    player.stop();
	}
    }

    public void onPrepared(MediaPlayer player) {
	playerIsPrepared = true;
	if (currentlyPlayingPodcast != null) {
	    player.start();
	}
    }

    public void onCompletion(MediaPlayer player) {
	//Log.d(TAG, "onCompletion - playback position : " + player.getCurrentPosition() + " of " + player.getDuration());
	if (playerIsPrepared && player.getDuration() > 0) {
	    currentlyPlayingPodcast = null;

	    if (activityToUpdateOnPlayCompletion != null) {
		activityToUpdateOnPlayCompletion.refreshList();
	    }
	}
    }
    
    public PodcastFeedEntry getCurrentlyPlayingPodcast() {
	if (currentlyPlayingPodcast == null)
	    return null;
	
	return (PodcastFeedEntry)fluPodcastsFeed.items.get(currentlyPlayingPodcast);
    }

    public void onBufferingUpdate(MediaPlayer player, int percent) {
	Toast.makeText(this, "Buffering complete: " + percent + "%", Toast.LENGTH_SHORT);
    }
    
    private int findPodcast(final PodcastFeedEntry entry) {
	for (int i=0; i<fluPodcastsFeed.items.size(); i++) {
	    if (fluPodcastsFeed.items.get(i).equals(entry)) {
		return i;
	    }
	}
	return -1;
    }
}
