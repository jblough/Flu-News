package com.josephblough.fluchallenge.activities;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.services.FluActivityReportDownloaderService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class FluActivityReport extends Activity {

    private ProgressDialog progress = null;
    private final String ERROR_MSG = "There was an error downloading the Flu report";
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
	ApplicationController app = (ApplicationController)getApplicationContext();
	if (app.fluReport != null)
	    done();
	else
	    loadFluActivityReport();
    }
    
    private void loadFluActivityReport() {
	Intent intent = new Intent(this, FluActivityReportDownloaderService.class);
	intent.putExtra(FluActivityReportDownloaderService.EXTRA_MESSENGER,
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

	progress = ProgressDialog.show(this, "", "Downloading Flu activity report");
    }
    
    private void error(final String error) {
	if (progress != null)
	    progress.dismiss();
	
	Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
    
    private void done() {
	if (progress != null)
	    progress.dismiss();
    }
}
