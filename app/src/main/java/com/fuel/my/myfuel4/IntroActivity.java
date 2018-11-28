package com.fuel.my.myfuel4;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import org.jetbrains.annotations.Nullable;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("Bienvenido a GasolinApp");
        sliderPage.setDescription("Una App que te ayuda a encontrar la gasolina más barata!");
        sliderPage.setImageDrawable(R.drawable.gasolinappintrologofinal);
        sliderPage.setBgColor(Color.parseColor("#6E3AC2"));
        addSlide(AppIntroFragment.newInstance(sliderPage));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("Posiciones");
        sliderPage2.setDescription("1er lugar: Dorado, 2do Lugar: Plateado, 3er Lugar: Bronze.");
        sliderPage2.setImageDrawable(R.drawable.medal);
        sliderPage2.setBgColor(Color.parseColor("#4D3DCC"));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle("¡ Agita !");
        sliderPage3.setDescription("Al agitar tu telefono te mostraremos el camino a los mejores precios");
        sliderPage3.setImageDrawable(R.drawable.cellphone_sound);
        sliderPage3.setBgColor(Color.parseColor("#3F51B5"));
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setTitle("Aumenta el rango");
        sliderPage4.setDescription("Desliza el dedo sobre la barra para encontrar más gasolineras");
        sliderPage4.setImageDrawable(R.drawable.map_marker_distance);
        sliderPage4.setBgColor(Color.parseColor("#3D78CC"));
        addSlide(AppIntroFragment.newInstance(sliderPage4));

        SliderPage sliderPage5 = new SliderPage();
        sliderPage5.setTitle("Selecciona el tipo de gasolina que usas");
        sliderPage5.setDescription("Ingresa a Ajustes para establecer un tipo de gasolina preferida");
        sliderPage5.setImageDrawable(R.drawable.gasstationintro);
        sliderPage5.setBgColor(Color.parseColor("#3A96C2"));
        addSlide(AppIntroFragment.newInstance(sliderPage5));

        SliderPage sliderPage6 = new SliderPage();
        sliderPage6.setTitle("Favoritos");
        sliderPage6.setDescription("Selecciona una gasolinera, presiona 2 veces y listo.");
        sliderPage6.setImageDrawable(R.drawable.heartintro);
        sliderPage6.setBgColor(Color.parseColor("#3FA8C2"));
        addSlide(AppIntroFragment.newInstance(sliderPage6));


    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }
}
