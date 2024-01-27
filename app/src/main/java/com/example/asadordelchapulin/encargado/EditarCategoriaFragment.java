package com.example.asadordelchapulin.encargado;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asadordelchapulin.R;
import com.example.asadordelchapulin.AlertLoader;
import com.example.asadordelchapulin.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditarCategoriaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarCategoriaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    private Spinner editAreaCategoria;
    private EditText editNombreCategoria;
    private ArrayAdapter arrayAdapter;

    public static Categoria categoria;
    private static final int GALLERY_REQUEST_CODE = 123;
    private Uri selectedImageUri;

    private ImageView imgCategoria;


    public EditarCategoriaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditarCategoriaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditarCategoriaFragment newInstance(String param1, String param2) {
        EditarCategoriaFragment fragment = new EditarCategoriaFragment();
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
        view= inflater.inflate(R.layout.fragment_editar_categoria, container, false);
        editAreaCategoria=view.findViewById(R.id.editAreaCategoria);
        arrayAdapter= new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, MainEncargado.getAreas());
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editAreaCategoria.setAdapter(arrayAdapter);
        editNombreCategoria=view.findViewById(R.id.editNombreCategoria);
        editNombreCategoria.setHint(categoria.getNombre());
        imgCategoria=view.findViewById(R.id.imgCategoriaEdit);
        Utils.cargarImagen(getContext(),categoria.getUri(),imgCategoria);
        ((TextView)view.findViewById(R.id.editCategoriaTitle)).setText(categoria.getNombre());

        editAreaCategoria.setSelection(MainEncargado.listAreas.indexOf(categoria.getArea()),true);


        ((Button)view.findViewById(R.id.btnActualizarCategoria)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editNombreCategoria.getText().toString().equals("") || !editAreaCategoria.getSelectedItem().toString().equals(categoria.getArea())){
                    AlertLoader alertLoader=new AlertLoader(getActivity(),view);
                    if (!editNombreCategoria.getText().toString().equals("")){
                        categoria.setNombre(editNombreCategoria.getText().toString());
                        categoria.getDocumentReference().update("nombre",editNombreCategoria.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    if(!editAreaCategoria.getSelectedItem().toString().equals(categoria.getArea())){
                                        categoria.setArea(editAreaCategoria.getSelectedItem().toString());
                                        categoria.getDocumentReference().update("area",editAreaCategoria.getSelectedItem().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    alertLoader.dimiss("Información actualizada");
                                                }
                                                else {
                                                    alertLoader.showError("Error al modificar el area de la categoría");
                                                }
                                            }
                                        });
                                    }
                                    else {
                                        alertLoader.dimiss("Información actualizada");
                                    }
                                }
                                else {
                                    alertLoader.showError("Error al actualizar el nombre de la categoría");
                                }
                            }
                        });
                        //editNombreCategoria.setHint(editNombreCategoria.getText().toString());
                        //editNombreCategoria.setText("");
                        //0editNombreCategoria.clearFocus();
                    }
                    else if(!editAreaCategoria.getSelectedItem().toString().equals(categoria.getArea())){
                        categoria.setArea(editAreaCategoria.getSelectedItem().toString());
                        categoria.getDocumentReference().update("area",editAreaCategoria.getSelectedItem().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    alertLoader.dimiss("Información actualizada");
                                }
                                else {
                                    alertLoader.showError("Error al actualizar el área de la categoría");
                                }
                            }
                        });
                    }
                }
                else {
                    Toast.makeText(getContext(),"No se ha realizado ningún cambio",Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).popBackStack();
                }
            }
        });
        return view;
    }
}