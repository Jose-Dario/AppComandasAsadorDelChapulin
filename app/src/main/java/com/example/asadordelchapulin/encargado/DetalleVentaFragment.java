package com.example.asadordelchapulin.encargado;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.tcp.TcpConnection;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.example.asadordelchapulin.R;
import com.example.asadordelchapulin.AlertLoader;
import com.example.asadordelchapulin.Utils;
import com.example.asadordelchapulin.async.AsyncEscPosPrint;
import com.example.asadordelchapulin.async.AsyncEscPosPrinter;
import com.example.asadordelchapulin.async.AsyncTcpEscPosPrint;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetalleVentaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetalleVentaFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    public static Venta venta;

    public AlertLoader alertLoader;

    private TextView fecha, cliente, mesero, mesa,total,folio, formaPago;

    private LinearLayout linearLayout;

    public DetalleVentaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetalleVentaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetalleVentaFragment newInstance(String param1, String param2) {
        DetalleVentaFragment fragment = new DetalleVentaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view=inflater.inflate(R.layout.fragment_detalle_venta, container, false);
         fecha=view.findViewById(R.id.tvFechaDetalle);
         cliente=view.findViewById(R.id.tvClienteDetalle);
         mesero=view.findViewById(R.id.tvMeseroDetalle);
         mesa=view.findViewById(R.id.tvMesaDetalle);
         total=view.findViewById(R.id.tvTotal);
         folio=view.findViewById(R.id.tvFolioDetalle);
         formaPago=view.findViewById(R.id.tvFormaPago);

         fecha.setText(Utils.imprimirFecha(venta.getFecha()));
         cliente.setText(venta.getCliente());
         mesero.setText(venta.getMesero());
         total.setText("$ "+venta.getTotal());
         mesa.setText(venta.getMesa());
         folio.setText(venta.getFolio());
         formaPago.setText(venta.getFormaPago());

         if(venta.getProductosOrdenados()!=null){
             linearLayout=view.findViewById(R.id.detalleVentaShow);
             for (HashMap map:venta.getProductosOrdenados()){
                 View aux=getActivity().getLayoutInflater().inflate(R.layout.card_producto_ordenado_cuenta,null);
                 ((TextView)aux.findViewById(R.id.tvCantProductoCuenta)).setText(map.get("cantidad").toString()+"");
                 ((TextView)aux.findViewById(R.id.tvNombreProductoOrdenado)).setText(map.get("producto").toString());
                 ((TextView)aux.findViewById(R.id.tvSubtotal)).setText("$ "+(Integer.parseInt(map.get("cantidad").toString())*Double.parseDouble(map.get("precio").toString())));
                 linearLayout.addView(aux);
             }
         }



        ((Button)view.findViewById(R.id.btnImprimirTicket)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imprimirTicket(venta.getProductosOrdenados());
            }
        });

        ((Button)view.findViewById(R.id.btnRemoveVenta)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertLoader alertLoader1=new AlertLoader(getActivity(),view);
                venta.getDocumentReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            alertLoader1.dimiss("Venta eliminada");
                        }
                        else {
                            alertLoader1.showError(task.getException().toString());
                        }
                    }
                });
            }
        });
         return view;
    }

    public void imprimirTicket(ArrayList<HashMap> productosOrdenadosImprimir){
        String textoImprimir = "\n";
        double total = 0;
        textoImprimir += "[C]Asador del chapulín restaurante y mezcalería\n";
        textoImprimir += "[C]Panamericana 190 Ranchería la caridad\n";
        textoImprimir += "[C]Berriozábal, Chiapas\n";
        textoImprimir += "[C]Berriozábal, México, CP. 29130\n";
        textoImprimir += "[L]\n";
        textoImprimir += "[C]<b>Folio " + venta.getFolio() + "</b>\n";
        textoImprimir += "[C]Mesa " + venta.getMesa() + "\n";
        textoImprimir += "[C]" + Utils.imprimirFecha(venta.getFecha()) + "\n";
        textoImprimir += "[C]-----------------------------------------------\n";
        textoImprimir += "[C]Cant.           Producto               Importe\n";
        for (Map map : productosOrdenadosImprimir) {
            textoImprimir += "[L]" + Utils.imprimirProducto(map.get("producto").toString(), Integer.parseInt(map.get("cantidad").toString()), Double.parseDouble(map.get("precio").toString()));
            total += Integer.parseInt(map.get("cantidad").toString()) * Double.parseDouble(map.get("precio").toString());
        }
        textoImprimir += "[C]----------------------------------------------\n";
        textoImprimir += "[L][L][R]Total: $" + total + "\n";
        textoImprimir += "[C](" + Utils.convertirNumeroEnPalabras(total) + ")\n";
        textoImprimir += "[L]\n";
        textoImprimir += "[C]<b>Este ticket no es comprobante fiscal</b>\n";
        textoImprimir += "[L]\n";
        textoImprimir += "[L]\n";
        textoImprimir += "[L]\n";
        textoImprimir += "[L]\n";
        printTcp(textoImprimir);
    }


    public void printTcp(String cadena) {
        try {
            new AsyncTcpEscPosPrint(
                    getContext(),
                    new AsyncEscPosPrint.OnPrintFinished() {
                        @Override
                        public void onError(AsyncEscPosPrinter asyncEscPosPrinter, int codeException) {
                            Log.e("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : An error occurred !");
                        }

                        @Override
                        public void onSuccess(AsyncEscPosPrinter asyncEscPosPrinter) {
                            Log.i("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : Print is finished !");
                        }
                    }
            )
                    .execute(
                            this.getAsyncEscPosPrinter(
                                    new TcpConnection(
                                            "192.168.1.254",
                                            9100)
                                    , cadena)
                    );
        } catch (NumberFormatException e) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Invalid TCP port address")
                    .setMessage("Port field must be an integer.")
                    .show();
            e.printStackTrace();
        }
    }


    public AsyncEscPosPrinter getAsyncEscPosPrinter(DeviceConnection printerConnection, String cadena) {
        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 203, 72f, 48);
        return printer.addTextToPrint(
                "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, getContext().getResources().getDrawableForDensity(R.drawable.logito, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" + cadena
        );
    }

}