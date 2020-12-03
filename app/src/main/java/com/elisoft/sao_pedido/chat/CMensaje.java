package com.elisoft.sao_pedido.chat;



public class CMensaje {
     boolean left;
     String mensaje;
     String fecha;
     String hora;
     String titulo;
     int estado=0;
     int id_usuario=0;
     int id_conductor=0;
     int yo=0;
     int id=0;
     String tipo="";

    public CMensaje(boolean left, String message, String titulo, String fecha, String hora, int estado , int id_usuario, int id_conductor, int yo,String tipo,int id) {
        super();
        this.setLeft(left);
        this.setMensaje(message);
        this.setTitulo(titulo);
        this.setFecha(fecha);
        this.setEstado(estado);
        this.setId_usuario(id_usuario);
        this.setId_conductor(id_conductor);
        this.setHora(hora);
        this.setYo(yo);
        this.setTipo(tipo);
        this.setId(id);
    }


    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getId_conductor() {
        return id_conductor;
    }

    public void setId_conductor(int id_conductor) {
        this.id_conductor = id_conductor;
    }

    public int getYo() {
        return yo;
    }

    public void setYo(int yo) {
        this.yo = yo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}