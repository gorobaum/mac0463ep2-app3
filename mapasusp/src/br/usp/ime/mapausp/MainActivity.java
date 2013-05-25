package br.usp.ime.mapausp;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		RequestJson macaco = new RequestJson();
		macaco.execute("http://uspservices.deusanyjunior.dj/categoriaslocal.json");
		String json = null;
		try {
			json = macaco.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				Log.i(EXTRA_MESSAGE, jsonArray.get(i).toString());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		atualizarMarcadores(-23.559429, -46.731548);
		Geocoder geocoder = new Geocoder(getApplicationContext());
		try {
			geocoder.getFromLocationName("IMEZÃO TA LIGADO", 5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	private void atualizarMarcadores(Double lat, Double log) {
		GoogleMap mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.addMarker(new MarkerOptions()
		        .position(new LatLng(lat, log))
		        .title("IMEZÃO TA LIGADO"));
	}
}