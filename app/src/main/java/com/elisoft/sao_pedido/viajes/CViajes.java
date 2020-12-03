package com.elisoft.sao_pedido.viajes;

/**
 * Created by elisoft on 06-03-17.
 */



public class CViajes {
    private int id;
    private int id_taxi;
    private int estado_pedido;
    private String fecha_pedido;
    private String nombre;
    private String apellido;
    private String celular;
    private String marca;
    private String placa;
    private String indicacion;
    private String descripcion;
    private String monto_total;
    private double latitud;
    private double longitud;
    private int clase_vehiculo;
    private int calificacion_vehiculo;
    private int calificacion_conductor;

    private String direccion_lugar;
    private String direccion_logo_lugar;
    private String monto_pedido;

    public CViajes()
    {

    }
    public CViajes( int id,
                            int id_taxi,
                            int estado_pedido,
                            String fecha_pedido,
                            String nombre,
                            String apellido,
                            String celular,
                            String marca,
                            String placa,
                            String indicacion,
                            String descripcion,
                            double latitud,
                            double longitud,
                            String monto_total,
                            int clase_vehiculo,
                            int calificacion_conductor,
                            int calificacion_vehiculo,
                            String direccion_lugar,
                            String direccion_logo_lugar,
                            String monto_pedido
    )
    {

        this.id= id;
        this.id_taxi=id_taxi;
        this.estado_pedido=estado_pedido;
        this.fecha_pedido=fecha_pedido;
        this.nombre=nombre;
        this.apellido=apellido;
        this.celular=celular;
        this.marca=marca;
        this.placa=placa;
        this.indicacion=indicacion;
        this.setDescripcion(descripcion);
        this.latitud=latitud;
        this.longitud=longitud;
        this.monto_total=monto_total;
        this.setClase_vehiculo(clase_vehiculo);
        this.setCalificacion_conductor(calificacion_conductor);
        this.setCalificacion_vehiculo(calificacion_vehiculo);
        this.setDireccion_lugar(direccion_lugar);
        this.setDireccion_logo_lugar(direccion_logo_lugar);
        this.setMonto_pedido(monto_pedido);
    }




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_taxi() {
        return id_taxi;
    }

    public void setId_taxi(int id_taxi) {
        this.id_taxi = id_taxi;
    }

    public int getEstado_pedido() {
        return estado_pedido;
    }

    public void setEstado_pedido(int estado_pedido) {
        this.estado_pedido = estado_pedido;
    }

    public String getFecha_pedido() {
        return fecha_pedido;
    }

    public void setFecha_pedido(String fecha_pedido) {
        this.fecha_pedido = fecha_pedido;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMonto_total() {
        return monto_total;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setMonto_total(String monto_total) {
        this.monto_total = monto_total;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getIndicacion() {
        return indicacion;
    }

    public void setIndicacion(String indicacion) {
        this.indicacion = indicacion;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }



    @Override
    public String toString() {
        return "Cpedido_usuario{" +
                "id='" + id + '\'' +
                ",id_taxi='" + id_taxi + '\'' +
                ", estado_pedido='" + estado_pedido + '\'' +
                ", fecha_pedido='" + fecha_pedido + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", celular='" + celular + '\'' +
                ", marca='" + marca + '\'' +
                ", placa='" + placa + '\'' +
                ", indicacion='" + indicacion + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", latitud='" + latitud + '\'' +
                ", longitud='" + longitud + '\'' +
                ", monto_total='" + monto_total + '\'' +
                '}';
    }

    public int getClase_vehiculo() {
        return clase_vehiculo;
    }

    public void setClase_vehiculo(int clase_vehiculo) {
        this.clase_vehiculo = clase_vehiculo;
    }

    public int getCalificacion_vehiculo() {
        return calificacion_vehiculo;
    }

    public void setCalificacion_vehiculo(int calificacion_vehiculo) {
        this.calificacion_vehiculo = calificacion_vehiculo;
    }

    public int getCalificacion_conductor() {
        return calificacion_conductor;
    }

    public void setCalificacion_conductor(int calificacion_conductor) {
        this.calificacion_conductor = calificacion_conductor;
    }

    public String getDireccion_lugar() {
        return direccion_lugar;
    }

    public void setDireccion_lugar(String direccion_lugar) {
        this.direccion_lugar = direccion_lugar;
    }

    public String getDireccion_logo_lugar() {
        return direccion_logo_lugar;
    }

    public void setDireccion_logo_lugar(String direccion_logo_lugar) {
        this.direccion_logo_lugar = direccion_logo_lugar;
    }

    public String getMonto_pedido() {
        return monto_pedido;
    }

    public void setMonto_pedido(String monto_pedido) {
        this.monto_pedido = monto_pedido;
    }
}
