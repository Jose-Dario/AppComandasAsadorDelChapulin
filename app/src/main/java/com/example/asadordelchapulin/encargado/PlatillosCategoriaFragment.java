package com.example.asadordelchapulin.encargado;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asadordelchapulin.AlertLoader;
import com.example.asadordelchapulin.R;
import com.example.asadordelchapulin.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlatillosCategoriaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlatillosCategoriaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    public static Categoria categoria;
    private GridView gridView;


    public PlatillosCategoriaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlatillosCategoriaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlatillosCategoriaFragment newInstance(String param1, String param2) {
        PlatillosCategoriaFragment fragment = new PlatillosCategoriaFragment();
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
        view= inflater.inflate(R.layout.fragment_platillos_categoria, container, false);
        gridView=view.findViewById(R.id.contendorPlatillos);
        AdapterPlatillos adapterPlatillos=new AdapterPlatillos(getContext(),categoria.getPlatillos());
        gridView.setAdapter(adapterPlatillos);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    EditarPlatilloFragment.platillo=adapterPlatillos.getPlatillo(i);
                    Navigation.findNavController(view).navigate(R.id.nav_edit_platillo);
            }
        });

        ((TextView)view.findViewById(R.id.nameCategoria)).setText(categoria.getNombre());
        ((Button)view.findViewById(R.id.btnAddPlatillo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPlatilloFragment.categoria=categoria;
                Navigation.findNavController(view).navigate(R.id.nav_add_platillo);
            }
        });
        ((Button)view.findViewById(R.id.btnEditCategoria)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditarCategoriaFragment.categoria=categoria;
                Navigation.findNavController(view).navigate(R.id.nav_edit_categoria);
            }
        });

        ((Button)view.findViewById(R.id.btnEliminarCategoria)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
                alert.setMessage("¿Está seguro de eliminar la categoría?")
                        .setCancelable(true)
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AlertLoader alertLoader=new AlertLoader(getActivity(),view);
                                Utils.removeImgCategoria(categoria.getNombre(),getContext());
                                categoria.getDocumentReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            alertLoader.dimiss("Categoría eliminada");
                                        }
                                        else {
                                            alertLoader.showError("Error al eliminar la categoría");
                                        }
                                    }
                                });
                            }
                        });
                AlertDialog titulo=alert.create();
                titulo.setTitle("Eliminar categoría");
                titulo.show();
            }
        });
        return view;
    }

}