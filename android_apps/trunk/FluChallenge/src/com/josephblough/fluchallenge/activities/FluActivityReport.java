package com.josephblough.fluchallenge.activities;

import com.josephblough.fluchallenge.ApplicationController;
import com.josephblough.fluchallenge.R;
import com.josephblough.fluchallenge.services.FluActivityReportDownloaderService;

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
import android.widget.Toast;

public class FluActivityReport extends Activity {

    private final static String TAG = "FluActivityReport";
    
    private ProgressDialog progress = null;
    private final String ERROR_MSG = "There was an error downloading the Flu report";
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.flu_activity_report);
        
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
	try {
	    ImageView map = (ImageView) findViewById(R.id.flu_activity_map);

	    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.usa_states_gray);
	    /*Canvas canvas = new Canvas(bitmap);
	    Path path = new Path();
	    
	    // From GIMP - pixels
	    path.moveTo(111, 69);
	    path.lineTo(160, 85);
	    path.lineTo(150, 124);
	    path.lineTo(134, 119);
	    path.lineTo(112, 119);
	    path.lineTo(107, 116);
	    path.lineTo(99, 117);
	    path.lineTo(96, 114);
	    path.lineTo(98, 109);
	    path.lineTo(94, 104);
	    path.lineTo(88, 103);
	    path.lineTo(91, 96);
	    path.lineTo(92, 73);
	    path.lineTo(95, 73);
	    path.lineTo(106, 84);
	    path.lineTo(102, 90);
	    path.lineTo(103, 95);
	    path.lineTo(107, 95);
	    path.lineTo(111, 87);
	    path.close();
	    path.offset(60, 60);*/
	    // From GIMP - Points
	    /*path.moveTo(90, 56);
	    path.lineTo(128, 69);
	    path.lineTo(121, 99);
	    path.lineTo(78, 92);
	    path.close();*/
	    /*path.moveTo(90, 25);
	    path.lineTo(94,27);
	    path.lineTo(104,29);
	    path.lineTo(112,31);
	    path.lineTo(131,37);
	    path.lineTo(154,42);
	    path.lineTo(169,46);
	    path.lineTo(168,50);
	    path.lineTo(165,63);
	    path.lineTo(160,83);
	    path.lineTo(157,99);
	    path.lineTo(157,108);
	    path.lineTo(143,105);
	    path.lineTo(128,101);
	    path.lineTo(113,101);
	    path.lineTo(113,100);
	    path.lineTo(108,102);
	    path.lineTo(103,102);
	    path.lineTo(101,100);
	    path.lineTo(100,101);
	    path.lineTo(95,100);
	    path.lineTo(94,99);
	    path.lineTo(89,97);
	    path.lineTo(88,97);
	    path.lineTo(84,96);
	    path.lineTo(82,98);
	    path.lineTo(76,97);
	    path.lineTo(70,93);
	    path.lineTo(71,92);
	    path.lineTo(71,85);
	    path.close();
	    */
	    //69,81, 65,81, 64,78, 62,78, 60,76, 58,77, 56,74, 56,72, 59,71, 61,67, 58,66, 58,63, 62,62, 60,59, 58,53, 59,50, 59,42, 57,39, 59,30, 62,30, 64,33, 67,36, 70,38, 74,40, 77,40, 80,42, 83,43, 85,42, 85,40, 87,39, 89,38, 89,39, 89,41, 87,41, 87,43, 88,44, 89,47, 90,49, 92,48, 92,47, 91,46, 90,43, 91,41, 90,40, 90,38, 92,34, 91,32, 89,27, 89,26, 90,25
	    //map.setBackgroundResource(R.drawable.map_of_usa_with_state_names_gray);
	    /*path.moveTo((float)108.11646, (float)157.8125);
	    path.rLineTo((float)4.21875, (float)1.40625);
	    path.rLineTo((float)9.375, (float)2.65625);
	    path.rLineTo((float)8.28125, (float)1.875);
	    path.rLineTo((float)19.375, (float)5.46875);
	    path.rLineTo((float)22.1875, (float)5.46875);
	    path.rLineTo((float)15.58386, (float)3.59127);
	    path.rLineTo((float)-0.97066, (float)3.75406);
	    path.rLineTo((float)-3.64602, (float)13.03728);
	    path.rLineTo((float)-4.30893, (float)20.10836);
	    path.rLineTo((float)-3.53554, (float)15.90992);
	    path.rLineTo((float)-0.18468, (float)9.37509);
	    path.rLineTo((float)-13.51552, (float)-3.29839);
	    path.rLineTo((float)-14.58409, (float)-3.42505);
	    path.close();
	    //-14.91554,0.11048 -0.44194,-1.32582 -5.30331,1.98874 -4.30893,-0.55243 -2.3202,-1.5468 -1.21534,0.66292 -4.5299,-0.22098 -1.65728,-1.32582 -5.08234,-1.98874 -0.7734,0.11049 -4.19845,-1.43632 -1.878253,1.76777 -5.96622,-0.33145 -5.745242,-3.97748 0.66291,-0.7734 0.22097,-7.51302 -2.20971,-3.7565 -3.97748,-0.55243 -0.66291,-2.43068 -2.27515,-0.45095 -1.85446,-1.44762 -1.71875,0.9375 -2.1875,-2.8125 0.3125,-2.8125 2.65625,-0.3125 1.5625,-3.90625 -2.5,-1.09375 0.15625,-3.59375 4.21875,-0.625 -2.65625,-2.65625 -1.40625,-6.875 0.625,-2.8125 0,-7.65625 -1.71875,-3.125 2.1875,-9.0625 2.03125,0.46875 2.34375,2.8125 2.65625,2.5 3.125,1.875 4.375,2.03125 2.968752,0.625 2.8125,1.40625 3.281253,0.9375 2.1875,-0.15625 0,-2.34375 1.25,-1.09375 2.03125,-1.25 0.3125,1.09375 0.3125,1.71875 -2.1875,0.46875 -0.3125,2.03125 1.71875,1.40625 1.09375,2.34375 0.625,1.875 1.40625,-0.15625 0.15625,-1.25 -0.9375,-1.25 -0.46875,-3.125 0.78125,-1.71875 -0.625,-1.40625 0,-2.1875 1.71875,-3.4375 -1.09375,-2.5 -2.34375,-4.6875 0.3125,-0.78125 1.09375,-0.78125 z m -9.140193,5.77886 1.953133,-0.15625 0.46875,1.32813 1.48438,-1.56251 2.26563,0 0.78125,1.48438 -1.48438,1.64063 0.62501,0.78126 -0.70313,1.95313 -1.32813,0.39062 c 0,0 -0.85938,0.0781 -0.85938,-0.23437 0,-0.3125 1.40626,-2.50001 1.40626,-2.50001 l -1.64063,-0.54688 -0.3125,1.40626 -0.70313,0.625 -1.484383,-2.18751 -0.46875,-2.42188
	    */	    
	    map.setImageBitmap(bitmap);
	}
	catch (Exception e) {
	    Log.e(TAG, e.getMessage(), e);
	}
    }
}
