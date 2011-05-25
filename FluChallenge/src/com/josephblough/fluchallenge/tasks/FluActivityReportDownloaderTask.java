package com.josephblough.fluchallenge.tasks;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.transport.DataRetriever;
import com.josephblough.fluchallenge.data.FluReport;

import android.os.AsyncTask;

public class FluActivityReportDownloaderTask extends AsyncTask<Void, Void, FluReport> {

	private ApplicationController app;

	public FluActivityReportDownloaderTask(ApplicationController app) {
		this.app = app;
	}
	
	@Override
	protected FluReport doInBackground(Void... params) {
		return DataRetriever.getFluActivityReport();
	}

	protected void onPostExecute(FluReport result) {
		app.fluReport = result;
	}
}
