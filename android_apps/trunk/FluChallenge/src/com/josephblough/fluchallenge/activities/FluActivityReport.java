package com.josephblough.fluchallenge.activities;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.R;
import com.josephblough.fluchallenge.data.TimePeriod;
import com.josephblough.fluchallenge.services.FluActivityReportDownloaderService;
import com.josephblough.fluchallenge.tasks.TimePeriodImageDownloaderTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class FluActivityReport extends Activity implements OnSeekBarChangeListener {

    private final static String TAG = "FluActivityReport";

    private ProgressDialog progress = null;
    private final String ERROR_MSG = "There was an error downloading the Flu report";
    private final String IMAGE_URL = "http://www.cdc.gov/flu/weekly/%ARCHIVE_DIRECTORY%/images/%IMAGE_FILENAME%";
    private final String IMAGE_DIRECTORY_PLACEHOLDER = "%ARCHIVE_DIRECTORY%";
    private final String IMAGE_FILENAME_PLACEHOLDER = "%IMAGE_FILENAME%";
    
    TextView mapTitle = null;
    SeekBar periodSeekbar = null;
    ImageView mapImage = null;
    Map<Integer, Bitmap> imageCache; 
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.flu_activity_report);

        this.mapTitle = (TextView) findViewById(R.id.flu_activity_week_label);
	this.periodSeekbar = (SeekBar) findViewById(R.id.flu_activity_week_seekbar);
	this.mapImage = (ImageView) findViewById(R.id.flu_activity_map);
	this.imageCache = new HashMap<Integer, Bitmap>();
        
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
	
	Toast toast = Toast.makeText(this, error, Toast.LENGTH_LONG);
	toast.setGravity(Gravity.BOTTOM, 0, 0);
	toast.show();
    }
    
    private void done() {
	if (progress != null)
	    progress.dismiss();
	
	setTitle(ListPresentation.FLU_ACTIVIY_TITLE);
	final ApplicationController app = (ApplicationController)getApplicationContext();
	final TimePeriod finalPeriod = app.fluReport.periods.get(app.fluReport.periods.size()-1);

	mapTitle.setText(finalPeriod.subtitle);
	
	periodSeekbar.setMax(app.fluReport.periods.size()-1);
	periodSeekbar.setProgress(app.fluReport.periods.size());
	periodSeekbar.setOnSeekBarChangeListener(this/*new OnSeekBarChangeListener() {
	    
	    public void onStopTrackingTouch(SeekBar seekBar) {
		    TimePeriod period = app.fluReport.periods.get(seekBar.getProgress());
		    mapTitle.setText(period.subtitle);
		    updateImage(period);
	    }
	    
	    public void onStartTrackingTouch(SeekBar seekBar) {
	    }
	    
	    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		TimePeriod period = app.fluReport.periods.get(progress);
		mapTitle.setText(period.subtitle);
	    }
	}*/);

	updateImage(finalPeriod);
	
    }

    private void updateImage(TimePeriod period) {
	Log.d(TAG, "Displaying progress dialog");
	//ProgressDialog downloadProgress = ProgressDialog.show(this, "", "Downloading map image...", false, false);
	progress = ProgressDialog.show(this, "", "Downloading map image...");
	/*
	try {
	    //map.setVisibility(View.INVISIBLE);
	    //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.usa_states_gray);
	    String archiveDirectory = "weeklyarchives" + (period.year-1) + "-" + period.year;
	    String imageFilename = "usmap" + period.number + ".jpg";
	    //Bitmap bitmap = getBitmapFromURL("http://www.cdc.gov/flu/weekly/" + archiveDirectory + "/images/" + imageFilename);
	    String imageUrl = 
		IMAGE_URL.replace(IMAGE_DIRECTORY_PLACEHOLDER, archiveDirectory).replace(IMAGE_FILENAME_PLACEHOLDER, imageFilename);
	    Bitmap bitmap = getBitmapFromURL(imageUrl);
	    mapImage.setImageBitmap(bitmap);
	    //map.setVisibility(View.VISIBLE);
	}
	catch (Exception e) {
	    Log.e(TAG, e.getMessage(), e);
	}
	*/
	String archiveDirectory = "weeklyarchives" + (period.year-1) + "-" + period.year;
	String imageFilename = "usmap" + period.number + ".jpg";
	//Bitmap bitmap = getBitmapFromURL("http://www.cdc.gov/flu/weekly/" + archiveDirectory + "/images/" + imageFilename);
	String imageUrl = 
	    IMAGE_URL.replace(IMAGE_DIRECTORY_PLACEHOLDER, archiveDirectory).replace(IMAGE_FILENAME_PLACEHOLDER, imageFilename);
	new TimePeriodImageDownloaderTask(this).execute(imageUrl);
	
	//Log.d(TAG, "Dismissing progress dialog");
	//downloadProgress.dismiss();
	//progress.dismiss();
    }
    
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public void done(final Bitmap bitmap) {
	if (progress != null)
	    progress.dismiss();
	
	if (bitmap == null) {
            Toast toast = Toast.makeText(this, "Unable to download image", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
	}
	else {
	    mapImage.setImageBitmap(bitmap);
	    imageCache.put(periodSeekbar.getProgress(), bitmap);
	}
    }
    
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	final ApplicationController app = (ApplicationController)getApplicationContext();
	TimePeriod period = app.fluReport.periods.get(progress);
	mapTitle.setText(period.subtitle);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
	// TODO Auto-generated method stub
	
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
	final ApplicationController app = (ApplicationController)getApplicationContext();
	TimePeriod period = app.fluReport.periods.get(seekBar.getProgress());
	mapTitle.setText(period.subtitle);
	if (imageCache.containsKey(seekBar.getProgress())) {
	    done(imageCache.get(seekBar.getProgress()));
	}
	else {
	    updateImage(period);
	}
    }
}
