package br.usp.ime.mapausp;

import java.util.List;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaBuscaListener implements OnEditorActionListener {

	private List<MarkerOptions> markers;
	private GoogleMap googleMap;

	public MapaBuscaListener(List<MarkerOptions> markers, GoogleMap googleMap) {
		this.markers = markers;
		this.googleMap = googleMap;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		boolean handled = false;
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			String markProcurada = v.getText().toString();
			for (MarkerOptions marker : markers) {
				if (marker.getTitle().equalsIgnoreCase(markProcurada)) {
					CameraUpdate moveCameraToMarker = CameraUpdateFactory
							.newLatLng(marker.getPosition());
					CameraUpdate zoomCamera = CameraUpdateFactory.zoomBy(14);
					googleMap.moveCamera(moveCameraToMarker);
					googleMap.moveCamera(zoomCamera);
				}
			}
			handled = true;
		}
		return handled;
	}
}
