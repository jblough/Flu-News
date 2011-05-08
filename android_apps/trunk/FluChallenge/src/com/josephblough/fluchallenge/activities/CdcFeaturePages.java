package com.josephblough.fluchallenge.activities;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.data.SyndicatedFeed;
import com.josephblough.fluchallenge.services.SyndicatedFeedDownloaderService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class CdcFeaturePages extends FeedListActivity {
    
    private ProgressDialog progress = null;
    private final String ERROR_MSG = "There was an error downloading the CDC pages feed";
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
	ApplicationController app = (ApplicationController)getApplicationContext();
	SyndicatedFeed feed = app.syndicatedFeeds.get(SyndicatedFeed.CDC_PAGES_TOPIC_ID);
	if (feed != null && feed.items != null && feed.items.size() > 0)
	    done();
	else
	    loadCdcPagesFeed();
    }
    
    private void loadCdcPagesFeed() {
	Intent intent = new Intent(this, SyndicatedFeedDownloaderService.class);
	intent.putExtra(SyndicatedFeedDownloaderService.TOPIC_ID, SyndicatedFeed.CDC_PAGES_TOPIC_ID);
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

	progress = ProgressDialog.show(this, "", "Downloading CDC pages feed");
    }
    
    private void error(final String error) {
	if (progress != null)
	    progress.dismiss();
	
	Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
    
    private void done() {
	if (progress != null)
	    progress.dismiss();
	
	ApplicationController app = (ApplicationController)getApplicationContext();
	RssFeedEntryAdapter adapter = new RssFeedEntryAdapter(this, app.syndicatedFeeds.get(SyndicatedFeed.CDC_PAGES_TOPIC_ID).items);
	setListAdapter(adapter);
    }
}
