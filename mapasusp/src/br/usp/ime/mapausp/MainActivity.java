package br.usp.ime.mapausp;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		new PagodeMAster().execute("http://uspservices.deusanyjunior.dj/categoriaslocal.json");
		
	}

}