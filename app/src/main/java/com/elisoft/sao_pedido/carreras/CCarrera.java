package com.elisoft.sao_pedido.carreras;

/**
 * Created by ELIO on 15/11/2016.
 */

public class CCarrera {

    private int id;
    private double latitud_inicio;
    private double longitud_inicio;
    private double latitud_fin;
    private double longitud_fin;
    private double monto;
    private String distancia;
    private String fecha_inicio;
    private String fecha_fin ;
    private String tiempo;
    private int id_pedido;
    private int id_conductor;
    private String ruta;
    private String numero;
    private String direccion_inicio;
    private String direccion_fin;
    private String placa;

    public CCarrera()
    {

    }
    public CCarrera(int id, double latitud_inicio, double longitud_inicio, double latitud_fin, double longitud_fin, String distancia, String fecha_inicio, String fecha_fin, String tiempo, int id_pedido, int id_conductor, double monto, String ruta, String numero,String direccion_inicio,String direccion_fin)    {
        this.id=id;
        this.latitud_inicio=latitud_inicio;
        this.longitud_inicio=longitud_inicio;
        this.latitud_fin=latitud_fin;
        this.longitud_fin=longitud_fin;
        this.monto=monto;
        this.distancia=distancia;
        this.fecha_inicio=fecha_inicio;
        this.fecha_fin=fecha_fin;
        this.tiempo=tiempo;
        this.id_pedido=id_pedido;
        this.id_conductor=id_conductor;
        this.setDireccion_inicio(direccion_inicio);
        this.setDireccion_fin(direccion_fin);

        this.setNumero(numero);
        this.setRuta(ruta);
        this.setPlaca("");
    }




    public int getId() {
        return id;
    }

    public double getLatitud_inicio() {
        return latitud_inicio;
    }

    public double getLongitud_inicio() {
        return longitud_inicio;
    }

    public double getLatitud_fin() {
        return latitud_fin;
    }

    public double getLongitud_fin() {
        return longitud_fin;
    }

    public double getMonto() {
        return monto;
    }


    public String getDistancia() {
        return distancia;
    }

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public String getFecha_fin() {
        return fecha_fin;
    }

    public String getTiempo() {
        return tiempo;
    }

    public int getId_pedido() {
        return id_pedido;
    }

    public int getId_conductor() {
        return id_conductor;
    }



    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDireccion_inicio() {
        return direccion_inicio;
    }

    public void setDireccion_inicio(String direccion_inicio) {
        this.direccion_inicio = direccion_inicio;
    }

    public String getDireccion_fin() {
        return direccion_fin;
    }

    public void setDireccion_fin(String direccion_fin) {
        this.direccion_fin = direccion_fin;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
}
