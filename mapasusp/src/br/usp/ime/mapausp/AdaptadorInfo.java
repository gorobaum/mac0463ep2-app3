package br.usp.ime.mapausp;

import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class AdaptadorInfo implements InfoWindowAdapter {

	private View contens;

	public AdaptadorInfo(View contens) {
		this.contens = contens;
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		String title = marker.getTitle();
		TextView txtTitle =  ((TextView) contens.findViewById(R.id.categoria));
		txtTitle.setText(title);
		TextView txtType = ((TextView) contens.findViewById(R.id.infos));
		txtType.setText(marker.getSnippet());
		return contens;
	}

}
