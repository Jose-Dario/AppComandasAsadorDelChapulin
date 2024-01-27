package com.example.asadordelchapulin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.tcp.TcpConnection;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.example.asadordelchapulin.async.AsyncEscPosPrint;
import com.example.asadordelchapulin.async.AsyncEscPosPrinter;
import com.example.asadordelchapulin.async.AsyncTcpEscPosPrint;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetalleCuentaFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public static HashMap cliente;

    private View view;

    private GridView contenedorDetalleCuenta;

    private AlertLoader alertLoader;
    private Button btnVerCuenta;

    private static NumeroLetras numeroLetras=new NumeroLetras();

    public DetalleCuentaFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static DetalleCuentaFragment newInstance(String param1, String param2) {
        DetalleCuentaFragment fragment = new DetalleCuentaFragment();
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
        view = inflater.inflate(R.layout.fragment_detalle_cuenta, container, false);
        ((TextView) view.findViewById(R.id.tvMesa)).setText("Mesa " + MesaDetalleFragment.mesa.getId());
        ((TextView) view.findViewById(R.id.tvCliente)).setText(cliente.get("nombre").toString());
        contenedorDetalleCuenta = view.findViewById(R.id.contenedorProductosCliente);
        btnVerCuenta = view.findViewById(R.id.btnVisualizarCuenta);
        ((Button) view.findViewById(R.id.btnAddOrden)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenerarOrdenFragment.comanda=null;
                GenerarOrdenFragment.modificacion = false;
                GenerarOrdenFragment.mesa = MesaDetalleFragment.mesa.getId();
                GenerarOrdenFragment.cliente = cliente.get("nombre").toString();
                GenerarOrdenFragment.mesero = MesaDetalleFragment.mesa.getMesero();
                Navigation.findNavController(view).navigate(R.id.nav_add_orden);
            }
        });

        ((Button)view.findViewById(R.id.btnAddFaltantes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenerarOrdenFragment.comanda=null;
                GenerarOrdenFragment.correcion=true;
                GenerarOrdenFragment.mesa = MesaDetalleFragment.mesa.getId();
                GenerarOrdenFragment.cliente = cliente.get("nombre").toString();
                GenerarOrdenFragment.mesero = MesaDetalleFragment.mesa.getMesero();
                Navigation.findNavController(view).navigate(R.id.nav_add_orden);
            }
        });

        btnVerCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Comanda> comandas = new ArrayList<>();
                ArrayList<ProductoOrdenado> listaProductosOrdenados=new ArrayList<>();
                for (Comanda aux : ComandasFragment.adapterComandas.getComandas()) {
                    if (aux.getCliente().equals(cliente.get("nombre").toString()) && aux.getMesa().equals(MesaDetalleFragment.mesa.getId())) {
                        comandas.add(aux);
                    }
                }
                for (Comanda aux : ComandasFragment.adapterComandas.getEntregadas()) {
                    if (aux.getCliente().equals(cliente.get("nombre").toString()) && aux.getMesa().equals(MesaDetalleFragment.mesa.getId())) {
                        comandas.add(aux);
                    }
                }
                for (Comanda aux : comandas) {
                    for (ProductoOrdenado productoOrdenado : aux.getCloneProductos()) {
                        buscarProducto(productoOrdenado, listaProductosOrdenados);
                    }
                }

                double consumoTotal=0;
                for(ProductoOrdenado productoOrdenado:listaProductosOrdenados){
                    consumoTotal+=productoOrdenado.getPrecio()* productoOrdenado.getCantidad();
                }

                AdapterDetalleCuenta adapterDetalleCuenta = new AdapterDetalleCuenta(getContext(), listaProductosOrdenados);
                contenedorDetalleCuenta.setAdapter(adapterDetalleCuenta);
                contenedorDetalleCuenta.setVisibility(View.VISIBLE);
                btnVerCuenta.setVisibility(View.GONE);
                ((TextView)view.findViewById(R.id.tituloCuenta)).setVisibility(View.VISIBLE);
                ((TextView)view.findViewById(R.id.tvTotalCuenta)).setText("Total: $"+consumoTotal);
                ((TextView)view.findViewById(R.id.tvTotalCuenta)).setVisibility(View.VISIBLE);
                contenedorDetalleCuenta.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ProductoOrdenado aux = adapterDetalleCuenta.getProductoOrdenado(position);
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.card_reponer_platillo, null);
                        ((TextView) dialogView.findViewById(R.id.nombrePlatilloReposicion)).setText(aux.getProducto());
                        EditText motivo = dialogView.findViewById(R.id.mensajeReposicion);
                        Spinner cantidad = dialogView.findViewById(R.id.spinnerCantidadResposicion);
                        ArrayList<Integer> cantidades = new ArrayList<>();
                        for (int i = 1; i <= aux.getCantidad(); i++) {
                            cantidades.add(i);
                        }
                        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, cantidades);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        cantidad.setAdapter(adapter);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setView(dialogView)
                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (motivo.getText().toString().equals("")) {
                                            Toast.makeText(getContext(), "No se solicitó la reposicion, ingresar motivo", Toast.LENGTH_SHORT).show();
                                        } else {
                                            AlertLoader alertLoader1 = new AlertLoader(getActivity());
                                            ArrayList<ProductoOrdenado> reposicion = new ArrayList<>();
                                            ProductoOrdenado productoOrdenado = new ProductoOrdenado(aux.getProducto(), 0, aux.getArea(), Integer.parseInt(cantidad.getSelectedItem().toString()), aux.getDescripcion());
                                            reposicion.add(productoOrdenado);
                                            Map comanda = new HashMap();
                                            comanda.put("cliente", cliente.get("nombre").toString());
                                            comanda.put("mesa", MesaDetalleFragment.mesa.getId());
                                            comanda.put("mesero", MesaDetalleFragment.mesa.getMesero());
                                            comanda.put("productos", reposicion);
                                            comanda.put("area", aux.getArea());
                                            comanda.put("estado", "en espera");
                                            comanda.put("fecha", new Date());
                                            comanda.put("mensaje", "reposición; "+motivo.getText().toString());
                                            FirebaseFirestore.getInstance().collection("Comandas").add(comanda).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {
                                                        alertLoader1.minimizar("Reposición ingresada");
                                                    } else {
                                                        alertLoader1.showError(task.getException().toString());
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setTitle("Reposición de producto");
                        alertDialog.show();
                    }
                });
            }
        });

        ((Button) view.findViewById(R.id.btnEliminarCuenta)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.card_medio_pago, null);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Spinner medioPago = dialogView.findViewById(R.id.medioPagoSpinner);

                ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, Utils.getMediosPago());
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                medioPago.setAdapter(arrayAdapter);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(dialogView)
                        //.setTitle("AlertDialog con Diseño Personalizado")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AlertLoader alertLoader = new AlertLoader(getActivity(), view);
                                ArrayList<Comanda> comandas = new ArrayList<>();
                                ArrayList<ProductoOrdenado> productosOrdenadosGuardar = new ArrayList<>();
                                for (Comanda aux : ComandasFragment.adapterComandas.getComandas()) {
                                    if (aux.getCliente().equals(cliente.get("nombre").toString()) && aux.getMesa().equals(MesaDetalleFragment.mesa.getId())) {
                                        comandas.add(aux);
                                    }
                                }

                                for (Comanda aux : ComandasFragment.adapterComandas.getEntregadas()) {
                                    if (aux.getCliente().equals(cliente.get("nombre").toString()) && aux.getMesa().equals(MesaDetalleFragment.mesa.getId())) {
                                        comandas.add(aux);
                                    }
                                }

                                for (Comanda aux : comandas) {
                                    for (ProductoOrdenado productoOrdenado : aux.getProductoOrdenados()) {
                                        buscarProducto(productoOrdenado, productosOrdenadosGuardar);
                                    }
                                }

                                double consumo=0;
                                for (ProductoOrdenado productoOrdenado:productosOrdenadosGuardar){
                                    consumo+=productoOrdenado.getCantidad()*productoOrdenado.getPrecio();
                                }

                                HashMap venta = new HashMap();
                                venta.put("total", consumo);
                                venta.put("cliente", cliente.get("nombre"));
                                venta.put("mesero", MesaDetalleFragment.mesa.getMesero());
                                venta.put("fecha", new Date());
                                venta.put("mesa", MesaDetalleFragment.mesa.getId());
                                venta.put("productosOrdenados", productosOrdenadosGuardar);
                                venta.put("folio", cliente.get("folio").toString());
                                venta.put("modoPago",medioPago.getSelectedItem().toString());
                                FirebaseFirestore.getInstance().collection("Ventas").add(venta).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            MesaDetalleFragment.mesa.getClientes().remove(cliente);
                                            MesaDetalleFragment.mesa.getDocumentReference().update("clientes", MesaDetalleFragment.mesa.getClientes()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        alertLoader.dimiss("Venta registrada");
                                                    } else {
                                                        alertLoader.showError("Ocurrió un error, intentelo nuevamente");
                                                    }
                                                }
                                            });
                                            removeComandasCliente();
                                        } else {
                                            alertLoader.showError("Ocurrió un error, intentelo nuevamente");
                                        }
                                    }

                                });
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        ((Button) view.findViewById(R.id.btnRemoveCuenta)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("¿Esta seguro de eliminar la cuenta?, se perderá la información relacionada")
                        .setCancelable(true)
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AlertLoader alert = new AlertLoader(getActivity(), view);
                                MesaDetalleFragment.mesa.getClientes().remove(cliente);
                                MesaDetalleFragment.mesa.getDocumentReference().update("clientes", MesaDetalleFragment.mesa.getClientes()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            alert.dimiss("Cuenta eliminada");
                                        } else {
                                            alert.showError("Error, Intentelo de nuevo");
                                        }
                                    }
                                });
                                removeComandasCliente();
                            }
                        });
                AlertDialog titulo = alert.create();
                titulo.setTitle("Eliminar cuenta");
                titulo.show();
            }
        });

        ((Button) view.findViewById(R.id.btnImprimirCuenta)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Comanda> comandas = new ArrayList<>();
                ArrayList<ProductoOrdenado> productosOrdenadosImprimir = new ArrayList<>();
                for (Comanda aux : ComandasFragment.adapterComandas.getComandas()) {
                    if (aux.getCliente().equals(cliente.get("nombre").toString()) && aux.getMesa().equals(MesaDetalleFragment.mesa.getId())) {
                        comandas.add(aux);
                    }
                }
                for (Comanda aux : ComandasFragment.adapterComandas.getEntregadas()) {
                    if (aux.getCliente().equals(cliente.get("nombre").toString()) && aux.getMesa().equals(MesaDetalleFragment.mesa.getId())) {
                        comandas.add(aux);
                    }
                }

                for (Comanda aux : comandas) {
                    for (ProductoOrdenado productoOrdenado : aux.getCloneProductos()) {
                        buscarProducto(productoOrdenado, productosOrdenadosImprimir);
                    }
                }
                if (!cliente.get("folio").toString().equals("")) {
                    imprimirTicketTall(productosOrdenadosImprimir);
                } else {
                    FirebaseFirestore.getInstance().collection("General").document("folio").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    int folio=Integer.parseInt(documentSnapshot.getString("folio"));
                                    cliente.put("folio", String.valueOf(folio));
                                    MesaDetalleFragment.mesa.actualizarFolio(String.valueOf(folio), cliente.get("nombre").toString());
                                    documentSnapshot.getReference().update("folio",String.valueOf(folio+1));
                                    imprimirTicketTall(productosOrdenadosImprimir);
                                }
                            }
                        }
                    });
                }


            }
        });

        return view;
    }

    public void imprimirTicket(ArrayList<ProductoOrdenado> productosOrdenadosImprimir){
        String textoImprimir = "\n";
        double total = 0;
        textoImprimir += "[C]Asador del chapulín restaurante y mezcalería\n";
        textoImprimir += "[C]Panamericana 190 Ranchería la caridad\n";
        textoImprimir += "[C]Berriozábal, Chiapas\n";
        textoImprimir += "[C]Berriozábal, México, CP. 29130\n";
        textoImprimir += "[L]\n";
        textoImprimir += "[C]<b>Folio " + cliente.get("folio").toString() + "</b>\n";
        textoImprimir += "[C]Mesa " + MesaDetalleFragment.mesa.getId() + "\n";
        textoImprimir += "[C]" + Utils.imprimirFecha(new Date()) + "\n";
        textoImprimir += "[C]-----------------------------------------------\n";
        textoImprimir += "[C]Cant.           Producto               Importe\n";
        for (ProductoOrdenado map : productosOrdenadosImprimir) {
            textoImprimir += "[L]" + Utils.imprimirProducto(map.getProducto(), map.getCantidad(), map.getPrecio());
            total += map.getCantidad() * map.getPrecio();
            if(map.getArea().equals("comal")){
                textoImprimir+="[L]"+Utils.imprimirDescripcion(map.getDescripcion())+"\n";
            }
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
        System.out.println(textoImprimir);
        printTcp(textoImprimir);
    }

    public void imprimirTicketTall(ArrayList<ProductoOrdenado> productosOrdenadosImprimir){
        String textoImprimir = "\n";
        double total = 0;
        textoImprimir += "[C]<font size='big'>   </font>\n";
        textoImprimir += "[C]<font size='normal'>Asador del chapulín restaurante y mezcalería\n";
        textoImprimir += "[C]Panamericana 190 Ranchería la caridad\n";
        textoImprimir += "[C]Berriozábal, Chiapas\n";
        textoImprimir += "[C]Berriozábal, México, CP. 29130\n";
        textoImprimir += "[L]\n";
        textoImprimir += "[C]<b>Folio " + cliente.get("folio").toString() + "</b>\n";
        textoImprimir += "[C]Mesa " + MesaDetalleFragment.mesa.getId() + "\n";
        textoImprimir += "[C]" + Utils.imprimirFecha(new Date()) + "\n";
        textoImprimir += "[C]-----------------------------------------------\n";
        textoImprimir += "[C]Cant.           Producto               Importe\n";
        for (ProductoOrdenado map : productosOrdenadosImprimir) {
            textoImprimir += "[L]" + Utils.imprimirProducto(map.getProducto(), map.getCantidad(), map.getPrecio());
            total += map.getCantidad() * map.getPrecio();
            if(map.getArea().equals("comal")){
                textoImprimir+="[L]"+Utils.imprimirDescripcion(map.getDescripcion())+"\n";
            }
        }
        textoImprimir +="</font>";
        textoImprimir += "[C]----------------------------------------------\n";
        textoImprimir += "[R]<font size='big'>Total: $" + total + "</font>\n";
        textoImprimir += "[C]<font size='tall'><b>(" +numeroLetras.Convertir(String.valueOf(total),"peso","pesos","centavo","centavos","y",false)+ ")</b></font>\n";
        textoImprimir += "[L]\n";
        textoImprimir += "[C]<b>Este ticket no es comprobante fiscal</b>\n";
        textoImprimir += "[L]\n";
        textoImprimir += "[L]\n";
        textoImprimir += "[L]\n";
        textoImprimir += "[L]\n";
        //System.out.println(new NumeroLetras().Convertir(String.valueOf(total),"peso", "pesos","centavo","centavos","y",false));
        printTcp(textoImprimir);
    }
    public void removeComandasCliente() {
        for (Comanda comanda : ComandasFragment.adapterComandas.getComandas()) {
            if (comanda.getMesa().equals(MesaDetalleFragment.mesa.getId())) {
                if (comanda.getCliente().equals(cliente.get("nombre").toString())) {
                    removeComanda(comanda.getDocumentReference());
                }
            }
        }
        for (Comanda comanda:ComandasFragment.adapterComandas.getEntregadas()){
            if (comanda.getMesa().equals(MesaDetalleFragment.mesa.getId())) {
                if (comanda.getCliente().equals(cliente.get("nombre").toString())) {
                    removeComanda(comanda.getDocumentReference());
                }
            }
        }
    }
    public void buscarProducto(ProductoOrdenado productoOrdenado, ArrayList<ProductoOrdenado> productosOrdenados) {
        if(productoOrdenado.getPrecio()==0){
            return;
        }
        if(productoOrdenado.getArea().equals("comal")){
            productosOrdenados.add(productoOrdenado);
            return;
        }
        for (ProductoOrdenado aux : productosOrdenados) {
            if (aux.getProducto().equals(productoOrdenado.getProducto()) && aux.getPrecio()==productoOrdenado.getPrecio()) {
                aux.setCantidad(aux.getCantidad() + productoOrdenado.getCantidad());
                return;
            }
        }
        productosOrdenados.add(productoOrdenado);
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

    public void removeComanda(DocumentReference documentReference){
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    removeComanda(documentReference);
                }
            }
        });
    }
}