package com.example.asadordelchapulin;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;

public class Mesa {
    private String id, mesero, estado, cupo;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    private ArrayList <HashMap> clientes;

    private DocumentReference documentReference;

    public Mesa(String id, String mesero, ArrayList<HashMap> clientes, String estado, DocumentReference documentReference, String cupo) {
        this.id = id;
        this.mesero = mesero;
        this.clientes = clientes;
        this.estado=estado;
        this.documentReference=documentReference;
        this.cupo=cupo;
    }

    public String getCupo() {
        return cupo;
    }

    public void setCupo(String cupo) {
        this.cupo = cupo;
    }

    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(DocumentReference documentReference) {
        this.documentReference = documentReference;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMesero() {
        return mesero;
    }

    public void setMesero(String mesero) {
        this.mesero = mesero;
    }


    public ArrayList<HashMap> getClientes() {
        return clientes;
    }

    public void addCliente(String cliente){
        HashMap aux=new HashMap();
        aux.put("nombre",cliente);
        aux.put("folio","");
        clientes.add(aux);
        documentReference.update("clientes",clientes);
    }

    public void actualizarFolio(String folio,String cliente){
        for (HashMap aux:clientes){
            if(aux.get("nombre").toString().equals(cliente)){
                aux.put("folio",folio);
                documentReference.update("clientes",clientes);
                return;
            }
        }
    }

    public HashMap getCliente(String cliente){
        for(HashMap map:clientes){
            if(map.get("nombre").toString().equals(cliente)){
                return map;
            }
        }
        return null;
    }

    public void setClientes(ArrayList<HashMap> clientes) {
        this.clientes = clientes;
    }

    public String toString(){
        return id;
    }
}
