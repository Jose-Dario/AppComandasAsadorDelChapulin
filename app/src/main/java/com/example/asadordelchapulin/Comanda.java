package com.example.asadordelchapulin;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;

public class Comanda {
    private String mesero, cliente, mesa, estado, area, mensaje;
    private ArrayList<ProductoOrdenado> productoOrdenados;

    private ArrayList<HashMap> productos;
    private DocumentReference documentReference;

    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(DocumentReference documentReference) {
        this.documentReference = documentReference;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Comanda(String mesero, String cliente, String mesa, String estado, ArrayList<HashMap> productos, DocumentReference documentReference,String area,String mensaje) {
        this.mesero = mesero;
        this.cliente = cliente;
        this.mesa = mesa;
        this.estado = estado;
        this.area=area;
        this.productos=productos;
        this.productoOrdenados = new ArrayList<>();
        for (HashMap producto:productos){
            productoOrdenados.add(new ProductoOrdenado(producto.get("producto").toString(),Double.parseDouble(producto.get("precio").toString()),
                    producto.get("area").toString(),Integer.parseInt(producto.get("cantidad").toString()),producto.get("descripcion").toString()));
        }
        this.documentReference=documentReference;
        this.mensaje=mensaje;
    }

    public ArrayList<HashMap> getHash(){
        return productos;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
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

    public String getMesa() {
        return mesa;
    }

    public void setMesa(String mesa) {
        this.mesa = mesa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public ArrayList<ProductoOrdenado> getProductoOrdenados() {
        return productoOrdenados;
    }

    public ArrayList<ProductoOrdenado> getCloneProductos(){
        ArrayList<ProductoOrdenado> aux=new ArrayList<>();
        for (ProductoOrdenado producto:productoOrdenados){
            aux.add(new ProductoOrdenado(producto.getProducto(),producto.getPrecio(),producto.getArea(),producto.getCantidad(),producto.getDescripcion()));
        }
        return aux;
    }

    public void setProductoOrdenados(ArrayList<ProductoOrdenado> productoOrdenados) {
        this.productoOrdenados = productoOrdenados;
    }

    public double getSubtotal(){
        double subtotal=0;
        for (ProductoOrdenado aux:productoOrdenados){
            subtotal+=aux.getCantidad()*aux.getPrecio();
        }
        return subtotal;
    }
}
