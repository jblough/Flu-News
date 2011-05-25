package com.josephblough.fluchallenge.activities;

import com.josephblough.fluchallenge.R;
import com.josephblough.fluchallenge.transport.DataRetriever;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FluChallenge extends Activity {
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ((Button)findViewById(R.id.flu_vaccination_estimates)).setOnClickListener(new OnClickListener() {
	    
	    public void onClick(View v) {
		DataRetriever.getFluVaccinationEstimates();
	    }
	});
        
        ((Button)findViewById(R.id.weekly_flu_activity)).setOnClickListener(new OnClickListener() {
	    
	    public void onClick(View v) {
		DataRetriever.getFluActivityReport();
	    }
	});
        
        ((Button)findViewById(R.id.flu_updates_rss)).setOnClickListener(new OnClickListener() {
	    
	    public void onClick(View v) {
		DataRetriever.getFluUpdates();
	    }
	});
        
        ((Button)findViewById(R.id.flu_podcasts)).setOnClickListener(new OnClickListener() {
	    
	    public void onClick(View v) {
		DataRetriever.getFluPodcasts();
	    }
	});
        
        ((Button)findViewById(R.id.flu_pages_rss_xml)).setOnClickListener(new OnClickListener() {
	    
	    public void onClick(View v) {
		DataRetriever.getFluPagesAsXml();
	    }
	});
        
        ((Button)findViewById(R.id.cdc_features_pages_xml)).setOnClickListener(new OnClickListener() {
	    
	    public void onClick(View v) {
		DataRetriever.getCdcFeaturesPagesAsXml();
	    }
	});
        
        ((Button)findViewById(R.id.tabbed_interface)).setOnClickListener(new OnClickListener() {
	    
	    public void onClick(View v) {
		Intent i = new Intent(FluChallenge.this, TabbedPresentation.class);
		startActivity(i);
	    }
	});
    }
}