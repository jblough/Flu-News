package com.josephblough.fluchallenge.services;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.data.FluReport;
import com.josephblough.fluchallenge.transport.DataRetriever;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class FluActivityReportDownloaderService extends IntentService {

    private final static String TAG = "FluActivityReportDownloaderService";

    public static final String EXTRA_MESSENGER = 
	"FluActivityReportDownloaderService";
    
    public FluActivityReportDownloaderService() {
	super("FluActivityReportDownloaderService");
    }
    
    @Override
    protected void onHandleIntent(Intent intent) {

	Log.d(TAG, "FluActivityReportDownloaderService started");
	
	int result = Activity.RESULT_CANCELED;
	FluReport report = null;
	
	report = DataRetriever.getFluActivityReport();
	if (report != null && report.title != null && !report.title.equals("")) {
	    ((ApplicationController)getApplicationContext()).fluReport = report;
	    result = Activity.RESULT_OK;
	}
	
	Bundle extras = intent.getExtras();
	
	if (extras != null) {
	    Messenger messenger = (Messenger) extras.get(EXTRA_MESSENGER);
	    if (messenger != null) {
		Message message = Message.obtain();
		message.arg1 = result;
		
		try {
		    messenger.send(message);
		} catch (android.os.RemoteException e) {
		    Log.e(TAG, e.getMessage(), e);
		}
	    }
	}

    
    }

}
