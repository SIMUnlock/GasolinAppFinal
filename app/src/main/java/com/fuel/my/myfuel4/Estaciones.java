package com.fuel.my.myfuel4;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

public class Estaciones implements ClusterItem, Serializable {

        private String place_id;
        private String name;
        private String x;
        private String y;
        private String category;
        private Precios miPrecio;
        private int posicion;


        public Estaciones(String place_id,
                     String name,
                     String  x,
                     String y,
                     String category
                          ) {
            this.place_id = place_id;
            this.name = name;
            this.x = x;
            this.y = y;
            this.category = category;
        }

        public String getX() {
            return x;
        }

    public String getY() {
        return y;
    }

    public String getNombre() {
        return name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public String getCategory() {
        return category;
    }

    public void setPrecio(Precios miPrecio){
            this.miPrecio=miPrecio;
        }

    public Precios getPrecio(){
        return miPrecio;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(Double.valueOf(y),Double.valueOf(x));
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSnippet() {
        Gson gson = new Gson();

        return gson.toJson(this);
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }
}
