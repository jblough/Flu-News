package com.josephblough.fluchallenge;

import com.josephblough.fluchallenge.R;
import com.josephblough.fluchallenge.activities.TabbedPresentation;
import com.josephblough.fluchallenge.transport.DataRetriever;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FluChallenge extends Activity {
    
    DataRetriever retriever;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        retriever = new DataRetriever();
        
        ((Button)findViewById(R.id.flu_vaccination_estimates)).setOnClickListener(new OnClickListener() {
	    
	    public void onClick(View v) {
		retriever.getFluVaccinationEstimates();
	    }
	});
        
        ((Button)findViewById(R.id.weekly_flu_activity)).setOnClickListener(new OnClickListener() {
	    
	    public void onClick(View v) {
		retriever.getFluActivityReport();
	    }
	});
        
        ((Button)findViewById(R.id.flu_updates_rss)).setOnClickListener(new OnClickListener() {
	    
	    public void onClick(View v) {
		retriever.getFluUpdates();
	    }
	});
        
        ((Button)findViewById(R.id.flu_podcasts)).setOnClickListener(new OnClickListener() {
	    
	    public void onClick(View v) {
		retriever.getFluPodcasts();
	    }
	});
        
        ((Button)findViewById(R.id.flu_pages_rss_xml)).setOnClickListener(new OnClickListener() {
	    
	    public void onClick(View v) {
		retriever.getFluPagesAsXml();
	    }
	});
        
        ((Button)findViewById(R.id.cdc_features_pages_xml)).setOnClickListener(new OnClickListener() {
	    
	    public void onClick(View v) {
		retriever.getCdcFeaturesPagesAsXml();
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