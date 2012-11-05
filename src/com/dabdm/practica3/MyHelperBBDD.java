package com.dabdm.practica3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyHelperBBDD extends SQLiteOpenHelper {

	
	
	public MyHelperBBDD(Context context) {
		super(context, "myfile.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE scores (id integer PRIMARY KEY, name String, score integer);");
		/*db.execSQL("INSERT INTO scores (id,name,score) VALUES (1,'pepito',2345);");
		db.execSQL("INSERT INTO scores (id,name,score) VALUES (2,'pepito',2234);");
		db.execSQL("INSERT INTO scores (id,name,score) VALUES (3,'pepito',1456);");
		db.execSQL("INSERT INTO scores (id,name,score) VALUES (4,'pepito',987);");*/
		//db.execSQL("commit;");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// No tiene sentido para esta practica
		
	}

}
