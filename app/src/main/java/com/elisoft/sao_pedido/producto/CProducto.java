package com.elisoft.sao_pedido.producto;

public class CProducto {

    private int id;
    private int estado_pedido;
    private String nombre;
    private String discripcion;
    private String precio;
    private String imagen1;
    private String imagen2;
    private String imagen3;
    private String imagen4;
    private String imagen5;
    private int id_lugar;
    private String id_tipo_producto;


    public CProducto(){
        this.setId(0);
        this.setNombre("");
        this.setDiscripcion("");
        this.setPrecio("0");
        this.setImagen1("");
        this.setImagen2("");
        this.setImagen3("");
        this.setImagen4("");
        this.setImagen5("");
        this.setId_lugar(0);
        this.setEstado_pedido(0);
        this.setId_tipo_producto("");
    }

    public CProducto(
            int id,
            String nombre,
            String discripcion,
            String precio,
            String imagen1,
            String imagen2,
            String imagen3,
            String imagen4,
            String imagen5,
            int id_lugar,
            int estado_pedido,
            String id_tipo_producto
    ){
        this.setId(id);
        this.setNombre(nombre);
        this.setDiscripcion(discripcion);
        this.setPrecio(precio);
        this.setImagen1(imagen1);
        this.setImagen2(imagen2);
        this.setImagen3(imagen3);
        this.setImagen4(imagen4);
        this.setImagen5(imagen5);
        this.setId_lugar(id_lugar);
        this.setEstado_pedido(estado_pedido);
        this.setId_tipo_producto(id_tipo_producto);
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

    public String getDiscripcion() {
        return discripcion;
    }

    public void setDiscripcion(String discripcion) {
        this.discripcion = discripcion;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getImagen1() {
        return imagen1;
    }

    public void setImagen1(String imagen1) {
        this.imagen1 = imagen1;
    }

    public String getImagen2() {
        return imagen2;
    }

    public void setImagen2(String imagen2) {
        this.imagen2 = imagen2;
    }

    public String getImagen3() {
        return imagen3;
    }

    public void setImagen3(String imagen3) {
        this.imagen3 = imagen3;
    }

    public String getImagen4() {
        return imagen4;
    }

    public void setImagen4(String imagen4) {
        this.imagen4 = imagen4;
    }

    public String getImagen5() {
        return imagen5;
    }

    public void setImagen5(String imagen5) {
        this.imagen5 = imagen5;
    }

    public int getId_lugar() {
        return id_lugar;
    }

    public void setId_lugar(int id_lugar) {
        this.id_lugar = id_lugar;
    }

    public int getEstado_pedido() {
        return estado_pedido;
    }

    public void setEstado_pedido(int estado_pedido) {
        this.estado_pedido = estado_pedido;
    }

    public String getId_tipo_producto() {
        return id_tipo_producto;
    }

    public void setId_tipo_producto(String id_tipo_producto) {
        this.id_tipo_producto = id_tipo_producto;
    }
}
