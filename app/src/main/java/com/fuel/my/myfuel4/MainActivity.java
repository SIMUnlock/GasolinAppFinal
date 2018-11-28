package com.fuel.my.myfuel4;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    public static FirebaseUser user;
    private View view;
    public static Usuarios usuarioLogin;
    private MenuItem itemSesion;
    private static NavigationView navigationView;
    private static TextView tipoGasStr,correoHeader;
    private static LinearLayout test2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);



        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {



            startActivity(new Intent(MainActivity.this, IntroActivity.class));


        }
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);


        navigationView.setNavigationItemSelectedListener(this);

        View test= navigationView.getHeaderView(0);
        test2=test.findViewById(R.id.headerSlide);
        tipoGasStr= test2.findViewById(R.id.textviewGasolinaHeader);
        correoHeader= test2.findViewById(R.id.textviewCorreoHeader);


        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            appBarLayout.setOutlineProvider(null);
            toolbar.setTitleTextColor(Color.rgb(46,143,111));
            toolbar.setTitle("GasolinApp");
        }




        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase .getInstance();


        user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user == null) {

                } else {

                    DatabaseReference root = database.getReference ();
                    DatabaseReference usuarios = root.child ("usuarios");

                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange (@NonNull DataSnapshot dataSnapshot) {

                                if(user!=null) {
                                    usuarioLogin = dataSnapshot.child(user.getUid()).getValue(Usuarios.class);

                                    Menu test = navigationView.getMenu();
                                    MenuItem itemtest = test.findItem(R.id.nav_inout);


                                    if (Integer.parseInt(usuarioLogin.getTipogasolina()) == 1) {
                                        test2.setBackgroundColor(Color.parseColor("#007800"));
                                        tipoGasStr.setText("Gasolina: Regular");
                                    }
                                    if (Integer.parseInt(usuarioLogin.getTipogasolina()) == 2) {

                                        test2.setBackgroundColor(Color.parseColor("#7e0000"));
                                        tipoGasStr.setText("Gasolina: Premium");
                                    }
                                    if (Integer.parseInt(usuarioLogin.getTipogasolina()) == 3) {

                                        test2.setBackgroundColor(Color.parseColor("#000000"));
                                        tipoGasStr.setText("Gasolina: Diesel");
                                    }

                                    correoHeader.setText(usuarioLogin.getCorreo());
                                    MapsActivity.tipoGasolina = Integer.parseInt(usuarioLogin.getTipogasolina());

                                    itemtest.setTitle("Cerrar Sesión");
                                }

                        }

                        @Override
                        public void onCancelled (@NonNull DatabaseError databaseError) {
                            Log.w ("PKAT", databaseError.toException ());
                        }
                    };

                    usuarios.addListenerForSingleValueEvent (valueEventListener);


                }
            }
        };


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            getSupportFragmentManager()
                    .beginTransaction().replace(R.id.container, new MapsActivity())
                    .commit();
        }


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();


        auth.addAuthStateListener(authListener);
    }



    @Override
    public void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            checkUserLocationPermission();
        }


            if(user!=null && usuarioLogin!=null) {


                if (Integer.parseInt(usuarioLogin.getTipogasolina()) == 1) {
                    test2.setBackgroundColor(Color.parseColor("#007800"));
                    tipoGasStr.setText("Gasolina: Regular");
                }
                if (Integer.parseInt(usuarioLogin.getTipogasolina()) == 2) {

                    test2.setBackgroundColor(Color.parseColor("#7e0000"));
                    tipoGasStr.setText("Gasolina: Premium");
                }
                if (Integer.parseInt(usuarioLogin.getTipogasolina()) == 3) {

                    test2.setBackgroundColor(Color.parseColor("#000000"));
                    tipoGasStr.setText("Gasolina: Diesel");
                }
                DatabaseReference updateData = FirebaseDatabase.getInstance()
                        .getReference("usuarios")
                        .child(user.getUid());
                updateData.child("tipogasolina").setValue(usuarioLogin.getTipogasolina());
            }else if (SettingsActivity.variablereturn==1){

                if (MapsActivity.tipoGasolina == 1) {
                    test2.setBackgroundColor(Color.parseColor("#007800"));
                    tipoGasStr.setText("Gasolina: Regular");
                }
                if (MapsActivity.tipoGasolina== 2) {

                    test2.setBackgroundColor(Color.parseColor("#7e0000"));
                    tipoGasStr.setText("Gasolina: Premium");
                }
                if (MapsActivity.tipoGasolina == 3) {

                    test2.setBackgroundColor(Color.parseColor("#000000"));
                    tipoGasStr.setText("Gasolina: Diesel");
                }
            }

        auth.addAuthStateListener(authListener);

    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_gasolineras) {

        }  else if (id == R.id.nav_favs) {

            Intent intent = new Intent(getApplicationContext(),FavsActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(intent);
        }

        else if (id == R.id.nav_help) {
            Intent intent = new Intent(getApplicationContext(),IntroActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_about) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.popup_about, null);
            popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            final PopupWindow popupWindow = new PopupWindow(popupView, popupView.getMeasuredWidth() + 200, popupView.getMeasuredHeight(), true);
            popupWindow.showAtLocation(popupView, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setIgnoreCheekPress();
        }
        else if (id == R.id.nav_inout) {

            if(user==null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.popup_login, null);
                popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                final PopupWindow popupWindow = new PopupWindow(popupView, popupView.getMeasuredWidth() + 200, popupView.getMeasuredHeight(), true);
                popupWindow.showAtLocation(popupView, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setIgnoreCheekPress();
                Button button= (Button) popupView.findViewById(R.id.btn_login);
                Button buttonRegister= (Button) popupView.findViewById(R.id.btn_link_signup);

                EditText correo = (EditText) popupView.findViewById(R.id.login_input_email);
                EditText contra = (EditText) popupView.findViewById(R.id.login_input_password);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = correo.getText().toString().trim();
                        final String password = contra.getText().toString().trim();

                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(v.getContext(),"Ingrese un correo electrónico",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(password)) {
                            Toast.makeText(v.getContext(),"Ingrese una contraseña",Toast.LENGTH_SHORT).show();
                            return;
                        }



                        auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (!task.isSuccessful()) {
                                            if (password.length() < 6) {
                                                Toast.makeText(v.getContext(),"La contraseña debe ser mayor a 5 caracteres",Toast.LENGTH_SHORT).show();

                                        } else {
                                                Toast.makeText(v.getContext(),"Ups! Algo malo ocurrio, verifique su correo o contraseña",Toast.LENGTH_SHORT).show();}
                                        } else {
                                            Toast.makeText(v.getContext(),"Se ha iniciado sesión",Toast.LENGTH_SHORT).show();

                                            user = auth.getCurrentUser();

                                            DatabaseReference root = database.getReference ();
                                            DatabaseReference usuarios = root.child ("usuarios");

                                            ValueEventListener valueEventListener = new ValueEventListener() {
                                                @Override
                                                public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                                                    usuarioLogin = dataSnapshot.child(user.getUid()).getValue(Usuarios.class);

                                                }

                                                @Override
                                                public void onCancelled (@NonNull DatabaseError databaseError) {
                                                    Log.w ("PKAT", databaseError.toException ());
                                                }
                                            };

                                            usuarios.addListenerForSingleValueEvent (valueEventListener);


                                            item.setTitle("Cerrar Sesión");
                                            popupWindow.dismiss();
                                        }
                                    }
                                });
                    }
                });

                buttonRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        popupWindow.dismiss();

                        View popupView = layoutInflater.inflate(R.layout.popup_signup, null);
                        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                        final PopupWindow popupWindow = new PopupWindow(popupView, popupView.getMeasuredWidth() , popupView.getMeasuredHeight(), true);
                        popupWindow.showAtLocation(popupView, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                        popupWindow.setOutsideTouchable(true);
                        popupWindow.setIgnoreCheekPress();
                        Button button= (Button) popupView.findViewById(R.id.btn_signup);

                        EditText correo = (EditText) popupView.findViewById(R.id.signup_input_email);
                        EditText contra = (EditText) popupView.findViewById(R.id.signup_input_password);

                        button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                String email = correo.getText().toString().trim();
                                final String password = contra.getText().toString().trim();

                                if (TextUtils.isEmpty(email)) {
                                    Toast.makeText(v.getContext(),"Ingrese un correo electrónico",Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (TextUtils.isEmpty(password)) {
                                    Toast.makeText(v.getContext(),"Ingrese una contraseña",Toast.LENGTH_SHORT).show();
                                    return;
                                }



                                if (password.length() < 6) {
                                    Toast.makeText(getApplicationContext(), "Enter minimum 6 characters.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(v.getContext(),"Ups! Algo salio mal",Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(v.getContext(),"Se ha registrado e iniciado sesión",Toast.LENGTH_SHORT).show();
                                                    DatabaseReference root = database.getReference (); /* root */
                                                    DatabaseReference usuarios = root.child ("usuarios"); /* /usuarios */

                                                    Usuarios usuariosObj = new Usuarios();
                                                    usuariosObj.setCorreo(correo.getText().toString());
                                                    usuariosObj.setContrasenia(contra.getText().toString());
                                                    usuariosObj.setTipogasolina("1");
                                                    HashMap<String, Object> hashMap = new HashMap<> ();
                                                    hashMap.put (auth.getUid(), usuariosObj);
                                                    usuarios.updateChildren (hashMap) //updateChildren envía los datos asíncronamente por lo que hay que definir los listener correspodientes
                                                            .addOnSuccessListener (v -> {
                                                                Log.i ("Done", "Información almacenada");
                                                            })
                                                            .addOnFailureListener (e -> {
                                                                Log.e ("Done"
                                                                        , "Error al guardar información", e);
                                                            });
                                                    item.setTitle("Cerrar Sesión");
                                                    popupWindow.dismiss();
                                                }
                                            }
                                        });
                            }
                        });



                    }
                });
            }else{
                Toast.makeText(this,"Cerraste sesión",Toast.LENGTH_SHORT).show();

                auth.signOut();
                user=null;

                usuarioLogin=null;
                tipoGasStr.setText("Gasolina: Regular");
                correoHeader.setText("correo@gasolinApp.com");
                test2.setBackgroundColor(Color.parseColor("#007800"));
                MapsActivity.tipoGasolina=1;
                item.setTitle("Iniciar Sesión");
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean checkUserLocationPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1);
                recreate();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1);
                recreate();
            }
            return  false;
        }else{
            return true;
        }
    }


}

