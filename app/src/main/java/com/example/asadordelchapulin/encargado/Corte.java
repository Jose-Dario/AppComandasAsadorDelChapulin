package com.example.asadordelchapulin.encargado;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Corte {
    private Date fecha;
    private ArrayList<HashMap> cuentas;
    private double total;
    private double efectivo;
    private double transferencia;

    public Corte(Date fecha, ArrayList<HashMap> cuentas,double total,double efectivo, double transferencia){
        this.fecha=fecha;
        this.cuentas=cuentas;
        this.total=total;
        this.efectivo=efectivo;
        this.transferencia=transferencia;
    }

    public Date getFecha(){
        return fecha;
    }

    public double getTotal(){
        return total;
    }

    public double getEfectivo(){
        return efectivo;
    }

    public double getTransferencia(){
        return transferencia;
    }
}
