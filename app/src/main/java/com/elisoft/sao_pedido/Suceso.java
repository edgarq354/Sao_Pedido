package com.elisoft.sao_pedido;

/**
 * Created by elisoft on 02-11-16.
 */

public class Suceso {
    private String suceso;
    private String mensaje;

    public Suceso()
    {
        suceso="0";
        mensaje="Error: Al conectar con el servidor.";
    }
    public Suceso(String suceso, String mensaje)
    {
        this.suceso=suceso;
        this.mensaje=mensaje;
    }

    public String getSuceso() {
        return suceso;
    }

    public String getMensaje() {
        return mensaje;
    }
}
