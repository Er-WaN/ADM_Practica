package com.dabdm.practica3;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import modelo.Question;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PlayActivity extends FragmentActivity {

	private List<Question> preguntas=new ArrayList<Question>();
	private Question preguntaActual;
	private boolean refrescarMenu=false;
	private MyHelperBBDD myHelperBBDD;

	//Si alcanza la pregunta 5 guarda el valor si falla, o si llega a la 10, si no lo pierde todo
	public static Integer[] premios = {100,200,300,500,1000,2000,4000,8000,16000,32000,64000,125000,250000,500000,1000000};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        leerXMLPreguntas();   

        this.preguntaActual = restoreData(); //Establece la pregunta en que esta actualmente
        muestraPregunta(  preguntaActual );
        this.myHelperBBDD = new MyHelperBBDD(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.activity_play, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(refrescarMenu){
        	refrescarMenu = false;
	    	for(int i = 0;i<menu.size();i++){
	           menu.getItem(i).setEnabled(true);	
	        }    	
        }
    	return super.onPrepareOptionsMenu(menu);
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		item.setEnabled(false);
		Integer[] botones = {1,2,3,4}; 
		switch (item.getItemId()){
             case R.id.play_telefono:
               botones[preguntaActual.getPhone()-1] = 0;  //Elimina el resto y deja solo la del telefono                         	 
               desactivarBotonesPreguntas(botones);               
               break;
             case R.id.play_cincuenta:
            	 desactivarBotonesPreguntas(preguntaActual.getFifty1(),preguntaActual.getFifty2());  
                 break;
             case R.id.play_audiencia:
            	 botones[preguntaActual.getAudience()-1] = 0;  //Elimina el resto y deja solo la de la audiencia
            	 desactivarBotonesPreguntas(botones);                 
                 break;                 
             default:
            	 return super.onOptionsItemSelected(item);
         }

		return true;		
	}
    

	public void desactivarBotonesPreguntas(Integer...botones){

		Button boton = null;

		for(Integer b : botones){
			switch(b){
			case 1:				
				boton = (Button) findViewById( R.id.answer1);				
				break;
		    case 2:				
			    boton = (Button) findViewById( R.id.answer2);				
			    break;
		    case 3:				
			    boton = (Button) findViewById( R.id.answer3);				
			    break;
		    case 4:				
			    boton = (Button) findViewById( R.id.answer4);				
			    break;			    
		    }
			if(boton!=null){
			   boton.setEnabled(false);
			}
		}
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

	private Question restoreData() {

		Integer numPregunta = 0 ;
		try {
			SharedPreferences preferences = getSharedPreferences("estado.xml",Context.MODE_PRIVATE);		
			//Recupera el n�mero de pregunta que estaba
			numPregunta = preferences.getInt("numPregunta", 1);
			numPregunta = numPregunta-1;

		} catch (Exception e) {
			numPregunta = 0;
		}		

		return preguntas.get(numPregunta);
	}

	private void saveData() {
		SharedPreferences preferences = getSharedPreferences("estado.xml",Context.MODE_PRIVATE);
		EditText text = (EditText) findViewById(R.id.play_numPregunta);
		Editor editor = preferences.edit();
		editor.putInt("numPregunta", Integer.parseInt(text.getText().toString()));
		editor.commit();
	}
    	

    	
	/**
	 * Carga las preguntas que va leyendo del fichero con el formato:
        <question answer1="" answer2="" answer3="" answer4="" audience="2" fifty1=" 1" fifty2=" 4" number=" 1" phone=" 2" right=" 2" text="" />
	 */
	private void leerXMLPreguntas(){

		InputStream inputStream = null;
		XmlPullParser parser = null;
		Question pregunta;
		try {

			//fileInputStream = openFileInput("questions.xml");
			inputStream = getResources().openRawResource(R.raw.questions);			
		    parser = XmlPullParserFactory.newInstance().newPullParser();
			parser.setInput(inputStream, null);

			int eventType = XmlPullParser.START_DOCUMENT;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG && "question".equals(parser.getName())) {
					pregunta = new Question();					
					pregunta.setAnswer1(  parser.getAttributeValue(null, "answer1") );
					pregunta.setAnswer2(  parser.getAttributeValue(null, "answer2") );
					pregunta.setAnswer3(  parser.getAttributeValue(null, "answer3") );
					pregunta.setAnswer4(  parser.getAttributeValue(null, "answer4") );					
					pregunta.setText(     parser.getAttributeValue(null, "text") );		
				    pregunta.setAudience( parser.getAttributeValue(null, "audience") );
				    pregunta.setFifty1(   parser.getAttributeValue(null, "fifty1") );
				    pregunta.setFifty2(   parser.getAttributeValue(null, "fifty2") );
				    pregunta.setNumber(   parser.getAttributeValue(null, "number") );
				    pregunta.setPhone(    parser.getAttributeValue(null, "phone") );
				    pregunta.setRight(    parser.getAttributeValue(null, "right") );
				    preguntas.add(pregunta);
				}
				eventType = parser.next();		
			}

			inputStream.close();

		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}catch (XmlPullParserException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}



	public void muestraPregunta(Question pregunta){

		// Texto de la pregunta
		TextView textView = (TextView) findViewById(R.id.play_pregunta);
		textView.setText( pregunta.getText() );

		// N�mero de pregunta
		TextView text = (TextView) findViewById(R.id.play_numPregunta);
		text.setText( Integer.toString( pregunta.getNumber() ) );
		text.setEnabled(false);
		// Premio que le corresponde para la pregunta
		text = (TextView) findViewById(R.id.play_apuesta);
		text.setText( Integer.toString(  premios[ pregunta.getNumber()-1] ) );// rango preguntas [0-14]
		text.setEnabled(false); 

		// Primera respuesta
		Button boton = (Button) findViewById( R.id.answer1);
		boton.setText( pregunta.getAnswer1() );
		boton.setEnabled(true);
		// Segunda respuesta
		boton = (Button) findViewById( R.id.answer2);
		boton.setText( pregunta.getAnswer2() );
		boton.setEnabled(true);
		// Tercera respuesta
		boton = (Button) findViewById( R.id.answer3);
		boton.setText( pregunta.getAnswer3() );
		boton.setEnabled(true);
		// Cuarta respuesta
		boton = (Button) findViewById( R.id.answer4);
		boton.setText( pregunta.getAnswer4() );
		boton.setEnabled(true);
	}


	public void onClickPreguntaSeleccionada(View v){

		  switch (v.getId()){
          case R.id.answer1:
        	  responderPregunta(1);            
            break;
          case R.id.answer2:
        	  responderPregunta(2);            
            break;
          case R.id.answer3:
        	  responderPregunta(3);            
            break;
          case R.id.answer4:
        	  responderPregunta(4);            
              break;            
          default://No hace nada
        	  break; 
      }

	}    

	private void responderPregunta(Integer numeroPreguntaSeleccionada){

		Integer irAPregunta=0;

		if( numeroPreguntaSeleccionada.equals(preguntaActual.getRight()) ){            		

			//Muestra la siguiente pregunta(mientras no sea la �ltima
			if(preguntaActual.getNumber()<15) {
				MostrarDialogo.mensaje = "Respuesta correcta! Pasa a la siguiente pregunta....";
				new MostrarDialogo().show(getSupportFragmentManager(), "Tag interno");				
				irAPregunta = preguntaActual.getNumber();

			}else{
				MostrarDialogo.mensaje = "Respuesta correcta, ha ganado el concurso, se lleva como premio " +  premios[ preguntaActual.getNumber()-1] ;
				new MostrarDialogo().show(getSupportFragmentManager(), "Tag interno");
				irAPregunta = 0; //Vuelta a empezar
			}
		}else{
			String mensajeNOK="Respuesta incorrectam juego terminado!";
			if(preguntaActual.getNumber()>=5 && preguntaActual.getNumber()<10){
				mensajeNOK = mensajeNOK + " Su premio es de " + premios[4]; // En el rango de preguntas [5-9] se lleva el premio de la quinta si falla 
			}else if(preguntaActual.getNumber()>10){
				mensajeNOK = mensajeNOK + " Su premio es de " + premios[9]; // En el rango de preguntas [10-15] se lleva el premio de la decima si falla
			}

			//Guarda la puntuaci�n obtenida en la bbdd
			SQLiteDatabase db = this.myHelperBBDD.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT max(id) AS id FROM scores", null);
			cursor.moveToFirst();
			Integer idMax = cursor.getInt(0);
			db.execSQL("INSERT INTO scores (id,name,score) VALUES ("+(idMax+1) +",'miUsuario',"+ premios[preguntaActual.getNumber()-1]+");");

			MostrarDialogo.mensaje = mensajeNOK;
			new MostrarDialogo().show(getSupportFragmentManager(), "TEXTO VACIAO");

			irAPregunta = 0;
			refrescarMenu = true; //Activar los items de menu desactivos
		}

        preguntaActual = preguntas.get(irAPregunta);
        muestraPregunta(  preguntaActual );
	}



	// Ver por que no funciona lo de los DIALOGOS!!!!!!!!!!!!!
	private static class MostrarDialogo extends DialogFragment {

		public static String mensaje = "PRUEBA QUE SALE EL MENSAJE";


		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage( mensaje );
			return builder.create();
		}

	}
}