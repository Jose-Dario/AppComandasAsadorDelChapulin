package com.example.asadordelchapulin.encargado;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.tcp.TcpConnection;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.example.asadordelchapulin.AlertLoader;
import com.example.asadordelchapulin.R;
import com.example.asadordelchapulin.Utils;
import com.example.asadordelchapulin.async.AsyncEscPosPrint;
import com.example.asadordelchapulin.async.AsyncEscPosPrinter;
import com.example.asadordelchapulin.async.AsyncTcpEscPosPrint;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CortesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CortesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<Corte> cortes;

    private View view;
    private double venta = 0, efectivo = 0, transferencia = 0;

    private TextView tvVenta, tvEfectivo, tvTransferencia;

    private TextView tvFechaCorte;

    private Date date;

    private LinearLayout contenedorCortes;

    public CortesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CortesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CortesFragment newInstance(String param1, String param2) {
        CortesFragment fragment = new CortesFragment();
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
        view = inflater.inflate(R.layout.fragment_cortes, container, false);
        contenedorCortes = view.findViewById(R.id.contenedorCortes);
        tvFechaCorte = view.findViewById(R.id.tvFechaCortes);
        tvVenta = view.findViewById(R.id.tvTotalDia);
        tvEfectivo = view.findViewById(R.id.tvEfectivoCorteDi);
        tvTransferencia = view.findViewById(R.id.tvTransferenciaDi);
        Calendar calendar = Calendar.getInstance();
        tvFechaCorte.setText(calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR));
        consultarCortes(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        ((ImageButton) view.findViewById(R.id.btnSelectDay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.card_calendar, null);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) CalendarView calendarView = dialogView.findViewById(R.id.calendarView);

                // Crea el AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(dialogView)
                        //.setTitle("AlertDialog con Diseño Personalizado")
                        .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Acciones a realizar cuando se hace clic en Aceptar
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        tvFechaCorte.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        date = new Date(year, month, dayOfMonth);
                        consultarCortes(year, month, dayOfMonth);
                        alertDialog.dismiss();
                    }
                });
            }
        });

        ((Button) view.findViewById(R.id.btnImprimirVentaDia)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cortes.size() > 0) {
                    Date fecha = new Date();
                    String textoImprimir = "\n";
                    textoImprimir += "[C]REPORTE DE VENTA DEL DÍA\n";
                    textoImprimir += "[C]"+ Utils.printFecha(fecha) + "\n\n";

                    for (int i = 0; i < cortes.size(); i++) {
                        if(i==0){
                            textoImprimir += "[C]CORTE DESAYUNOS \n";
                            textoImprimir += "[L]EFECTIVO: $" + cortes.get(i).getEfectivo() + "\n";
                            textoImprimir += "[L]TRANSFERENCIA: $" + cortes.get(i).getTransferencia() + "\n";
                            textoImprimir += "[L]SUBTOTAL: $" + cortes.get(i).getTotal() + "\n\n";
                        }
                        else if(i==1){
                            textoImprimir += "[C]CORTE COMIDAS \n";
                            textoImprimir += "[L]EFECTIVO: $" + cortes.get(i).getEfectivo() + "\n";
                            textoImprimir += "[L]TRANSFERENCIA: $" + cortes.get(i).getTransferencia() + "\n";
                            textoImprimir += "[L]SUBTOTAL: $" + cortes.get(i).getTotal() + "\n\n";
                        }
                        else{
                            textoImprimir += "[C]CORTE NÚMERO "+(i+1)+"\n";
                            textoImprimir += "[L]EFECTIVO: $" + cortes.get(i).getEfectivo() + "\n";
                            textoImprimir += "[L]TRANSFERENCIA: $" + cortes.get(i).getTransferencia() + "\n";
                            textoImprimir += "[L]SUBTOTAL: $" + cortes.get(i).getTotal() + "\n\n\n";
                        }
                    }
                    textoImprimir += "[L]<b>TOTAL EFECTIVO: $" + efectivo + "</b>\n";
                    textoImprimir += "[L]<b>TOTAL TRANSFERENCIA: $" + transferencia + "</b>\n";
                    textoImprimir += "[L]<b>VENTA TOTAL: $" + venta +"</b>\n";
                    textoImprimir += "[L]\n";
                    textoImprimir += "[L]\n";
                    textoImprimir += "[L]\n";
                    textoImprimir += "[L]\n";
                    printTcp(textoImprimir);
                } else {
                    Toast.makeText(getContext(), "No se realizaron cortes para el día seleccionado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }


    public void consultarCortes(int year, int month, int day) {
        AlertLoader alertLoader = new AlertLoader(getActivity());
        venta = 0;
        efectivo = 0;
        transferencia = 0;
        cortes = new ArrayList<>();
        contenedorCortes.removeAllViews();
        Timestamp inicioDelDia = new Timestamp(new Date(year - 1900, month, day, 0, 0)); // Año, Mes (0-11), Día, Hora, Minuto
        Timestamp finDelDia = new Timestamp(new Date(year - 1900, month, day, 23, 59));
        FirebaseFirestore.getInstance().collection("Cortes")
                .whereGreaterThanOrEqualTo("Fecha", inicioDelDia)  // Documentos con fecha mayor o igual al inicio del día
                .whereLessThanOrEqualTo("Fecha", finDelDia)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                View v;
                                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                                v = layoutInflater.inflate(R.layout.card_corte, null);
                                Corte corte = new Corte(doc.getDate("Fecha"), (ArrayList<HashMap>) doc.get("Cuentas"), doc.getDouble("Total"), doc.getDouble("Efectivo"), doc.getDouble("Transferencia"));
                                cortes.add(corte);
                                ((TextView) v.findViewById(R.id.tvFechaCorte)).setText(Utils.imprimirFecha(corte.getFecha()));
                                ((TextView) v.findViewById(R.id.tvTotalCorte)).setText(" $" + corte.getTotal());
                                ((TextView) v.findViewById(R.id.tvEfectivoCorte)).setText(" $" + corte.getEfectivo());
                                ((TextView) v.findViewById(R.id.tvTransferenciaCorte)).setText(" $" + corte.getTransferencia());

                                v.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                });
                                contenedorCortes.addView(v);
                                venta += corte.getTotal();
                                efectivo += corte.getEfectivo();
                                transferencia += corte.getTransferencia();
                            }
                            tvVenta.setText(" $ " + venta);
                            tvEfectivo.setText(" $ " + efectivo);
                            tvTransferencia.setText(" $ " + transferencia);
                            alertLoader.finalizarLoader();
                        } else {
                            alertLoader.showError(task.getException().toString());
                        }
                    }
                });
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
                cadena
        );
    }

}