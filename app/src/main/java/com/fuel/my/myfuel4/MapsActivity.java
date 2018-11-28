package com.fuel.my.myfuel4;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.ramotion.fluidslider.FluidSlider;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import kotlin.Unit;
import safety.com.br.android_shake_detector.core.ShakeCallback;
import safety.com.br.android_shake_detector.core.ShakeDetector;
import safety.com.br.android_shake_detector.core.ShakeOptions;
import java.util.HashMap;
import kotlin.jvm.functions.Function0;

public class MapsActivity extends Fragment implements OnMapReadyCallback {
    public static int tipoGasolina=1;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LatLng miUbicacion;
    private List<Precios> myPrices = null;
    public static List<Estaciones> myStations = null;
    private ShakeDetector shakeDetector;
    private int controlDownload=0;
    private int controlShake=0;
    private Polyline route;
    private List<Estaciones> copiaEstaciones=null;
    private ClusterManager<Estaciones> mClusterManager;
    public static float rango=0.009f;
    List<Estaciones> entries = null;
    private Marker userMarker;
    private boolean isMarkerRotating;
    private FusedLocationProviderClient mFusedLocationClient;
    private int voyAGasolinera=0;
    private int doubleTap=0;
    public MapsActivity() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_maps, container, false);
    }

    RadioGroup mMode;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());


        final int max = 50;
        final int min = 1;
        final int total = max - min;

        ShakeOptions options = new ShakeOptions()
                .background(false)
                .interval(1000)
                .shakeCount(2)
                .sensibility(2.0f);

        this.shakeDetector = new ShakeDetector(options).start(getContext(), new ShakeCallback() {
            @Override
            public void onShake() {

                if(controlShake==0 &&myStations!=null){

                    if(copiaEstaciones.size()==0)
                    {
                        Toast.makeText(getContext(),"No hay estaciones cerca",Toast.LENGTH_SHORT).show();
                        return;

                    }
                    LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View popupView = layoutInflater.inflate(R.layout.popup_window, null);
                    popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    final PopupWindow popupWindow = new PopupWindow(popupView, popupView.getMeasuredWidth(), popupView.getMeasuredHeight(), true);
                    popupWindow.showAtLocation(popupView, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setIgnoreCheekPress();

                    if(tipoGasolina==1){
                        if(copiaEstaciones.size()==0)
                            return;
                        if(copiaEstaciones.size()==1){
                            TextView textviewGas1= (TextView) popupView.findViewById(R.id.nomGas1);
                            textviewGas1.setText(copiaEstaciones.get(0).getNombre());
                        }
                        if(copiaEstaciones.size()==2){
                            TextView textviewGas1= (TextView) popupView.findViewById(R.id.nomGas1);
                            textviewGas1.setText(copiaEstaciones.get(0).getNombre());
                            TextView textviewGas2= (TextView) popupView.findViewById(R.id.nomGas2);
                            textviewGas2.setText(copiaEstaciones.get(1).getNombre());
                        }
                        if (copiaEstaciones.size()>2)
                        {
                            TextView textviewGas1= (TextView) popupView.findViewById(R.id.nomGas1);
                            textviewGas1.setText(copiaEstaciones.get(0).getNombre());
                            TextView textviewGas2= (TextView) popupView.findViewById(R.id.nomGas2);
                            textviewGas2.setText(copiaEstaciones.get(1).getNombre());
                            TextView textviewGas3= (TextView) popupView.findViewById(R.id.nomGas3);
                            textviewGas3.setText(copiaEstaciones.get(2).getNombre());

                        }

                        if(copiaEstaciones.size()==1){
                            RadioButton RDGas1 = (RadioButton) popupView.findViewById(R.id.radioButtonGaso1);
                            RDGas1.setText(copiaEstaciones.get(0).getPrecio().getRegular());
                            RDGas1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.water,0,0,0);
                        }
                        if(copiaEstaciones.size()==2){
                            RadioButton RDGas1 = (RadioButton) popupView.findViewById(R.id.radioButtonGaso1);
                            RDGas1.setText(copiaEstaciones.get(0).getPrecio().getRegular());
                            RDGas1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.water,0,0,0);
                            RadioButton RDGas2 = (RadioButton) popupView.findViewById(R.id.radioButtonGaso2);
                            RDGas2.setText(copiaEstaciones.get(1).getPrecio().getRegular());
                            RDGas2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.water,0,0,0);
                        }
                        if (copiaEstaciones.size()>2)
                        {
                            RadioButton RDGas1 = (RadioButton) popupView.findViewById(R.id.radioButtonGaso1);
                            RDGas1.setText(copiaEstaciones.get(0).getPrecio().getRegular());
                            RDGas1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.water,0,0,0);
                            RadioButton RDGas2 = (RadioButton) popupView.findViewById(R.id.radioButtonGaso2);
                            RDGas2.setText(copiaEstaciones.get(1).getPrecio().getRegular());
                            RDGas2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.water,0,0,0);
                            RadioButton RDGas3 = (RadioButton) popupView.findViewById(R.id.radioButtonGaso3);
                            RDGas3.setText(copiaEstaciones.get(2).getPrecio().getRegular());
                            RDGas3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.water,0,0,0);

                        }




                    }
                    if(tipoGasolina==2){
                        if(copiaEstaciones.size()==0)
                            return;
                        if(copiaEstaciones.size()==1){
                            TextView textviewGas1= (TextView) popupView.findViewById(R.id.nomGas1);
                            textviewGas1.setText(copiaEstaciones.get(0).getNombre());
                        }
                        if(copiaEstaciones.size()==2){
                            TextView textviewGas1= (TextView) popupView.findViewById(R.id.nomGas1);
                            textviewGas1.setText(copiaEstaciones.get(0).getNombre());
                            TextView textviewGas2= (TextView) popupView.findViewById(R.id.nomGas2);
                            textviewGas2.setText(copiaEstaciones.get(1).getNombre());
                        }
                        if (copiaEstaciones.size()>2)
                        {
                            TextView textviewGas1= (TextView) popupView.findViewById(R.id.nomGas1);
                            textviewGas1.setText(copiaEstaciones.get(0).getNombre());
                            TextView textviewGas2= (TextView) popupView.findViewById(R.id.nomGas2);
                            textviewGas2.setText(copiaEstaciones.get(1).getNombre());
                            TextView textviewGas3= (TextView) popupView.findViewById(R.id.nomGas3);
                            textviewGas3.setText(copiaEstaciones.get(2).getNombre());

                        }

                        if(copiaEstaciones.size()==1){
                            RadioButton RDGas1 = (RadioButton) popupView.findViewById(R.id.radioButtonGaso1);
                            RDGas1.setText(copiaEstaciones.get(0).getPrecio().getPremium());
                            RDGas1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.premium,0,0,0);
                        }
                        if(copiaEstaciones.size()==2){
                            RadioButton RDGas1 = (RadioButton) popupView.findViewById(R.id.radioButtonGaso1);
                            RDGas1.setText(copiaEstaciones.get(0).getPrecio().getPremium());
                            RDGas1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.premium,0,0,0);
                            RadioButton RDGas2 = (RadioButton) popupView.findViewById(R.id.radioButtonGaso2);
                            RDGas2.setText(copiaEstaciones.get(1).getPrecio().getPremium());
                            RDGas2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.premium,0,0,0);
                        }
                        if (copiaEstaciones.size()>2)
                        {
                            RadioButton RDGas1 = (RadioButton) popupView.findViewById(R.id.radioButtonGaso1);
                            RDGas1.setText(copiaEstaciones.get(0).getPrecio().getPremium());
                            RDGas1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.premium,0,0,0);
                            RadioButton RDGas2 = (RadioButton) popupView.findViewById(R.id.radioButtonGaso2);
                            RDGas2.setText(copiaEstaciones.get(1).getPrecio().getPremium());
                            RDGas2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.premium,0,0,0);
                            RadioButton RDGas3 = (RadioButton) popupView.findViewById(R.id.radioButtonGaso3);
                            RDGas3.setText(copiaEstaciones.get(2).getPrecio().getPremium());
                            RDGas3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.premium,0,0,0);

                        }

                    }

                    if(tipoGasolina==3){
                        if(copiaEstaciones.size()==0)
                            return;
                        if(copiaEstaciones.size()==1){
                            TextView textviewGas1= (TextView) popupView.findViewById(R.id.nomGas1);
                            textviewGas1.setText(copiaEstaciones.get(0).getNombre());
                        }
                        if(copiaEstaciones.size()==2){
                            TextView textviewGas1= (TextView) popupView.findViewById(R.id.nomGas1);
                            textviewGas1.setText(copiaEstaciones.get(0).getNombre());
                            TextView textviewGas2= (TextView) popupView.findViewById(R.id.nomGas2);
                            textviewGas2.setText(copiaEstaciones.get(1).getNombre());
                        }
                        if (copiaEstaciones.size()>2)
                        {
                            TextView textviewGas1= (TextView) popupView.findViewById(R.id.nomGas1);
                            textviewGas1.setText(copiaEstaciones.get(0).getNombre());
                            TextView textviewGas2= (TextView) popupView.findViewById(R.id.nomGas2);
                            textviewGas2.setText(copiaEstaciones.get(1).getNombre());
                            TextView textviewGas3= (TextView) popupView.findViewById(R.id.nomGas3);
                            textviewGas3.setText(copiaEstaciones.get(2).getNombre());

                        }

                        if(copiaEstaciones.size()==1){
                            RadioButton RDGas1 = (RadioButton) popupView.findViewById(R.id.radioButtonGaso1);
                            RDGas1.setText(copiaEstaciones.get(0).getPrecio().getDiesel());
                            RDGas1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.disel,0,0,0);
                        }
                        if(copiaEstaciones.size()==2){
                            RadioButton RDGas1 = (RadioButton) popupView.findViewById(R.id.radioButtonGaso1);
                            RDGas1.setText(copiaEstaciones.get(0).getPrecio().getDiesel());
                            RDGas1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.disel,0,0,0);
                            RadioButton RDGas2 = (RadioButton) popupView.findViewById(R.id.radioButtonGaso2);
                            RDGas2.setText(copiaEstaciones.get(1).getPrecio().getDiesel());
                            RDGas2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.disel,0,0,0);
                        }
                        if (copiaEstaciones.size()>2)
                        {
                            RadioButton RDGas1 = (RadioButton) popupView.findViewById(R.id.radioButtonGaso1);
                            RDGas1.setText(copiaEstaciones.get(0).getPrecio().getDiesel());
                            RDGas1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.disel,0,0,0);
                            RadioButton RDGas2 = (RadioButton) popupView.findViewById(R.id.radioButtonGaso2);
                            RDGas2.setText(copiaEstaciones.get(1).getPrecio().getDiesel());
                            RDGas2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.disel,0,0,0);
                            RadioButton RDGas3 = (RadioButton) popupView.findViewById(R.id.radioButtonGaso3);
                            RDGas3.setText(copiaEstaciones.get(2).getPrecio().getDiesel());
                            RDGas3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.disel,0,0,0);

                        }

                    }

                    mMode = (RadioGroup) popupView.findViewById(R.id.radioGroupGas);
                    mMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            switch (mMode.getCheckedRadioButtonId()) {
                                case R.id.radioButtonGaso1:
                                    voyAGasolinera=1;
                                    LatLng origin1 = miUbicacion;
                                    LatLng dest1=null;
                                    if(voyAGasolinera==1)
                                        dest1= new LatLng(Double.parseDouble(copiaEstaciones.get(0).getY()),Double.parseDouble(copiaEstaciones.get(0).getX()));
                                    String url = getUrl(origin1, dest1);
                                    controlShake++;
                                    FetchUrl FetchUrl = new FetchUrl();

                                    FetchUrl.execute(url);

                                    voyAGasolinera=0;
                                    popupWindow.dismiss();
                                    break;
                                case R.id.radioButtonGaso2:
                                    if(copiaEstaciones.size()==1){
                                        Toast.makeText(getContext(),"No hay estaciones disponibles",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    voyAGasolinera=2;
                                    if(voyAGasolinera==0)
                                        return;
                                    LatLng origin = miUbicacion;
                                    LatLng dest=null;
                                    if(voyAGasolinera==2)
                                        dest= new LatLng(Double.parseDouble(copiaEstaciones.get(1).getY()),Double.parseDouble(copiaEstaciones.get(1).getX()));
                                    String url2 = getUrl(origin, dest);

                                    FetchUrl FetchUrl2 = new FetchUrl();
                                    controlShake++;
                                    FetchUrl2.execute(url2);

                                    voyAGasolinera=0;
                                    popupWindow.dismiss();
                                    break;
                                case R.id.radioButtonGaso3:
                                    if(copiaEstaciones.size()<=2){
                                        Toast.makeText(getContext(),"No hay estaciones disponibles",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    voyAGasolinera=3;
                                    if(voyAGasolinera==0)
                                        return;
                                    LatLng origin3 = miUbicacion;
                                    LatLng dest3=null;
                                    if(voyAGasolinera==3)
                                        dest3= new LatLng(Double.parseDouble(copiaEstaciones.get(2).getY()),Double.parseDouble(copiaEstaciones.get(2).getX()));
                                    String url3 = getUrl(origin3, dest3);

                                    FetchUrl FetchUrl3 = new FetchUrl();
                                    controlShake++;
                                    FetchUrl3.execute(url3);

                                    voyAGasolinera=0;
                                    popupWindow.dismiss();
                                    break;
                                default:
                                    voyAGasolinera=0;
                                    controlShake=0;
                                    break;
                            }
                        }
                    });




                }else  {

                    if(route!=null && myStations!=null)
                    {
                        route.remove();
                        controlShake=0;

                    }
                }
            }
        });
        final FluidSlider slider = getView().findViewById(R.id.fluidSlider);

        slider.setEndTrackingListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                if(mClusterManager!=null){
                    if (route!=null&&route.getPoints()!=null)
                        route.remove();
                    mClusterManager.clearItems();
                    mClusterManager.cluster();

                    myStations.forEach(test -> test.setPosicion(0));


                    copiaEstaciones=myStations;



                    copiaEstaciones=copiaEstaciones.stream()
                            .filter(p -> !((Double.parseDouble(p.getX())<=miUbicacion.longitude-rango ||
                                    Double.parseDouble(p.getX())>=miUbicacion.longitude+rango)) ).collect(Collectors.toList())
                    ;

                    copiaEstaciones = copiaEstaciones.stream()
                            .filter(p -> !((Double.parseDouble(p.getY())<=miUbicacion.latitude-rango ||
                                    Double.parseDouble(p.getY())>=miUbicacion.latitude+rango)) ).collect(Collectors.toList())
                    ;


                    mClusterManager = new ClusterManager<>(getContext(), mMap);

                    mMap.setOnCameraIdleListener(mClusterManager);
                    mMap.setOnMarkerClickListener(mClusterManager);
                    mMap.setOnInfoWindowClickListener(mClusterManager);





                    for (int i = 0; i < copiaEstaciones.size(); i++) {
                        if(copiaEstaciones.get(i).getPrecio()==null)
                        {
                            copiaEstaciones.remove(i);
                            continue;
                        }
                        if(tipoGasolina==1){
                            if (copiaEstaciones.get(i).getPrecio().getRegular()==null) {
                                copiaEstaciones.remove(i);
                                i--;
                                continue;
                            }
                        }
                        if(tipoGasolina==2){

                            if (copiaEstaciones.get(i).getPrecio().getPremium()==null) {
                                copiaEstaciones.remove(i);
                                i--;
                                continue;
                            }
                        }
                        if(tipoGasolina==3){
                            if (copiaEstaciones.get(i).getPrecio().getDiesel()==null) {
                                copiaEstaciones.remove(i);
                                i--;
                                continue;
                            }
                        }



                    }

                    if(tipoGasolina==1){
                        copiaEstaciones.sort((f1, f2) -> Float.valueOf(f1.getPrecio().getRegular()).compareTo(Float.valueOf(f2.getPrecio().getRegular()) ));
                    }
                    if(tipoGasolina==2){

                        copiaEstaciones.sort((f1, f2) -> Float.valueOf(f1.getPrecio().getPremium()).compareTo(Float.valueOf(f2.getPrecio().getPremium()) ));

                    }

                    if(tipoGasolina==3){

                        copiaEstaciones.sort((f1, f2) -> Float.valueOf(f1.getPrecio().getDiesel()).compareTo(Float.valueOf(f2.getPrecio().getDiesel()) ));

                    }

                    if(copiaEstaciones.size()==0)
                        return null;

                    if(copiaEstaciones.size()==1)
                        copiaEstaciones.get(0).setPosicion(1);

                    else if (copiaEstaciones.size()==2){
                        copiaEstaciones.get(0).setPosicion(1);
                        copiaEstaciones.get(1).setPosicion(2);
                    }else
                    {
                        copiaEstaciones.get(0).setPosicion(1);
                        copiaEstaciones.get(1).setPosicion(2);
                        copiaEstaciones.get(2).setPosicion(3);
                    }



                    mClusterManager.addItems(copiaEstaciones);
                    mClusterManager.setRenderer(new MyClusterRenderer(getContext(), mMap,mClusterManager));
                    CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(LayoutInflater.from(getContext()));
                    mClusterManager.getMarkerCollection()
                            .setOnInfoWindowAdapter(customInfoWindow);

                    mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

                    mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {

                        @Override
                        public void onInfoWindowClose(Marker marker) {
                            doubleTap=0;
                        }
                    });
                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            doubleTap++;
                            if(doubleTap==2){
                                if(MainActivity.user!=null){
                                    DatabaseReference updateData = FirebaseDatabase.getInstance()
                                            .getReference("usuarios")
                                            .child(MainActivity.user.getUid());
                                    Map<String, Object> postValues = new HashMap<String,Object>();
                                    Gson gson = new Gson();
                                    Estaciones estacion = gson.fromJson(marker.getSnippet(),Estaciones.class);

                                    if(MainActivity.usuarioLogin.getFavoritos()==null){
                                        HashMap<String,String> favos = new HashMap<>(0);
                                        favos.put(estacion.getPlace_id(),estacion.getPlace_id());
                                        MainActivity.usuarioLogin.setFavoritos(favos);
                                        postValues.put("favoritos",MainActivity.usuarioLogin.getFavoritos());
                                        updateData.updateChildren(postValues);
                                    }else if(MainActivity.usuarioLogin.getFavoritos()!=null && !MainActivity.usuarioLogin.getFavoritos().containsKey(estacion.getPlace_id())){
                                        MainActivity.usuarioLogin.getFavoritos().put(estacion.getPlace_id(),estacion.getPlace_id());

                                        updateData.child("favoritos").setValue(MainActivity.usuarioLogin.getFavoritos());
                                    }else{
                                        MainActivity.usuarioLogin.getFavoritos().remove(estacion.getPlace_id());
                                        updateData.child("favoritos").setValue(MainActivity.usuarioLogin.getFavoritos());
                                    }


                                    marker.hideInfoWindow();
                                    marker.showInfoWindow();
                                }else{
                                    Toast.makeText(getContext(),"Inicie sesión para empezar a agregar favoritos ♥",Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                    });
                    mClusterManager.cluster();






                }

                return Unit.INSTANCE;
            }
        });


        slider.setPositionListener(pos -> {
            final String value = String.valueOf( (int)(min + total * pos) );
            rango=0.009f*(Float.valueOf(value));
            slider.setBubbleText(value );
            return Unit.INSTANCE;
        });

        slider.setStartText(String.valueOf(min)+ "Km");
        slider.setEndText(String.valueOf(max)+ "Km");

        final ImageView v = (ImageView) getView().findViewById(R.id.buttonPos);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        giveMeMyLocation();
                        break;
                    }

                }
                return true;
            }
        });





        if(getActivity()!=null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager() .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
                buildGoogleApiClient();
                createLocationRequest();
                Loc_Update();


            }
        }




    }


    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location!=null){

                                        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addApi(LocationServices.API)
                .build();
    }
    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1);
        locationRequest.setFastestInterval(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void Loc_Update() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(getContext()).checkLocationSettings(builder.build());
        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(locationRequest,new LocationCallback(){
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                if (locationResult == null ) {
                                    return;
                                }

                                if(mMap!=null){

                                    for (Location location : locationResult.getLocations()) {


                                        if (controlDownload == 0) {


                                            miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());

                                            File file = new File(getContext().getFilesDir() + "/estaciones.txt");
                                            if (file.exists()) {

                                                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                                                Date today = new Date();


                                                Date todayWithZeroTime = null;
                                                try {
                                                    todayWithZeroTime = formatter.parse(formatter.format(today));
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                Date fileDateWithZeroTime = null;
                                                try {
                                                    fileDateWithZeroTime = formatter.parse(formatter.format(file.lastModified()));
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                                if(todayWithZeroTime.compareTo(fileDateWithZeroTime)>0){
                                                    file.delete();
                                                    Log.i("Archivo","UPDATE ");
                                                    new TareaDescargaXmlPrecios().execute("https://publicacionexterna.azurewebsites.net/publicaciones/prices");

                                                    new TareaDescargaXml().execute("https://publicacionexterna.azurewebsites.net/publicaciones/places");

                                                }else {
                                                    Log.i("Archivo","NO UPDATE - LEYENDO");
                                                    FileInputStream f = null;

                                                    try {
                                                        f = new FileInputStream(file);
                                                        int bufferSize = 16 * 1024;

                                                        ObjectInputStream o = new ObjectInputStream(new BufferedInputStream(f, bufferSize));

                                                        myStations = (List<Estaciones>) o.readObject();

                                                        o.close();
                                                        f.close();
                                                    } catch (FileNotFoundException e) {
                                                        e.printStackTrace();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    } catch (ClassNotFoundException e) {
                                                        e.printStackTrace();
                                                    }
                                                    dibujaEstaciones(myStations);

                                                }




                                            } else  {


                                                new TareaDescargaXmlPrecios().execute("https://publicacionexterna.azurewebsites.net/publicaciones/prices");

                                                new TareaDescargaXml().execute("https://publicacionexterna.azurewebsites.net/publicaciones/places");
                                            }

                                            userMarker = mMap.addMarker(new MarkerOptions()
                                                    .position(miUbicacion)
                                                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.navigation))
                                                    .anchor(0.5f, 0.5f)

                                                    .flat(true));

                                            controlDownload = 1;

                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(), 16.0f));
                                            controlShake = 0;
                                        }

                                        if (lastPos == null)
                                            lastPos = new LatLng(location.getLatitude(), location.getLongitude());

                                        LatLng currentPost = new LatLng(location.getLatitude(), location.getLongitude());
                                        float bearing = bearingBetweenLocations(lastPos, currentPost);
                                        rotateMarker(userMarker, lastPos, currentPost, bearing);
                                        if (controlShake == 1) {

                                            CameraPosition camPos = CameraPosition
                                                    .builder(
                                                            mMap.getCameraPosition()
                                                    )
                                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                                    .zoom(18)
                                                    .build();
                                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));

                                        }
                                        lastPos = currentPost;
                                        miUbicacion = currentPost;
                                    }

                                }


                            }
                        },null);
                    }
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {

                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                resolvable.startResolutionForResult(getActivity(), 2001);
                                break;
                            } catch (ClassCastException e) {
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }}
        });

    }


    private String getUrl(LatLng origin, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+ "&key=" + getString(R.string.api);
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }



    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";

            try {
                data = downloadUrl(url[0]);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);

                DataParser parser = new DataParser();

                routes = parser.parse(jObject);


            } catch (Exception e) {

                e.printStackTrace();
            }
            return routes;
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(15);
                lineOptions.color(Color.rgb(127,191,63));



            }

            if(lineOptions != null) {
                lineOptions.jointType(JointType.ROUND);
                route=mMap.addPolyline(lineOptions);

            }
            else {
                Log.d("onPostExecute","sin polilineas");
            }
        }
    }


    LatLng lastPos=null;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
      //  mMap.setS(GoogleMap.MAP_TYPE_NORMAL);
      //  mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
         mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        getContext(),R.raw.style));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
             mMap.getUiSettings().setMapToolbarEnabled(false);



    }




    private float bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        float brng =(float) Math.atan2(y, x);

        brng = (float ) Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    private void rotateMarker(final Marker marker,LatLng startLatLng,LatLng toPosition ,final float toRotation) {
        if(!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final LinearInterpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;
                    double lng = t * toPosition.longitude + (1 - t)
                            * startLatLng.longitude;
                    double lat = t * toPosition.latitude + (1 - t)
                            * startLatLng.latitude;
                    marker.setPosition(new LatLng(lat, lng));

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);

                    if (t < 1.0) {
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }

                }
            });
        }


    }



    public void giveMeMyLocation() {

        if(mMap!=null &&miUbicacion!=null){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(miUbicacion,16);
        mMap.animateCamera(cameraUpdate);
        }
    }


    private class TareaDescargaXml extends AsyncTask<String, Void, List<Estaciones>> {

        @Override
        protected List<Estaciones> doInBackground(String... urls) {
            try {
                return parsearXmlDeUrl(urls[0]);
            } catch (IOException e) {
                return null;
            } catch (XmlPullParserException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Estaciones> result) {
            myStations= result;


            Map<String, List<Estaciones>> marksEstaciones = myStations.stream()
                    .collect(Collectors.groupingBy(Estaciones::getPlace_id, Collectors.toList()));


            myPrices = myPrices.stream()
                    .flatMap(o1 -> marksEstaciones.get(o1.getPlace_id()).stream().map(o2 -> {
                        if(o2.getPrecio()!=null)
                        {
                            if( o2.getPrecio().getActualizacion()!=null && o1.getActualizacion()!=null){
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                Date convertedDate1 ;
                                Date convertedDate2 ;
                                try {
                                    convertedDate1 = dateFormat.parse(o2.getPrecio().getActualizacion());
                                    convertedDate2 = dateFormat.parse(o1.getActualizacion());
                                    if(convertedDate2.compareTo(convertedDate1)>0){
                                        o2.getPrecio().setActualizacion(o1.getActualizacion());
                                    }
                                } catch (ParseException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }else if(o2.getPrecio().getActualizacion()==null){
                                o2.getPrecio().setActualizacion(o1.getActualizacion());
                            }
                            if(o1.getRegular()!=null && o2.getPrecio().getRegular()==null)
                                o2.getPrecio().setRegular(o1.getRegular());
                            if(o1.getPremium()!=null && o2.getPrecio().getPremium()==null)
                                o2.getPrecio().setPremium(o1.getPremium());
                            if(o1.getDiesel()!=null && o2.getPrecio().getDiesel()==null)
                                o2.getPrecio().setDiesel(o1.getDiesel());
                        }else
                            o2.setPrecio(o1);
                        return o1;
                    }))

                    .collect(Collectors.toList());



            try {
                FileOutputStream f = new FileOutputStream(new File(getContext().getFilesDir()+"/estaciones.txt"));

                int bufferSize = 16 * 1024;

                ObjectOutputStream o = new ObjectOutputStream(new BufferedOutputStream(f, bufferSize));

                o.writeObject(myStations);
                o.close();
                f.close();
            } catch (FileNotFoundException e) {
                System.out.println(e);
            } catch (IOException e) {
                System.out.println("Error initializing stream");
            }


            dibujaEstaciones(myStations);

        }
    }

    private void dibujaEstaciones(List<Estaciones> myStations){
        copiaEstaciones = myStations;
        copiaEstaciones = copiaEstaciones.stream()
                .filter(p -> !((Double.parseDouble(p.getX())<=miUbicacion.longitude-rango ||
                        Double.parseDouble(p.getX())>=miUbicacion.longitude+rango)) ).collect(Collectors.toList())
        ;

        copiaEstaciones = copiaEstaciones.stream()
                .filter(p -> !((Double.parseDouble(p.getY())<=miUbicacion.latitude-rango ||
                        Double.parseDouble(p.getY())>=miUbicacion.latitude+rango)) ).collect(Collectors.toList())
        ;


        mClusterManager = new ClusterManager<>(getContext(), mMap);

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);




        for (int i = 0; i < copiaEstaciones.size(); i++) {
            if(copiaEstaciones.get(i).getPrecio()==null)
            {
                copiaEstaciones.remove(i);
                continue;
            }
            if(tipoGasolina==1){
                if (copiaEstaciones.get(i).getPrecio().getRegular()==null) {
                    copiaEstaciones.remove(i);
                    i--;
                    continue;
                }
            }
            if(tipoGasolina==2){
                if (copiaEstaciones.get(i).getPrecio().getPremium()==null) {
                    copiaEstaciones.remove(i);
                    i--;
                    continue;
                }
            }
            if(tipoGasolina==3){
                if (copiaEstaciones.get(i).getPrecio().getDiesel()==null) {
                    copiaEstaciones.remove(i);
                    i--;
                    continue;
                }
            }



        }

        if(tipoGasolina==1){
            copiaEstaciones.sort((f1, f2) -> Float.valueOf(f1.getPrecio().getRegular()).compareTo(Float.valueOf(f2.getPrecio().getRegular()) ));
        }
        if(tipoGasolina==2){

            copiaEstaciones.sort((f1, f2) -> Float.valueOf(f1.getPrecio().getPremium()).compareTo(Float.valueOf(f2.getPrecio().getPremium()) ));

        }

        if(tipoGasolina==3){

            copiaEstaciones.sort((f1, f2) -> Float.valueOf(f1.getPrecio().getDiesel()).compareTo(Float.valueOf(f2.getPrecio().getDiesel()) ));

        }

        if(copiaEstaciones.size()==0)
            return;

        if(copiaEstaciones.size()==1)
            copiaEstaciones.get(0).setPosicion(1);

        else if (copiaEstaciones.size()==2){
            copiaEstaciones.get(0).setPosicion(1);
            copiaEstaciones.get(1).setPosicion(2);
        }else
        {
            copiaEstaciones.get(0).setPosicion(1);
            copiaEstaciones.get(1).setPosicion(2);
            copiaEstaciones.get(2).setPosicion(3);
        }



        mClusterManager.addItems(copiaEstaciones);
        mClusterManager.setRenderer(new MyClusterRenderer(getContext(), mMap,mClusterManager));
        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(LayoutInflater.from(getContext()));
        mClusterManager.getMarkerCollection()
                .setOnInfoWindowAdapter(customInfoWindow);

        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

        mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {

            @Override
            public void onInfoWindowClose(Marker marker) {
                doubleTap=0;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                doubleTap++;
                if(doubleTap==2){
                    if(MainActivity.user!=null){
                        DatabaseReference updateData = FirebaseDatabase.getInstance()
                                .getReference("usuarios")
                                .child(MainActivity.user.getUid());
                        Map<String, Object> postValues = new HashMap<String,Object>();
                        Gson gson = new Gson();
                        Estaciones estacion = gson.fromJson(marker.getSnippet(),Estaciones.class);

                        if(MainActivity.usuarioLogin.getFavoritos()==null){
                            HashMap<String,String> favos = new HashMap<>(0);
                            favos.put(estacion.getPlace_id(),estacion.getPlace_id());
                            MainActivity.usuarioLogin.setFavoritos(favos);
                            postValues.put("favoritos",MainActivity.usuarioLogin.getFavoritos());
                            updateData.updateChildren(postValues);
                        }else if(MainActivity.usuarioLogin.getFavoritos()!=null && !MainActivity.usuarioLogin.getFavoritos().containsKey(estacion.getPlace_id())){
                            MainActivity.usuarioLogin.getFavoritos().put(estacion.getPlace_id(),estacion.getPlace_id());

                            updateData.child("favoritos").setValue(MainActivity.usuarioLogin.getFavoritos());
                        }else{
                            MainActivity.usuarioLogin.getFavoritos().remove(estacion.getPlace_id());
                            updateData.child("favoritos").setValue(MainActivity.usuarioLogin.getFavoritos());
                        }


                        marker.hideInfoWindow();
                        marker.showInfoWindow();
                    }else{
                        Toast.makeText(getContext(),"Inicie sesión para empezar a agregar favoritos ♥",Toast.LENGTH_LONG).show();
                    }

                }
            }

        });
        mClusterManager.cluster();
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }





    private class TareaDescargaXmlPrecios extends AsyncTask<String, Void, List<Precios>> {

        @Override
        protected List<Precios> doInBackground(String... urls) {
            try {
                return parsearXmlDeUrlPre(urls[0]);
            } catch (IOException e) {
                return null;
            } catch (XmlPullParserException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Precios> result) {
            myPrices=result;
        }
    }

    private List<Estaciones> parsearXmlDeUrl(String urlString)
            throws XmlPullParserException, IOException {
        InputStream stream = null;
        parserXML parserXml = new parserXML(miUbicacion);


        try {
            stream = descargarContenido(urlString);
            entries = parserXml.parsear(stream);

        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return entries;
    }

    private List<Precios> parsearXmlDeUrlPre(String urlString)
            throws XmlPullParserException, IOException {
        InputStream stream = null;
        parserXML parserXml = new parserXML(miUbicacion);
        List<Precios> entries = null;

        try {
            stream = descargarContenido(urlString);
            System.out.println("hola");
            entries = parserXml.parsearPrecios(stream);

        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return entries;
    }

    private InputStream descargarContenido(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }


    public class MyClusterRenderer extends DefaultClusterRenderer<Estaciones> {


        public MyClusterRenderer(Context context, GoogleMap map,
                                 ClusterManager<Estaciones> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(Estaciones item,
                                                   MarkerOptions markerOptions) {
            BitmapDescriptor markerDescriptor;
            if(item.getPosicion()==1)
                markerDescriptor = bitmapDescriptorFromVector(getContext(),R.drawable.gas_stationdorada);
            else if(item.getPosicion()==2)
                markerDescriptor = bitmapDescriptorFromVector(getContext(),R.drawable.gas_stationplata);
            else if(item.getPosicion()==3)
                markerDescriptor = bitmapDescriptorFromVector(getContext(),R.drawable.gas_stationbronce);
            else
                markerDescriptor = bitmapDescriptorFromVector(getContext(),R.drawable.gas_stationgris);

            markerOptions.icon(markerDescriptor);
        }

        @Override
        protected void onClusterItemRendered(Estaciones clusterItem, Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);
        }


    }
}