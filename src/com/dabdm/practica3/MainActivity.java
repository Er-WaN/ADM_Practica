package com.dabdm.practica3;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button buttonCredit = (Button) findViewById(R.id.Button01main);
    	
    	buttonCredit.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    				final Dialog dialog = new Dialog(MainActivity.this);
    				dialog.setContentView(R.layout.activity_credits);
    				dialog.setTitle("Creditos");
    				dialog.setCancelable(true);
    				
    				TextView text = (TextView) dialog.findViewById(R.id.TextView01);
    				
    				Button button = (Button) dialog.findViewById(R.id.cancelCredits);
    				button.setOnClickListener(new OnClickListener() {
    					public void onClick(View v) {
    						dialog.dismiss();
    					}
    				});
    				dialog.show();
    		}
    	});
    }

		
	public void onClickSettings(View v){
		 startActivity (new Intent (this, SettingsActivity.class));
	}

	public void onClickScores(View v){
		 startActivity (new Intent (this, ScoresActivity.class));
	}
	
	public void onClickPlay(View v){
		 startActivity (new Intent (this, PlayActivity.class));
	}    
	
	
}
