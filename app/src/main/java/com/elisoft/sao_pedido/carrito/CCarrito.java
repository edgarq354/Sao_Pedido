package com.elisoft.sao_pedido.carrito;

public class CCarrito {

    private int id_producto;
    private int id_pedido;
    private int cantidad;
    private String nombre;
    private String descripcion;
    private String url;
    private double monto_unidad;
    private double monto_total;

    public CCarrito()
    {
        this.setId_producto(0);
        this.setId_pedido(0);
        this.setCantidad(0);
        setNombre("");
        setDescripcion("");
        setUrl("");
        setMonto_unidad(0);
        setMonto_total(0);
    }

    public CCarrito(int id_producto,
                    int id_pedido,
                    int cantidad,
                    String nombre,
                    String descripcion,
                    String url,
                    double monto_unidad,
                    double monto_total)
    {
        setId_producto(id_producto);
        setId_producto(id_producto);
        setCantidad(cantidad);
        setNombre(nombre);
        setDescripcion(descripcion);
        setUrl(url);
        setMonto_unidad(monto_unidad);
        setMonto_total(monto_total);

    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public int getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(int id_pedido) {
        this.id_pedido = id_pedido;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getMonto_unidad() {
        return monto_unidad;
    }

    public void setMonto_unidad(double monto_unidad) {
        this.monto_unidad = monto_unidad;
    }

    public double getMonto_total() {
        return monto_total;
    }

    public void setMonto_total(double monto_total) {
        this.monto_total = monto_total;
    }
}
