package br.usp.ime.mapausp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	private List<MarkerOptions> markers = new ArrayList<MarkerOptions>();

	private GoogleMap googleMap;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		googleMap = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		centralizaMapaNaUsp();
		criaListenerParaABusca();
		criaMarkers();
	}

	private void centralizaMapaNaUsp() {
		// TODO Auto-generated method stub
		
	}

	private void criaMarkers() {
		RequestJson requestJson = new RequestJson();
		requestJson
				.execute("http://uspservices.deusanyjunior.dj/categoriaslocal.json");
		JSONArray jsonArray;
		try {
			jsonArray = requestJson.get();
			for (int i = 0; i < jsonArray.length(); i++) {
				Log.i(EXTRA_MESSAGE, jsonArray.get(i).toString());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		criaNovoMarker(-23.559429, -46.731548, "IME");
	}

	private void criaListenerParaABusca() {
		EditText editText = (EditText) findViewById(R.id.buscaMapa);
		editText.setOnEditorActionListener(new MapaBuscaListener(markers, googleMap));
	}

	private void criaNovoMarker(Double lat, Double log, String nome) {
		MarkerOptions novoMarker = new MarkerOptions().position(
				new LatLng(lat, log)).title(nome);
		markers.add(novoMarker);
		googleMap.addMarker(novoMarker);
	}

}