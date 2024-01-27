package com.example.asadordelchapulin.encargado;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Venta {
    private String mesa, mesero, cliente, formaPago;
    private Date fecha;
    private double total;
    private ArrayList<HashMap> productosOrdenados;

    private String folio;
    private DocumentReference documentReference;

    public Venta(DocumentReference documentReference, String mesa, String mesero, String cliente, Date fecha, double total, String folio, ArrayList<HashMap> productosOrdenados, String formaPago) {
        this.documentReference=documentReference;
        this.mesa = mesa;
        this.mesero = mesero;
        this.cliente = cliente;
        this.fecha = fecha;
        this.total = total;
        this.folio = folio;
        this.productosOrdenados = productosOrdenados;
        this.formaPago=formaPago;
    }

    public Venta(String mesa, String mesero, String cliente, Date fecha, double total, String folio,String formaPago) {
        this.mesa = mesa;
        this.mesero = mesero;
        this.cliente = cliente;
        this.fecha = fecha;
        this.total = total;
        this.folio = folio;
        this.formaPago=formaPago;
    }

    public String getMesa() {
        return mesa;
    }

    public void setMesa(String mesa) {
        this.mesa = mesa;
    }

    public String getMesero() {
        return mesero;
    }

    public void setMesero(String mesero) {
        this.mesero = mesero;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public ArrayList<HashMap> getProductosOrdenados() {
        return productosOrdenados;
    }

    public void setProductosOrdenados(ArrayList<HashMap> productosOrdenados) {
        this.productosOrdenados = productosOrdenados;
    }

    public DocumentReference getDocumentReference(){
        return documentReference;
    }
    public String getFormaPago() {
        return formaPago;
    }

    public String getFolio() {
        return folio;
    }
}
