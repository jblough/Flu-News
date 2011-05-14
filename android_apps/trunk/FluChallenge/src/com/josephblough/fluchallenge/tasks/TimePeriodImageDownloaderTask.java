package com.josephblough.fluchallenge.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.josephblough.fluchallenge.activities.FluActivityReport;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class TimePeriodImageDownloaderTask extends
	AsyncTask<String, Void, Bitmap> {
    
    private final static String TAG = "TimePeriodImageDownloaderTask";
    FluActivityReport activity;

    public TimePeriodImageDownloaderTask(FluActivityReport activity) {
	this.activity = activity;
    }
    
    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
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

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        
        activity.done(result);
    }
}
