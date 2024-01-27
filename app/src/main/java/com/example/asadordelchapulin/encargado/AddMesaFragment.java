package com.example.asadordelchapulin.encargado;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asadordelchapulin.R;
import com.example.asadordelchapulin.AlertLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddMesaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddMesaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private EditText id,cupo;

    public AddMesaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddMesaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddMesaFragment newInstance(String param1, String param2) {
        AddMesaFragment fragment = new AddMesaFragment();
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
        view= inflater.inflate(R.layout.fragment_add_mesa, container, false);
        id=view.findViewById(R.id.etIdMesa);
        cupo=view.findViewById(R.id.etIdCupo);

        ((Button)view.findViewById(R.id.btnAddMesa)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!id.getText().toString().equals("") && !cupo.getText().toString().equals("")){
                    AlertLoader alertLoader=new AlertLoader(getActivity(),view);
                    Map mesa=new HashMap();
                    mesa.put("id",id.getText().toString());
                    mesa.put("idDoc",Integer.parseInt(id.getText().toString()));
                    mesa.put("cupo",cupo.getText().toString());
                    mesa.put("numAdultos",0);
                    mesa.put("numNinos",0);
                    mesa.put("clientes",new ArrayList<>());
                    mesa.put("mesero","");
                    mesa.put("estado","disponible");
                    FirebaseFirestore.getInstance().collection("Mesas").add(mesa).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                alertLoader.dimiss("Mesa agregada correctamente");
                            }
                            else {
                                alertLoader.showError("Ocurrio un error al registrar la mesa");
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getContext(),"Rellene los campos solicitados",Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }
}