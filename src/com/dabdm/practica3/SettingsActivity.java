package com.dabdm.practica3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import modelo.HighScoreList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;


public class SettingsActivity extends Activity /*implements OnItemSelectedListener */ {

	private static String[] ayudasPermitidas = {"0","1","2","3"};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
  
        restoreData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		saveData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveData();
	}

	@Override
	protected void onStop() {
		super.onStop();
		saveData();
	}

	
	public void onClickAddFriend(View v){
		
		String[] parametros = {"yo","amigo"};
		new AddAmigo().equals(parametros);
	}
	
	
	
	private void restoreData() {
		
		String ayudas=null;
		String nomUsuario=null;
		try {
			SharedPreferences preferences = getSharedPreferences("estado.xml",Context.MODE_PRIVATE);		
			//Recupera el n�mero de pregunta que estaba
			ayudas = preferences.getString("numAyudas", "0");
			nomUsuario = preferences.getString("usuario", "");
			
		} catch (Exception e) {
			ayudas = "0";
		}		
		
		Spinner numAyudas = (Spinner) findViewById(R.id.manyTimes);
		numAyudas.setSelection( Integer.parseInt(ayudas));
		
		EditText usuario = (EditText) findViewById(R.id.usuario);
		usuario.setText(nomUsuario);
		
	}

	private void saveData() {
		
		Spinner numAyudas = (Spinner) findViewById(R.id.manyTimes);
		EditText usuario = (EditText) findViewById(R.id.usuario);
		
		SharedPreferences preferences = getSharedPreferences("estado.xml",Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("numAyudas", (String) numAyudas.getSelectedItem() );
		editor.putString("usuario", usuario.getText().toString() );
		
		editor.commit();
	}

    	

	
	
   // Clase privada para lanzar la peticion POST http
    
    private class AddAmigo extends AsyncTask<String, Void, Boolean> {
    	
    	@Override
    	protected void onPreExecute() {    
    		super.onPreExecute();
    	}

    	@Override
    	protected Boolean doInBackground(String... params) {          
    
            		
    		HttpResponse response = null;
    		HttpEntity entity = null;
    		HttpClient client = new DefaultHttpClient();
    		HttpPost request = null;
    		List<NameValuePair> pares = new ArrayList<NameValuePair>();
    		pares.add(new BasicNameValuePair("name", params[0]));
    		pares.add(new BasicNameValuePair("friend_name", params[1]));

			try {
				
				request = new HttpPost("http://soletaken.disca.upv.es:8080/WWTBAM/rest/friends");
				request.setEntity(new UrlEncodedFormEntity(pares));				
				response = client.execute(request);						

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}            		
    		
    		return true;
    	}

    	@Override
    	protected void onProgressUpdate(Void... values) {      		  		
    		super.onProgressUpdate(values);
    		Thread.currentThread().interrupt();
    	}
    }
	
}
