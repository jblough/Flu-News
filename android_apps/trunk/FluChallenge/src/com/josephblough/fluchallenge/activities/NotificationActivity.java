package com.josephblough.fluchallenge.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class NotificationActivity extends Activity {

    private static final String TAG = "NotificationActivity";
    
    public static final String EXTRA_FILE_URL = "NotificationActivity.file";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	Log.d(TAG, "onCreate");
	
	final String fileToPlay = getIntent().getStringExtra(EXTRA_FILE_URL);
	
	Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
	Uri data = Uri.parse(fileToPlay/*"file:///sdcard/abc_xyz.mp3"*/);
	intent.setDataAndType(data, "audio/mp3");
	try {
	    startActivity(intent);
	} catch (ActivityNotFoundException e) {
	    Log.e(TAG, e.getMessage(), e);
	}

	finish();
    }
}
