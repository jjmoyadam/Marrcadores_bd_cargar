package jjmoya.marrcadores_bd_cargar;

/**
 * Created by curso on 19/09/2017.
 */

public class Coordenadas {

    private Double latitud;
    private Double longitud;
    private String nombre;

    //metodos get


    public Double getLatitud() {
        return latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public String getNombre() {
        return nombre;
    }

    //metodos set


    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
