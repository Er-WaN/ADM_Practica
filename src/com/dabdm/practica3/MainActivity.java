package com.dabdm.practica3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		System.out.println("item.getItemId()->"+item.getItemId());
		Log.d("onOptionsItemSelected","item.getItemId()->"+ item.getItemId());
		
		switch (item.getItemId()){
             case R.id.credits:
               startActivity (new Intent (this, CreditsActivity.class));              
               break;
             default:
            	 return super.onOptionsItemSelected(item);
         }
		
		return true;		
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
