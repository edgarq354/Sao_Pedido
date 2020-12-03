package com.elisoft.sao_pedido.SqLite;

/**
 * Created by elisoft on 07-11-16.
 */import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String nombre, CursorFactory factory, int version) {
        super(context, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table chat(" +
                "id integer default 0," +
                "titulo text," +
                "mensaje text," +
                "fecha text," +
                "hora text," +
                "id_usuario text," +
                "id_conductor text,"+
                "estado integer default 0," +
                "yo integer default 0," +
                "tipo text ," +
                "audio text ," +
                "primary key(id,id_usuario,id_conductor))");

        db.execSQL("create table direccion(" +
                "id integer," +
                "detalle text," +
                "latitud text," +
                "longitud text," +
                "id_empresa integer," +
                "id_usuario integer,"+
                "nombre text)");



        db.execSQL("create table notificacion(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT not null," +
                "titulo text," +
                "mensaje text," +
                "cliente text," +
                "id_pedido text," +
                "nombre text,"+
                "latitud text," +
                "longitud text," +
                "tipo text," +
                "fecha text," +
                "hora text," +
                "leido integer default 0," +
                "indicacion text)");
//guia turistica CATEGORIA DE LUGAR
        db.execSQL("create table categoria(" +
                "id integer not null," +
                "nombre text not null," +
                "direccion_imagen text," +
                "estado_pedido integer default 0)");

//guia turistica  GUIA COMERCIAL
        db.execSQL("create table lugar(" +
                "id integer not null," +
                "nombre text not null," +
                "direccion text not null," +
                "telefono text not null," +
                "whatsapp text not null," +
                "latitud text," +
                "longitud text," +
                "estado integer default 0," +
                "id_categoria integer," +
                "direccion_logo text," +
                "distancia integer default 0," +
                "facebook text," +
                "informacion text," +
                "estado_pedido integer," +
                "direccion_banner text," +
                "tiempo_preparacion text," +
                "abierto_cerrado text," +
                "monto_minimo text," +
                "solicitud_whatsapp text," +
                "solicitud_aplicacion text" +
                ")");

        db.execSQL("create table carrito(" +
                "id_pedido integer not null," +
                "id_producto integer not null," +
                "cantidad integer not null," +
                "monto_unidad integer not null," +
                "monto_total integer not null," +
                "nombre text," +
                "descripcion text," +
                "url text ,"+
                "primary key(id_pedido,id_producto))");

        db.execSQL("create table producto(" +
                "id_lugar integer not null," +
                "id_producto integer not null," +
                "nombre text not null," +
                "descripcion text not null," +
                "precio text not null," +
                "imagen1 text not null," +
                "imagen2 text not null," +
                "imagen3 text not null," +
                "imagen4 text not null," +
                "imagen5 text not null," +
                "estado_pedido integer not null," +
                "id_tipo_producto text not null," +
                "primary key(id_lugar,id_producto))");

        db.execSQL("create table carrera(" +
                "id integer," +
                "latitud_inicio text," +
                "longitud_inicio text," +
                "latitud_fin text ," +
                "longitud_fin text ," +
                "distancia text," +
                "tiempo text," +
                "fecha_inicio text," +
                "fecha_fin text," +
                "id_pedido integer," +
                "id_conductor integer," +
                "monto text," +
                "ruta text," +
                "direccion_inicio text," +
                "direccion_fin text" +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnte, int versionNue) {
        db.execSQL("drop table if exists chat");
        db.execSQL("create table chat(" +
                "id integer default 0," +
                "titulo text," +
                "mensaje text," +
                "fecha text," +
                "hora text," +
                "id_usuario text," +
                "id_conductor text,"+
                "estado integer default 0," +
                "yo integer default 0," +
                "tipo text ," +
                "audio text ," +
                "primary key(id,id_usuario,id_conductor))");



        db.execSQL("drop table if exists notificacion");
        db.execSQL("create table notificacion(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT not null," +
                "titulo text," +
                "mensaje text," +
                "cliente text," +
                "id_pedido text," +
                "nombre text,"+
                "latitud text," +
                "longitud text," +
                "tipo text," +
                "fecha text," +
                "hora text," +
                "leido integer default 0," +
                "indicacion  text)");


        //guia turistica CATEGORIA DE LUGAR
        db.execSQL("drop table if exists categoria");
        db.execSQL("create table categoria(" +
                "id integer not null," +
                "nombre text not null," +
                "direccion_imagen text," +
                "estado_pedido integer default 0)");



//guia turistica  GUIA COMERCIA
        db.execSQL("drop table if exists lugar");
        db.execSQL("create table lugar(" +
                "id integer not null," +
                "nombre text not null," +
                "direccion text not null," +
                "telefono text not null," +
                "whatsapp text not null," +
                "latitud text ," +
                "longitud text ," +
                "estado integer default 0," +
                "id_categoria integer," +
                "direccion_logo text," +
                "distancia integer default 0," +
                "facebook text," +
                "informacion text," +
                "estado_pedido integer," +
                "direccion_banner text," +
                "tiempo_preparacion text," +
                "abierto_cerrado text," +
                "monto_minimo text," +
                "solicitud_whatsapp text," +
                "solicitud_aplicacion text" +
                ")");
        //CARRITO
        db.execSQL("drop table if exists carrito");
        db.execSQL("create table carrito(" +
                "id_pedido integer not null," +
                "id_producto integer not null," +
                "cantidad integer not null," +
                "monto_unidad integer not null," +
                "monto_total integer not null," +
                "nombre text," +
                "descripcion text," +
                "url text ,"+
                "primary key(id_pedido,id_producto))");

        db.execSQL("drop table if exists producto");
        db.execSQL("create table producto(" +
                "id_lugar integer not null," +
                "id_producto integer not null," +
                "nombre text not null," +
                "descripcion text not null," +
                "precio text not null," +
                "imagen1 text not null," +
                "imagen2 text not null," +
                "imagen3 text not null," +
                "imagen4 text not null," +
                "imagen5 text not null," +
                "estado_pedido integer not null," +
                "id_tipo_producto text not null," +
                "primary key(id_lugar,id_producto))");
        db.execSQL("drop table if exists carrera");
        db.execSQL("create table carrera(" +
                "id integer," +
                "latitud_inicio text," +
                "longitud_inicio text," +
                "latitud_fin text ," +
                "longitud_fin text ," +
                "distancia text," +
                "tiempo text," +
                "fecha_inicio text," +
                "fecha_fin text," +
                "id_pedido integer," +
                "id_conductor integer," +
                "monto text," +
                "ruta text," +
                "direccion_inicio text," +
                "direccion_fin text" +
                ")");
    }
}

