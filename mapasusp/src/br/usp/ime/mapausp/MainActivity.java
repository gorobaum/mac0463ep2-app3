package br.usp.ime.mapausp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	private List<MarkerOptions> markers = new ArrayList<MarkerOptions>();

	@SuppressLint("UseSparseArrays")
	private Map<Integer, String> categorias = new HashMap<Integer, String>();

	private GoogleMap googleMap;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		googleMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();
		googleMap.setMyLocationEnabled(true);
		defineWindowAdapter();
		centralizaNaUSP();
		criaListenerParaABusca();
		DBAdapter dbadapter = new DBAdapter(getApplicationContext());
		atualizaBanco(dbadapter);
		desenhaMarkers(dbadapter);
		dbadapter.close();
	}

	private void atualizaBanco(DBAdapter dbadapter) {
		dbadapter.open();
		if (isOnline()) {
			try {
				renovarLocaisNoBanco(dbadapter);
				Toast.makeText(getApplicationContext(),
						"Dados atualizados com sucesso.", Toast.LENGTH_SHORT)
						.show();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			if (dbadapter.isEmpty()) {
				mostraToastEFecha();
			} else {
				Toast.makeText(
						getApplicationContext(),
						"Mostrando dados antigos... Conectar a internet para atualizar.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void mostraToastEFecha() {
		Toast.makeText(
				getApplicationContext(),
				"Você precisa de acesso a internet para baixar os dados pela primeira vez!",
				Toast.LENGTH_SHORT).show();
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(3500); // As I am using LENGTH_LONG in
										// Toast
					MainActivity.this.finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	private void desenhaMarkers(DBAdapter dbadapter) {
		List<Local> locais = dbadapter.getAllLocais();
		for (Local local : locais) {
			MarkerOptions novoMarker = new MarkerOptions();
			novoMarker.title(local.getTitulo());
			novoMarker.snippet(local.getInfo());
			novoMarker.position(new LatLng(local.getLatitude(), local
					.getLongitude()));
			colocaMarkerNoMapa(novoMarker);
		}
	}

	private void renovarLocaisNoBanco(DBAdapter dbadapter)
			throws InterruptedException, ExecutionException, JSONException {
		dbadapter.deleteAllLocais();
		List<Local> locais = criaLocais();
		dbadapter.insertNewLocais(locais);

	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return (netInfo != null) && netInfo.isConnectedOrConnecting();
	}

	private void defineWindowAdapter() {
		googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

			@Override
			public View getInfoWindow(Marker marker) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public View getInfoContents(Marker marker) {
				View contens = getLayoutInflater().inflate(R.layout.info, null);
				String title = marker.getTitle();
				TextView txtTitle = ((TextView) contens
						.findViewById(R.id.categoria));
				txtTitle.setText(title);
				TextView txtType = ((TextView) contens.findViewById(R.id.infos));
				txtType.setText(marker.getSnippet());
				return contens;
			}
		});
	}

	private void centralizaNaUSP() {
		LatLng latLgnUsuario = new LatLng(-23.560052, -46.730926);
		CameraUpdate moveCameraToUsr = CameraUpdateFactory
				.newLatLng(latLgnUsuario);
		CameraUpdate zoomCamera = CameraUpdateFactory.zoomBy(12);
		googleMap.moveCamera(moveCameraToUsr);
		googleMap.moveCamera(zoomCamera);
	}

	private List<Local> criaLocais() throws InterruptedException,
			ExecutionException, JSONException {
		carregaCategorias();
		List<Local> locais = new ArrayList<Local>();
		RequestJson requestJson = new RequestJson();
		requestJson.execute("http://uspservices.deusanyjunior.dj/local.json");
		JSONArray jsonArrayLocais;
		jsonArrayLocais = requestJson.get();
		for (int i = 0; i < jsonArrayLocais.length(); i++) {
			Local novoLocal = criaNovoLocal(jsonArrayLocais.getJSONObject(i));
			locais.add(novoLocal);
		}
		return locais;
	}

	private Local criaNovoLocal(JSONObject place) throws JSONException {
		JSONObject novoLocal = place.getJSONObject("place");

		Local local = new Local();
		String titulo = categorias.get(novoLocal.getInt("placescategory_id"));
		local.setTitulo(titulo);

		Double latitude = novoLocal.getDouble("latitude");
		Double longitude = novoLocal.getDouble("longitude");
		local.setLatitude(latitude);
		local.setLongitude(longitude);

		String info = criarInfoMarker(novoLocal);
		local.setInfo(info);

		return local;
	}

	private String criarInfoMarker(JSONObject novoLocal) throws JSONException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(novoLocal.getString("name") + '\n');
		String endereco = novoLocal.getString("address");
		stringBuilder.append(endereco + '\n');
		String telefone = novoLocal.getString("tel");
		stringBuilder.append("Telefone - " + telefone);
		return stringBuilder.toString();
	}

	private void carregaCategorias() throws InterruptedException,
			ExecutionException, JSONException {
		RequestJson requestJson = new RequestJson();
		requestJson
				.execute("http://uspservices.deusanyjunior.dj/categoriaslocal.json");
		JSONArray jsonArrayCategorias;
		jsonArrayCategorias = requestJson.get();
		preencheMapaCategorias(jsonArrayCategorias);
	}

	private void preencheMapaCategorias(JSONArray jsonArrayCategorias)
			throws JSONException {
		for (int i = 0; i < jsonArrayCategorias.length(); i++) {
			JSONObject novaCategoria = jsonArrayCategorias.getJSONObject(i)
					.getJSONObject("placescategory");
			int idCategoria = novaCategoria.getInt("id");
			String nomeCategoria = novaCategoria.getString("name");
			categorias.put(idCategoria, nomeCategoria);
		}
	}

	private void criaListenerParaABusca() {
		EditText editText = (EditText) findViewById(R.id.buscaMapa);
		editText.setOnEditorActionListener(new MapaBuscaListener(markers,
				googleMap));
	}

	private void colocaMarkerNoMapa(MarkerOptions novoMarker) {
		markers.add(novoMarker);
		googleMap.addMarker(novoMarker);
	}

}