package com.josephblough.fluchallenge.activities;

import com.josephblough.fluchallenge.R;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class TabbedPresentation extends TabActivity {

    private static final String TAG = "TabbedPresentation";
    
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	Log.d(TAG, "onCreate");

	Resources res = getResources();
	TabHost tabHost = getTabHost();
	TabHost.TabSpec spec;
	Intent intent;
	
	// Flu Activity Report
	// Flu Updates
	// Flu Podcasts
	// Flu Pages
	// CDC Feature Pages
	
	intent = new Intent().setClass(this, FluActivityReport.class);
	spec = tabHost.newTabSpec("activity").setIndicator("Activity Report"/*, res.getDrawable(R.drawable.icon)*/).setContent(intent);
	tabHost.addTab(spec);
	
	intent = new Intent().setClass(this, FluUpdates.class);
	spec = tabHost.newTabSpec("updates").setIndicator("Updates"/*, res.getDrawable(R.drawable.icon)*/).setContent(intent);
	tabHost.addTab(spec);

	intent = new Intent().setClass(this, FluPodcasts.class);
	spec = tabHost.newTabSpec("podcasts").setIndicator("Podcasts", res.getDrawable(android.R.drawable.ic_btn_speak_now)).setContent(intent);
	tabHost.addTab(spec);

	intent = new Intent().setClass(this, FluPages.class);
	spec = tabHost.newTabSpec("pages").setIndicator("Pages"/*, res.getDrawable(R.drawable.icon)*/).setContent(intent);
	tabHost.addTab(spec);
	
	intent = new Intent().setClass(this, CdcFeaturePages.class);
	spec = tabHost.newTabSpec("features").setIndicator("CDC Pages"/*, res.getDrawable(R.drawable.icon)*/).setContent(intent);
	tabHost.addTab(spec);

	tabHost.setCurrentTab(0);
    }
}
