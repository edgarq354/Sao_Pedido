package com.elisoft.sao_pedido.empresa;

/**
 * Created by ELIO on 23/12/2017.
 */

public class CEmpresa {
    private int id;
    private int estado_pedido;
    private String nombre;
    private String facebook;
    private String direccion_logo;
    private String direccion_banner;
    private String direccion;
    private String telefono;
    private String whatsapp;
    private String latitud;
    private String longitud;
    private String informacion;
    private int estado;
    private int id_categoria;
    private int distancia;
    private int tiempo_preparacion;
    private String abierto_cerrado;
    private String monto_minimo;
    private String solicitud_whatsapp;
    private String solicitud_aplicacion;

    public CEmpresa(){
        setId(0);
        setNombre("");
        setDireccion("");
        setTelefono("");
        setWhatsapp("");
        setLatitud("0");
        setLongitud("0");
        setEstado(0);
        setId_categoria(0);
        this.setDireccion_logo("");
        this.setDistancia(0);
        this.setFacebook("");
        this.setInformacion("");
        this.setEstado_pedido(0);
        this.setDireccion_banner("");
        this.setTiempo_preparacion(0);
        this.setAbierto_cerrado("");
        this.setMonto_minimo("0");
    }

    public CEmpresa(int id,
                    String nombre,
                    String direccion,
                    String telefono,
                    String whatsapp,
                    String latitud,
                    String longitud,
                    int estado,
                    int id_categoria,
                    String direccion_logo,
                    String direccion_banner,
                    int distancia,
                    String facebook,
                    String informacion,
                    int estado_pedido,
                    int tiempo_preparacion,
                    String abierto_cerrado,
                    String monto_minimo,
                    String solicitud_whatsapp,
                    String solicitud_aplicacion
                    ){
        this.setId(id);
        this.setNombre(nombre);
        this.setDireccion_logo(direccion_logo);
        this.setDireccion(direccion);
        this.setTelefono(telefono);
        this.setWhatsapp(whatsapp);
        this.setLatitud(latitud);
        this.setLongitud(longitud);
        this.setEstado(estado);
        this.setId_categoria(id_categoria);
        this.setDistancia(distancia);
        this.setFacebook(facebook);
        this.setInformacion(informacion);
        this.setEstado_pedido(estado_pedido);
        this.setDireccion_banner(direccion_banner);
        this.setTiempo_preparacion(tiempo_preparacion);
        this.setAbierto_cerrado(abierto_cerrado);
        this.setMonto_minimo(monto_minimo);
        this.setSolicitud_whatsapp(solicitud_whatsapp);
        this.setSolicitud_aplicacion(solicitud_aplicacion);
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }

    public String getDireccion_logo() {
        return direccion_logo;
    }

    public void setDireccion_logo(String direccion_logo) {
        this.direccion_logo = direccion_logo;
    }

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInformacion() {
        return informacion;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }

    public int getEstado_pedido() {
        return estado_pedido;
    }

    public void setEstado_pedido(int estado_pedido) {
        this.estado_pedido = estado_pedido;
    }

    public String getDireccion_banner() {
        return direccion_banner;
    }

    public void setDireccion_banner(String direccion_banner) {
        this.direccion_banner = direccion_banner;
    }

    public int getTiempo_preparacion() {
        return tiempo_preparacion;
    }

    public void setTiempo_preparacion(int tiempo_preparacion) {
        this.tiempo_preparacion = tiempo_preparacion;
    }

    public String getAbierto_cerrado() {
        return abierto_cerrado;
    }

    public void setAbierto_cerrado(String abierto_cerrado) {
        this.abierto_cerrado = abierto_cerrado;
    }

    public String getMonto_minimo() {
        return monto_minimo;
    }

    public void setMonto_minimo(String monto_minimo) {
        this.monto_minimo = monto_minimo;
    }

    public String getSolicitud_whatsapp() {
        return solicitud_whatsapp;
    }

    public void setSolicitud_whatsapp(String solicitud_whatsapp) {
        this.solicitud_whatsapp = solicitud_whatsapp;
    }

    public String getSolicitud_aplicacion() {
        return solicitud_aplicacion;
    }

    public void setSolicitud_aplicacion(String solicitud_aplicacion) {
        this.solicitud_aplicacion = solicitud_aplicacion;
    }
}
