package com.fuel.my.myfuel4;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FavsActivity extends AppCompatActivity implements OnFavTouchedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(MainActivity.user!=null){
            if(MainActivity.usuarioLogin.getFavoritos()==null){
                setContentView(R.layout.activity_favsnofavs);
            }else {
                setContentView(R.layout.activity_favs);

                getSupportFragmentManager ()
                        .beginTransaction ()
                        .replace (R.id.rootContainer, new FavListFragment ())
                        .setTransition (FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit ();
            }
        }else {
            setContentView(R.layout.activity_favsnologin);

        }

    }
    @Override
    public void onFavTouched (int index, ImageView corazon) {
        if(MainActivity.usuarioLogin.getFavoritos().containsKey(String.valueOf(index))){
            DatabaseReference updateData = FirebaseDatabase.getInstance()
                    .getReference("usuarios")
                    .child(MainActivity.user.getUid());
            MainActivity.usuarioLogin.getFavoritos().remove(String.valueOf(index));
            updateData.child("favoritos").setValue(MainActivity.usuarioLogin.getFavoritos());
            corazon.setImageResource(R.drawable.heartunselected);
        }else{
            DatabaseReference updateData = FirebaseDatabase.getInstance()
                    .getReference("usuarios")
                    .child(MainActivity.user.getUid());
            MainActivity.usuarioLogin.getFavoritos().put(String.valueOf(index),String.valueOf(index));
            corazon.setImageResource(R.drawable.heartselected);
            updateData.child("favoritos").setValue(MainActivity.usuarioLogin.getFavoritos());
        }

    }
}
