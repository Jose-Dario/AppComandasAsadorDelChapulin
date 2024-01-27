package com.example.asadordelchapulin;

import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class Mensaje {
    private String user, contenido;
    private Date fecha;

    private DocumentReference documentReference;

    public Mensaje(String user, String contenido, Date fecha, DocumentReference documentReference) {
        this.user = user;
        this.contenido = contenido;
        this.fecha = fecha;
        this.documentReference=documentReference;
    }

    public DocumentReference getDocumentReference(){
        return documentReference;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
