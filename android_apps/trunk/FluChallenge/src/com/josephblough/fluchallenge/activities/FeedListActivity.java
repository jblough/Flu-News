package com.josephblough.fluchallenge.activities;

import com.josephblough.fluchallenge.R;
import com.josephblough.fluchallenge.adapters.RssFeedEntryAdapter;
import com.josephblough.fluchallenge.data.FeedEntry;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class FeedListActivity extends ListActivity implements OnItemSelectedListener, OnItemClickListener, OnEditorActionListener, TextWatcher {

    private final String TAG = "FeedListActivity";
    
    private View searchView;
    
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

        setContentView(R.layout.feed_entry_listing);

	getListView().setOnItemSelectedListener(this);
	getListView().setOnItemClickListener(this);
        getListView().setTextFilterEnabled(true);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	FeedEntry entry = ((RssFeedEntryAdapter)getListAdapter()).getItem(position);
	visitLink(entry);
    }
    
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	FeedEntry entry = ((RssFeedEntryAdapter)getListAdapter()).getItem(position);
	visitLink(entry);
    }
    
    public void onNothingSelected(AdapterView<?> parent) {
	// TODO Auto-generated method stub
    }

    protected void visitLink(final FeedEntry entry) {
	Log.d(TAG, "Selected: " + entry.link);
	final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(entry.link));
	startActivity(intent);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_SEARCH) {
	    return onSearchRequested();
	}
	return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onSearchRequested() {
	toggleSearch();

	// Returning true indicates that we did launch the search, instead of blocking it.
	return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	// If we're in search mode when the back button is pressed, exit search mode
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    if (searchView != null &&
		    searchView.isShown()) {
		toggleSearch();
		return true;
	    }
	}

	return super.onKeyDown(keyCode, event);
    }

    private void toggleSearch() {
	if (searchView == null) {
	    searchView = ((ViewStub) findViewById(R.id.stub_search)).inflate();
	    EditText input = ((EditText) searchView.findViewById(R.id.input_search_query));
	    input.setOnEditorActionListener(this);
	    input.addTextChangedListener(this);
	    Button searchButton = ((Button) searchView.findViewById(R.id.button_go));
	    searchButton.setOnClickListener(new View.OnClickListener() {

		public void onClick(View v) {
		    toggleSearch();
		}
	    });

	    searchView.setVisibility(View.VISIBLE);

	    input.requestFocus();
	    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
	    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}
	else if (searchView.isShown()) {
	    searchView.setVisibility(View.GONE);

	    EditText input = ((EditText) searchView.findViewById(R.id.input_search_query));
	    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
	    input.clearFocus();
	}
	else {
	    searchView.setVisibility(View.VISIBLE);

	    EditText input = ((EditText) searchView.findViewById(R.id.input_search_query));
	    input.requestFocus();
	    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
	    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	toggleSearch();
	v.clearFocus();

	return false;
    }

    public void afterTextChanged(Editable s) {
	getListView().setFilterText(s.toString());
    }

    public void beforeTextChanged(CharSequence s, int start, int count,
	    int after) {
	// TODO Auto-generated method stub
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
	// TODO Auto-generated method stub
    }
}
