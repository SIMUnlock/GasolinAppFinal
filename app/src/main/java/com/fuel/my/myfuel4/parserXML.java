package com.fuel.my.myfuel4;

import android.util.Xml;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class parserXML {


    private static final String ns = null;

    private static final String ETIQUETA_PLACES = "places";
    private static final String ETIQUETA_PLACE = "place";
    private static final String ETIQUETA_PLACEID = "place_id";
    private static final String ETIQUETA_NAME = "name";

    private static final String ETIQUETA_CATEGORY = "category";
    private static final String ETIQUETA_LOCATION = "location";
    private static final String ETIQUETA_X = "x";
    private static final String ETIQUETA_Y = "y";
    private static final String ETIQUETA_GASPRICE ="gas_price";
    private  LatLng miUbicacion = null;

    public parserXML(LatLng miUbicacion){
        this.miUbicacion=miUbicacion;
    }




    public List<Estaciones> parsear(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(in, null);
            parser.nextTag();
            return leerEstaciones(parser);
        } finally {
            in.close();
        }
    }

    public List<Precios> parsearPrecios(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(in, null);
            parser.nextTag();
            return leerEstacionPrecios(parser);
        } finally {
            in.close();
        }
    }

    private List<Precios> leerEstacionPrecios(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        List<Precios> listaPrecios = new ArrayList<Precios>();

        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_PLACES);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String nombreEtiqueta = parser.getName();

            if (nombreEtiqueta.equals(ETIQUETA_PLACE)) {

                listaPrecios.add(leerEstaPrecio(parser));


            } else {
                System.out.println("Aqui");
                saltarEtiqueta(parser);
            }
        }
        return listaPrecios;
    }

    private Precios leerEstaPrecio(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_PLACE);

        String placeid=null;

        String regular = null;
        String premium = null;
        String diesel=null;
        String actualizacion =null;

        placeid=parser.getAttributeValue(null,ETIQUETA_PLACEID);


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            switch (name) {
                case ETIQUETA_GASPRICE:
                    if("regular".equals(parser.getAttributeValue(null,"type"))){
                        actualizacion= parser.getAttributeValue(null,"update_time");
                        regular= leerPrecio(parser);

                    } else
                    if("premium".equals(parser.getAttributeValue(null,"type"))){
                        actualizacion= parser.getAttributeValue(null,"update_time");
                        premium= leerPrecio(parser);

                    }
                    else{
                        actualizacion= parser.getAttributeValue(null,"update_time");
                        diesel=leerPrecio(parser);

                    }


                    break;

                default:
                    saltarEtiqueta(parser);
                    break;
            }
        }
        return new Precios(placeid,
                regular,
                premium,
                diesel,
                actualizacion);
    }

    private String leerPrecio(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_GASPRICE);
        String precio = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_GASPRICE);
        return precio;
    }


    private List<Estaciones> leerEstaciones(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        List<Estaciones> listaEstaciones = new ArrayList<Estaciones>();

        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_PLACES);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String nombreEtiqueta = parser.getName();

            if (nombreEtiqueta.equals(ETIQUETA_PLACE)) {

                listaEstaciones.add(leerEstacion(parser));
                if(listaEstaciones.get(listaEstaciones.size()-1).getX()==null ||listaEstaciones.get(listaEstaciones.size()-1).getY()==null  )
                    listaEstaciones.remove(listaEstaciones.size()-1);

            } else {
                System.out.println("Aqui");
                saltarEtiqueta(parser);
            }
        }
        return listaEstaciones;
    }

    private Estaciones leerEstacion(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_PLACE);
        String nombre = null;
        String x= null;
        String y=null;
        String placeid=null;

        String category=null;

        placeid=parser.getAttributeValue(null,ETIQUETA_PLACEID);


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            switch (name) {
                                case ETIQUETA_NAME:
                    nombre = leerName(parser);
                    break;
                case ETIQUETA_CATEGORY:
                    category = leerCategory(parser);
                    break;

                case ETIQUETA_LOCATION:
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        String name2 = parser.getName();

                        switch (name2) {

                            case ETIQUETA_X:
                                x=leerX(parser);

                                break;
                            case ETIQUETA_Y:
                                y=leerY(parser);

                                break;
                            default:
                                saltarEtiqueta(parser);
                                break;
                        }
                    }

                    break;
                default:
                    saltarEtiqueta(parser);
                    break;
            }
        }
        return new Estaciones(placeid,
                nombre,
                x,
                y,
                category);
    }

    private String leerName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_NAME);
        String nombre = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_NAME);
        return nombre;
    }

    private String leerCategory(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_CATEGORY);
        String category = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_CATEGORY);
        return category;
    }


    private String leerX(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_X);
        String x = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_X);
        return x;
    }

    private String leerY(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_Y);
        String y = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_Y);
        return y;
    }




    private String obtenerTexto(XmlPullParser parser) throws IOException, XmlPullParserException {
        String resultado = "";
        if (parser.next() == XmlPullParser.TEXT) {
            resultado = parser.getText();
            parser.nextTag();
        }
        return resultado;
    }


    private void saltarEtiqueta(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }


}
