package com.example.asadordelchapulin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddCuentaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddCuentaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText etNombreCuenta;

    private View view;

    public AddCuentaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddCuentaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddCuentaFragment newInstance(String param1, String param2) {
        AddCuentaFragment fragment = new AddCuentaFragment();
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
        view= inflater.inflate(R.layout.fragment_add_cuenta, container, false);
        ((TextView)view.findViewById(R.id.tituloMesaAddCuenta)).setText("Mesa "+MesaDetalleFragment.mesa.getId());
        etNombreCuenta=view.findViewById(R.id.etNombreCuenta);
        ((Button)view.findViewById(R.id.btnAddCuentaMesa)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etNombreCuenta.getText().toString().equals("")){
                    AlertLoader alertLoader=new AlertLoader(getActivity(),view);
                    HashMap aux=new HashMap();
                    aux.put("nombre",etNombreCuenta.getText().toString());
                    aux.put("consumo",0);
                    aux.put("folio","");
                    MesaDetalleFragment.mesa.getClientes().add(aux);
                    MesaDetalleFragment.mesa.getDocumentReference().update("clientes",MesaDetalleFragment.mesa.getClientes()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                alertLoader.finalizarLoader();
                                Navigation.findNavController(view).popBackStack();
                                Toast.makeText(getContext(),"Cuenta añadida",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getContext(),"Ingrese el nombre de la cuenta", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}