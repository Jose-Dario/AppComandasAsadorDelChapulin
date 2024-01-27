package com.example.asadordelchapulin;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentReference;

public class Usuario {
    private String nombre, correo, rol;
    private DocumentReference documentReference;
    private Bitmap imgProfile;
    private boolean img=false;

    private String profileReference;
    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(DocumentReference documentReference) {
        this.documentReference = documentReference;
    }

    public Usuario(String nombre, String correo, String rol, DocumentReference documentReference, String profileReference) {
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.documentReference=documentReference;
        this.profileReference=profileReference;
    }



    public Usuario(String nombre, String correo, String rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Bitmap getImgProfile() {
        return imgProfile;
    }

    public void cargarImagen(ImageView imageView){
        if(imgProfile==null){
            if(!img){
                Utils.downloadImageProfile(imageView);
            }
        }
        else {
            imageView.setImageBitmap(imgProfile);
        }
    }

    public void cargarProfileUser(ImageView imageView){
        if(imgProfile==null){
            if(!img){
                Utils.downloadImageProfile(this,imageView);
            }
        }
        else {
            imageView.setImageBitmap(imgProfile);
        }
    }

    public void setImgProfile(Bitmap imgProfile) {
        this.imgProfile = imgProfile;
    }

    public boolean isImg() {
        return img;
    }

    public void setImg(boolean img) {
        this.img = img;
    }

    public String getProfileReference(){
        return profileReference;
    }
}
