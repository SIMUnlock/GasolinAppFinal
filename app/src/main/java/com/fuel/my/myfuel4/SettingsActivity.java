package com.fuel.my.myfuel4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioGroup;

public class SettingsActivity extends AppCompatActivity {
    public static int variablereturn=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        if(MainActivity.user!=null){
            if(Integer.parseInt(MainActivity.usuarioLogin.getTipogasolina())==1)
                radioGroup.check(findViewById(R.id.radioButton).getId());
            if(Integer.parseInt(MainActivity.usuarioLogin.getTipogasolina())==2)
                radioGroup.check(findViewById(R.id.radioButton2).getId());
            if(Integer.parseInt(MainActivity.usuarioLogin.getTipogasolina())==3)
                radioGroup.check(findViewById(R.id.radioButton3).getId());
        }else {
            if(MapsActivity.tipoGasolina==1)
                radioGroup.check(findViewById(R.id.radioButton).getId());
            if(MapsActivity.tipoGasolina==2)
                radioGroup.check(findViewById(R.id.radioButton2).getId());
            if(MapsActivity.tipoGasolina==3)
                radioGroup.check(findViewById(R.id.radioButton3).getId());
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId== findViewById(R.id.radioButton).getId()){
                    MapsActivity.tipoGasolina=1;
                    if (MainActivity.user!=null)
                    MainActivity.usuarioLogin.setTipogasolina("1");
                    else {
                        variablereturn=1;
                    }
                }

                if (checkedId== findViewById(R.id.radioButton2).getId())
                {
                    MapsActivity.tipoGasolina=2;
                    if (MainActivity.user!=null)
                    MainActivity.usuarioLogin.setTipogasolina("2");
                    else {
                        variablereturn=1;
                    }
                }

                if (checkedId== findViewById(R.id.radioButton3).getId())

                {
                    MapsActivity.tipoGasolina=3;
                    if (MainActivity.user!=null)
                    MainActivity.usuarioLogin.setTipogasolina("3");
                    else {
                        variablereturn=1;
                    }
                }
            }
        });
    }
}
