package com.elisoft.sao_pedido.categoria;

/**
 * Created by ELIO on 23/12/2017.
 */

public class CCategoria {
    private int id;
    private int estado_pedido;
    private String nombre;
    private String direccion_imagen;

    public CCategoria(){
        setId(0);
        setNombre("");
        setDireccion_imagen("");
        setEstado_pedido(0);
    }

    public CCategoria(int id, String nombre, String direccion_imagen, int estado_pedido){
        this.setId(id);
        this.setNombre(nombre);
        this.setDireccion_imagen(direccion_imagen);
        this.setEstado_pedido(estado_pedido);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion_imagen() {
        return direccion_imagen;
    }

    public void setDireccion_imagen(String direccion_imagen) {
        this.direccion_imagen = direccion_imagen;
    }

    public int getEstado_pedido() {
        return estado_pedido;
    }

    public void setEstado_pedido(int estado_pedido) {
        this.estado_pedido = estado_pedido;
    }
}
