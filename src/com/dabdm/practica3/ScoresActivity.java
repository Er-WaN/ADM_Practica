package com.dabdm.practica3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import modelo.HighScore;
import modelo.HighScoreList;
import modelo.Question;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ScoresActivity extends Activity {

	//private static String TABLE = "scores";
	//private static String[] COLUMNS = {"id","name","score"};
	
	
	private MyHelperBBDD myHelperBBDD;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        setContentView(R.layout.activity_scores);
        this.myHelperBBDD = new MyHelperBBDD(this);
        TabHost tabhost = (TabHost) findViewById(R.id.tabhost);          
        tabhost.setup();
        
        TabSpec tabSpec = tabhost.newTabSpec("Mis puntuaciones");//Esto
        
        tabSpec.setIndicator("MisPuntos");
        tabSpec.setContent(R.id.scores_tabla);        
        tabhost.addTab(tabSpec);
        tabhost.setCurrentTab(0);
        
        cargaTablaMisPuntuaciones();
        
        tabSpec = tabhost.newTabSpec("Amigos");//Esto
        tabSpec.setIndicator("amigos");
        tabSpec.setContent(R.id.scores_amigos);
        tabhost.addTab(tabSpec);
        //tabhost.setCurrentTab(1); //Si se quiere que sea esta la activa por defecto
        
        String usuario = restoreData();
        
        if(usuario!=null &&  !"".equals(usuario)){
           new RecuperarScoresAmigos().execute( usuario );
        }
           
      
    }

    /**
     * Recuperar el nombre de mi usuario
     * @return Recuperar el nombre de mi usuario
     */
	private String restoreData() {
		
		String usuario;
		try {
			SharedPreferences preferences = getSharedPreferences("estado.xml",Context.MODE_PRIVATE);		
			//Recupera el n�mero de pregunta que estaba
			usuario = preferences.getString("usuario", "");
						
		} catch (Exception e) {
			usuario = "";
		}		
		
		return usuario;
	}
    
    
    
    public void cargaTablaMisPuntuaciones(){
        TableLayout tabla = (TableLayout) findViewById(R.id.scores_tabla);
        tabla.removeAllViews();
        TableRow fila;
        TextView texto;        
        Cursor cursor = getScores();
        cursor.moveToFirst();
        
        while (!cursor.isAfterLast()) {
            fila = new TableRow(this);
           
            // A�ade columna name
            texto = new TextView(this);
            texto.setText( cursor.getString(1));
            fila.addView(texto);
            // A�ade columna score
            texto = new TextView(this);
            texto.setText(  Integer.toString( cursor.getInt(2) ));
            fila.addView(texto);            
            //A�ade la fila
            tabla.addView(fila);
            
            cursor.moveToNext();
          }
    }
    
    public void cargaTablaPuntuacionesAmigos(HighScoreList puntuaciones){
        TableLayout tabla = (TableLayout) findViewById(R.id.scores_amigos);
        tabla.removeAllViews();
        TableRow fila;
        TextView texto;        
            
        for(HighScore puntuacion : puntuaciones.getScores()) {
            fila = new TableRow(this);
           
            // A�ade columna name
            texto = new TextView(this);
            texto.setText( puntuacion.getName());
            fila.addView(texto);
            // A�ade columna score
            texto = new TextView(this);
            texto.setText(  Integer.toString( puntuacion.getScoring()));
            fila.addView(texto);            
            //A�ade la fila
            tabla.addView(fila);
         
          }
    }
        
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.activity_scores, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    	switch (item.getItemId()){
            case R.id.scores_borrar:
            	 SQLiteDatabase db = this.myHelperBBDD.getReadableDatabase();
            	 db.execSQL("DELETE FROM scores;");
            	 cargaTablaMisPuntuaciones();
                 break;                
           default:
       	         return super.onOptionsItemSelected(item);
        }
    	return true;	    
    }
    
    
    @SuppressWarnings("deprecation")
	private Cursor getScores(){
    	SQLiteDatabase db = this.myHelperBBDD.getReadableDatabase();
    	Cursor cursor = db.rawQuery("SELECT id,name,score FROM scores ORDER BY score DESC", null);
    	startManagingCursor(cursor);
    	return cursor;
    }
    

    // Clase privada para lanzar la peticion GET http
    
    private class RecuperarScoresAmigos extends AsyncTask<String, HighScoreList, Boolean> {

    	//private HighScoreList lista;    	// Declarar una variable con la lista de scores 
    	
    	@Override
    	protected void onPreExecute() {    
    		super.onPreExecute();
    	}

    	@Override
    	protected Boolean doInBackground(String... params) {
    		/*

    		 */

            while(!isCancelled()){
            	try{
            		
            		HttpResponse response = null;
            		HttpEntity entity = null;
            		HttpClient client = new DefaultHttpClient();
            		HttpGet request = null;
            		List<NameValuePair> pares = new ArrayList<NameValuePair>();
            		pares.add(new BasicNameValuePair("name", params[0]));

            		//  pares.add(new BasicNameValuePair("name", "jsilva"));
              	    //  pares.add(new BasicNameValuePair("name", "jcruizg"));
					try {
						
						request = new HttpGet("http://soletaken.disca.upv.es:8080/WWTBAM/rest/highscores?" + URLEncodedUtils.format(pares, "utf-8"));
						response = client.execute(request);
						entity = response.getEntity();
						
						if (entity != null) {
							InputStream stream = entity.getContent();
							BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
							StringBuilder sb = new StringBuilder();
							String line = null;
							while ((line = reader.readLine()) != null) {
								sb.append(line + "\n");
							}
							stream.close();
							String responseString = sb.toString();
							GsonBuilder builder = new GsonBuilder();
							Gson gson = builder.create();
							JSONObject json = new JSONObject(responseString);
							//Aqui hay que cargar la lista de scores...
							HighScoreList lista =  gson.fromJson(json.toString(), HighScoreList.class);			
							publishProgress(lista);
						}

					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}catch (JSONException e) {
						e.printStackTrace();
					}
            		            		
					//Va actualizando las puntuaciones
            		Thread.sleep(2000);
            	    

            	}catch(InterruptedException e){
            		 Log.d("ObtenerScoresAmigos", "InterruptedException");
            	}        	
            }
    		
    		return true;
    	}

    	@Override
    	protected void onProgressUpdate(HighScoreList... values) {
    		/*
                 Despues aqui hay que leer la lista de scores cargada anteriormente y pintar las filas en la tabla
    		 */
    		cargaTablaPuntuacionesAmigos(values[0]);    		
    		super.onProgressUpdate(values);
    		Thread.currentThread().interrupt();
    	}
    }
}
