package com.example.asadordelchapulin.encargado;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.asadordelchapulin.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Platillo implements Comparable<Platillo>{
    private String nombre;
    private String descripcion;
    private double precio;

    private String area;
    private boolean existencia;

    private Uri direccion;

    public Platillo(String nombre, String descripcion,double precio, boolean existencia, String area) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.existencia=existencia;
        this.area=area;
    }

    public void setExistencia(boolean existencia){
        this.existencia=existencia;
    }

    public boolean getExistencia(){
        return existencia;
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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }



    public Uri getUri(){
        if(direccion!=null){
            return direccion;
        }
        else{
            direccion=Utils.getUri(nombre);
            return direccion;
        }
    }

    public String getArea(){
            return   area;

    }
    public String toString(){
        return nombre;
    }
    @Override
    public int compareTo(Platillo o) {
        return this.nombre.compareTo(o.getNombre());
    }
}
