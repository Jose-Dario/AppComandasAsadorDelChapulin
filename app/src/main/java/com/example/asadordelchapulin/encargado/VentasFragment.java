package com.example.asadordelchapulin.encargado;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.tcp.TcpConnection;
import com.example.asadordelchapulin.AlertLoader;
import com.example.asadordelchapulin.R;
import com.example.asadordelchapulin.Utils;
import com.example.asadordelchapulin.async.AsyncEscPosPrint;
import com.example.asadordelchapulin.async.AsyncEscPosPrinter;
import com.example.asadordelchapulin.async.AsyncTcpEscPosPrint;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VentasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VentasFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AlertLoader alertLoader;

    private View view;

    private TextView ventaTotal,ventaEfectivo,ventaTransferencia;
    private Button imprimirReporte;

    private Button verVentas;

    private LinearLayout contendorVentas;

    private double venta=0,efectivo=0,transferencia=0;

    private ArrayList<Venta> ventas;

    public VentasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VentasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VentasFragment newInstance(String param1, String param2) {
        VentasFragment fragment = new VentasFragment();
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
        view= inflater.inflate(R.layout.fragment_ventas, container, false);
        contendorVentas=view.findViewById(R.id.contendorVentas);
        ventaTotal=view.findViewById(R.id.tvVentaTotal);
        ventaEfectivo=view.findViewById(R.id.tvEfectivo);
        ventaTransferencia=view.findViewById(R.id.tvTransferencia);
        imprimirReporte=view.findViewById(R.id.btnImprimirReporte);
        ventas=new ArrayList<>();
        verVentas=view.findViewById(R.id.btnVerVentas);
        verVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verVentas.setVisibility(View.GONE);
                consultarVentas();
            }
        });
        ((Button)view.findViewById(R.id.btnVerCortes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.nav_cortes);
            }
        });

        imprimirReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ventas.size()>0){
                    AlertLoader alertLoader1=new AlertLoader(getActivity());
                    HashMap hashMap=new HashMap();
                    hashMap.put("Transferencia",transferencia);
                    hashMap.put("Efectivo",efectivo);
                    hashMap.put("Total",venta);
                    hashMap.put("Fecha",new Date());
                    hashMap.put("Cuentas",ventas);
                    FirebaseFirestore.getInstance().collection("Cortes").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                for (Venta venta:ventas){
                                    removeVenta(venta.getDocumentReference());
                                }
                                verVentas.setVisibility(View.VISIBLE);
                                ventaTotal.setText("");
                                ventaEfectivo.setText("");
                                ventaTransferencia.setText("");
                                contendorVentas.removeAllViews();
                                alertLoader1.minimizar("Corte realizado exitosamente");
                            }
                            else{
                                alertLoader1.showError(task.getException().toString());
                                Toast.makeText(getContext(),"Ocurrió un error al registrar el corte en la base de datos",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getContext(),"No hay ventas que registrar",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;

    }

    public void removeVenta(DocumentReference documentReference){
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    removeVenta(documentReference);
                }
            }
        });
    }



    public void consultarVentas(){
        AlertLoader alertLoader=new AlertLoader(getActivity());
        ventas=new ArrayList<>();
        contendorVentas.removeAllViews();
        venta=0;
        efectivo=0;
        transferencia=0;
        ventaTotal.setText("");
        FirebaseFirestore.getInstance().collection("Ventas").orderBy("fecha", Query.Direction.ASCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot doc:task.getResult()){
                        View v;
                        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
                        v= layoutInflater.inflate(R.layout.card_venta,null);
                        Venta detalleVenta=new Venta(doc.getReference(), doc.getString("mesa"),doc.getString("mesero"),doc.getString("cliente"),doc.getDate("fecha"), doc.getDouble("total"), doc.getString("folio"),(ArrayList<HashMap>) doc.get("productosOrdenados"),doc.getString("modoPago"));
                        ventas.add(detalleVenta);
                        ((TextView)v.findViewById(R.id.tvFechaCorte)).setText(doc.getString("cliente"));
                        ((TextView)v.findViewById(R.id.tvTotalCorte)).setText(doc.getString("mesero"));
                        ((TextView)v.findViewById(R.id.tvMesaCuenta)).setText(doc.getString("mesa"));
                        ((TextView)v.findViewById(R.id.tvTotalMesa)).setText("$ "+doc.getDouble("total"));
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DetalleVentaFragment.venta=detalleVenta;
                                Navigation.findNavController(view).navigate(R.id.nav_detalle_venta);
                            }
                        });
                        contendorVentas.addView(v);
                        venta+=doc.getDouble("total");
                        if(detalleVenta.getFormaPago().equalsIgnoreCase("Efectivo")){
                            efectivo+=detalleVenta.getTotal();
                        }
                        else{
                            transferencia+=detalleVenta.getTotal();
                        }
                    }
                    alertLoader.finalizarLoader();
                }
                else{
                    alertLoader.showError(task.getException().toString());
                }
                ventaEfectivo.setText("Efectivo: $ "+efectivo);
                ventaTransferencia.setText("Transferencia: $ "+transferencia);
                ventaTotal.setText("Venta total: $ "+venta);
            }
        });
    }




}