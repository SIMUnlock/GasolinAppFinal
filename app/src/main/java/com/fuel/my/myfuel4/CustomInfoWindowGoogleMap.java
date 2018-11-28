package com.fuel.my.myfuel4;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private final LayoutInflater mInflater;
    public static View popup;

    public CustomInfoWindowGoogleMap(LayoutInflater inflater) {
        this.mInflater = inflater;
    }

    @Override public View getInfoWindow(Marker marker) {
         popup = mInflater.inflate(R.layout.map_custom_infowindow, null);
        Gson gson = new Gson();
        Estaciones estaciones = gson.fromJson(marker.getSnippet(),Estaciones.class);

        if(MainActivity.usuarioLogin!=null) {
            if (MainActivity.usuarioLogin.getFavoritos() != null) {


                if (MainActivity.usuarioLogin.getFavoritos().containsKey(estaciones.getPlace_id())) {
                    ImageView imageView = popup.findViewById(R.id.imageViewHeart);
                    imageView.setImageResource(R.drawable.heartselected);
                }
            }
        }

        ((TextView) popup.findViewById(R.id.textViewNombreMarker)).setText(estaciones.getNombre());
        if(estaciones.getPrecio().getRegular()!=null)
            ((TextView) popup.findViewById(R.id.textViewMagnaMarker)).setText("$"+estaciones.getPrecio().getRegular());
        if(estaciones.getPrecio().getPremium()!=null)
            ((TextView) popup.findViewById(R.id.textViewPremiumMarker)).setText("$"+estaciones.getPrecio().getPremium());
        if(estaciones.getPrecio().getDiesel()!=null)
            ((TextView) popup.findViewById(R.id.textViewDieselMarker)).setText("$"+estaciones.getPrecio().getDiesel());
        ((TextView) popup.findViewById(R.id.textViewUpdateMarker)).setText("Actualización: "+estaciones.getPrecio().getActualizacion());


        return popup;
    }

    @Override public View getInfoContents(Marker marker) {
        popup = mInflater.inflate(R.layout.map_custom_infowindow, null);
        Gson gson = new Gson();
        Estaciones estaciones = gson.fromJson(marker.getSnippet(),Estaciones.class);

        if(MainActivity.usuarioLogin.getFavoritos()!=null) {


            if (MainActivity.usuarioLogin.getFavoritos().containsKey(estaciones.getPlace_id())) {
                ImageView imageView = popup.findViewById(R.id.imageViewHeart);
                imageView.setImageResource(R.drawable.heartselected);
            }
        }

        ((TextView) popup.findViewById(R.id.textViewNombreMarker)).setText(estaciones.getNombre());
        if(estaciones.getPrecio().getRegular()!=null)
        ((TextView) popup.findViewById(R.id.textViewMagnaMarker)).setText("$"+estaciones.getPrecio().getRegular());
        if(estaciones.getPrecio().getPremium()!=null)
        ((TextView) popup.findViewById(R.id.textViewPremiumMarker)).setText("$"+estaciones.getPrecio().getPremium());
        if(estaciones.getPrecio().getDiesel()!=null)
        ((TextView) popup.findViewById(R.id.textViewDieselMarker)).setText("$"+estaciones.getPrecio().getDiesel());
        ((TextView) popup.findViewById(R.id.textViewUpdateMarker)).setText("Actualización: "+estaciones.getPrecio().getActualizacion());


        return popup;
    }
}