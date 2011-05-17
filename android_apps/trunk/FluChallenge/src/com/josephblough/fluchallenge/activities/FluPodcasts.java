package com.josephblough.fluchallenge.activities;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.R;
import com.josephblough.fluchallenge.adapters.PodcastFeedEntryAdapter;
import com.josephblough.fluchallenge.data.FeedEntry;
import com.josephblough.fluchallenge.data.PodcastFeedEntry;
import com.josephblough.fluchallenge.services.FluPodcastsFeedDownloaderService;
import com.josephblough.fluchallenge.services.PodcastDownloaderService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
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
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class FluPodcasts extends FeedListActivity {
    private static final String TAG = "FluPodcasts";
    
    private ProgressDialog progress = null;
    private final String ERROR_MSG = "There was an error downloading the Flu podcasts feed";
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Tell Android that the volume control buttons should set the
        //	media volume and not the ringer volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

	getListView().setOnItemSelectedListener(this);
	getListView().setOnItemClickListener(this);
        registerForContextMenu(getListView());
        
	ApplicationController app = (ApplicationController)getApplicationContext();
	if (app.fluPodcastsFeed != null && app.fluPodcastsFeed.items != null && app.fluPodcastsFeed.items.size() > 0)
	    done();
	else
	    loadFluPodcastsFeed();
    }
    
    private void loadFluPodcastsFeed() {
	Intent intent = new Intent(this, FluPodcastsFeedDownloaderService.class);
	intent.putExtra(FluPodcastsFeedDownloaderService.EXTRA_MESSENGER,
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

	progress = ProgressDialog.show(this, "", "Downloading Flu podcasts feed");
    }
    
    private void error(final String error) {
	if (progress != null)
	    progress.dismiss();
	
	Toast toast = Toast.makeText(this, error, Toast.LENGTH_LONG);
	toast.setGravity(Gravity.BOTTOM, 0, 0);
	toast.show();
    }
    
    private void done() {
	if (progress != null)
	    progress.dismiss();
	
	ApplicationController app = (ApplicationController)getApplicationContext();
	setTitle(app.fluPodcastsFeed.title);
	PodcastFeedEntryAdapter adapter = new PodcastFeedEntryAdapter(this, app.fluPodcastsFeed.items);
	setListAdapter(adapter);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	Log.d(TAG, "onItemClick");
	FeedEntry entry = ((PodcastFeedEntryAdapter)getListAdapter()).getItem(position);
	visitLink(entry);
    }
    
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	Log.d(TAG, "onItemClick");
	FeedEntry entry = ((PodcastFeedEntryAdapter)getListAdapter()).getItem(position);
	visitLink(entry);
    }

    public void refreshList() {
	((PodcastFeedEntryAdapter)getListAdapter()).notifyDataSetChanged();
    }
 
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, view, menuInfo);
        getMenuInflater().inflate(R.menu.podcast_list_item_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        ApplicationController app = (ApplicationController)getApplicationContext();
        PodcastFeedEntry entry = (PodcastFeedEntry)((PodcastFeedEntryAdapter)getListAdapter()).getItem(info.position);

        switch (item.getItemId()) {
        case R.id.context_menu_play_podcast:
            app.playPodcast(entry);
            refreshList();
            return true;
        case R.id.context_menu_share_link:
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, entry.link);
            startActivity(Intent.createChooser(intent,"Share using"));
            return true;
        case R.id.context_menu_view:
            visitLink(entry);
            return true;
        case R.id.context_menu_download:
            downloadPodcast(entry);
            return true;
        };

        return super.onContextItemSelected(item);
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
	loadFluPodcastsFeed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Register to be notified the the podcast finishes playing
	ApplicationController app = (ApplicationController)getApplicationContext();
	app.activityToUpdateOnPlayCompletion = this;
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        // Unregister to be notified the the podcast finishes playing
	ApplicationController app = (ApplicationController)getApplicationContext();
	app.activityToUpdateOnPlayCompletion = null;
    }
    
    private void downloadPodcast(PodcastFeedEntry entry) {
	Intent intent = new Intent(this, PodcastDownloaderService.class);
	intent.putExtra(PodcastDownloaderService.EXTRA_URL, entry.mp3url);
	intent.putExtra(PodcastDownloaderService.EXTRA_TITLE, entry.title);
	startService(intent);

	Toast toast = Toast.makeText(this, "Downloading podcast", 
		Toast.LENGTH_SHORT);
	toast.setGravity(Gravity.BOTTOM, 0, 0);
	toast.show();
    }
}
