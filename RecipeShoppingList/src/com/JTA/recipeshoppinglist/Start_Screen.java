package com.JTA.recipeshoppinglist;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;



public class Start_Screen extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start__screen);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start__screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_start__screen, container, false);
            return rootView;
        }
    }
    
    public void createNew(){
		Intent intent = new Intent(this, ListScreen.class);
		intent.putExtra("hi", "new");
		startActivity(intent);
	 }
    
    public void continue_but(View v){
		Intent intent = new Intent(this, ListScreen.class);
		intent.putExtra("hi", "continue");
		startActivity(intent);
	 }
    
    public void listScreen(View v){
    	AlertDialog.Builder aboutDialog = new AlertDialog.Builder(this);
	    //aboutDialog.setMessage(R.string.about_dialog);
	    aboutDialog.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {	
			dialog.cancel();
		}
		});
	    aboutDialog.setPositiveButton(R.string.new_button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {	
				Start_Screen.this.createNew();
			}
			});
	    AlertDialog dialog = aboutDialog.create();
	    dialog.show();
	    }
    }

