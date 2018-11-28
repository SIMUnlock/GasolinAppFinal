package com.fuel.my.myfuel4;


import java.util.HashMap;

public class  Usuarios {
    private String correo;
    private String tipogasolina;
    private HashMap<String,String> favoritos;
    private String contrasenia;

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public HashMap<String, String> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(HashMap<String, String> favoritos) {
        this.favoritos = favoritos;
    }

    public String getTipogasolina() {
        return tipogasolina;
    }

    public void setTipogasolina(String tipogasolina) {
        this.tipogasolina = tipogasolina;
    }
}
