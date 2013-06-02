package br.usp.ime.mapausp;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

	public static final String ID = "_id";
	public static final String TITULO = "titulo";
	public static final String INFO = "info";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	private static final String TAG = "DBAdapter";

	private static final String DATABASE_NAME = "ep2";
	private static final String DATABASE_TABLE = "locais";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ DATABASE_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TITULO + " TEXT NOT NULL, " + INFO + " TEXT NOT NULL, "
			+ LATITUDE + " REAL NOT NULL, " + LONGITUDE + " REAL NOT NULL);";

	private final Context context;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);
		}
	}

	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		DBHelper.close();
	}

	public List<Long> insertNewLocais(List<Local> locais) {
		List<Long> ids = new ArrayList<Long>();
		for (Local local : locais) {
			ids.add(insertLocal(local));
		}
		return ids;
	}

	public long insertLocal(Local local) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(TITULO, local.getTitulo());
		initialValues.put(INFO, local.getInfo());
		initialValues.put(LATITUDE, local.getLatitude());
		initialValues.put(LONGITUDE, local.getLongitude());
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	public boolean deleteLocal(long rowId) {
		return db.delete(DATABASE_TABLE, ID + "=" + rowId, null) > 0;
	}

	public boolean deleteAllLocais() {
		return db.delete(DATABASE_TABLE, null, null) > 0;
	}

	public List<Local> getAllLocais() {
		Cursor cursor = getAllLocaisCursor();
		List<Local> locais = new ArrayList<Local>();
		while (cursor.moveToNext()) {
			Local local = new Local();
			local.setTitulo(cursor.getString(1));
			local.setInfo(cursor.getString(2));
			local.setLatitude(cursor.getDouble(3));
			local.setLongitude(cursor.getDouble(4));
			locais.add(local);
		}
		return locais;
	}

	public Cursor getAllLocaisCursor() {
		Cursor cursor = db.query(DATABASE_TABLE, new String[] { ID, TITULO,
				INFO, LATITUDE, LONGITUDE }, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor getLocal(long rowId) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] { ID,
				TITULO, INFO }, ID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor getLocalPorTitulo(String titulo) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] { ID,
				TITULO, INFO, LATITUDE, LONGITUDE }, TITULO + "LIKE"
				+ "%titulo", null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public boolean updateLocal(long rowId, Local local) {
		ContentValues args = new ContentValues();
		args.put(TITULO, local.getTitulo());
		args.put(INFO, local.getInfo());
		args.put(LATITUDE, local.getLatitude());
		args.put(LONGITUDE, local.getLongitude());
		return db.update(DATABASE_TABLE, args, ID + "=" + rowId, null) > 0;
	}

	public boolean isEmpty() {
		Cursor cursor = getAllLocaisCursor();
		return !cursor.moveToFirst();
	}
}