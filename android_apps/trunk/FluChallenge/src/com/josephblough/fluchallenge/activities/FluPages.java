package com.josephblough.fluchallenge.activities;

import com.josephblough.fluchallenge.services.SyndicatedFeedDownloaderService;
import com.josephblough.fluchallenge.transport.DataRetriever;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class FluPages extends ListActivity {
    
    private ProgressDialog progress = null;
    private final String ERROR_MSG = "There was an error downloading the Flu pages feed";
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        loadFluPagesFeed();
    }
    
    private void loadFluPagesFeed() {
	Intent intent = new Intent(this, SyndicatedFeedDownloaderService.class);
	intent.putExtra(SyndicatedFeedDownloaderService.TOPIC_ID, DataRetriever.FLU_PAGES_TOPIC_ID);
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

	progress = ProgressDialog.show(this, "", "Downloading Flu pages feed");
    }
    
    private void error(final String error) {
	if (progress != null)
	    progress.dismiss();
	
	Toast.makeText(this, error, Toast.LENGTH_LONG);
    }
    
    private void done() {
	if (progress != null)
	    progress.dismiss();
    }
}
