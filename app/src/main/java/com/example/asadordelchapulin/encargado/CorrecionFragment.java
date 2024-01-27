package com.example.asadordelchapulin.encargado;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.asadordelchapulin.R;
import com.example.asadordelchapulin.AlertLoader;
import com.example.asadordelchapulin.Mesa;
import com.example.asadordelchapulin.MesasFragment;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CorrecionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CorrecionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    private LinearLayout linearLayout;

    public CorrecionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CorrecionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CorrecionFragment newInstance(String param1, String param2) {
        CorrecionFragment fragment = new CorrecionFragment();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_correcion, container, false);
        linearLayout=view.findViewById(R.id.linearClientes);

        for(Mesa mesa:MesasFragment.adapterMesas.getMesas()){
            if(mesa.getEstado().equals("ocupado")){
                for(HashMap map:mesa.getClientes()){
                    View aux=inflater.inflate(R.layout.card_cliente,null);
                    ((TextView)aux.findViewById(R.id.tvNombreCliente)).setText("Mesa "+mesa.getId()+" : "+map.get("nombre").toString());
                    aux.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertLoader alertLoader=new AlertLoader(getActivity());
                            CorregirComandasFragment.alertLoader=alertLoader;
                            CorregirComandasFragment.mesa=mesa.getId();
                            CorregirComandasFragment.cliente=map;
                            Navigation.findNavController(view).navigate(R.id.nav_show_comandas);
                        }
                    });
                    linearLayout.addView(aux);
                }
            }
        }

        return view;
    }
}