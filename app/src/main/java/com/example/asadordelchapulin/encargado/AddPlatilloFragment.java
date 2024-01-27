package com.example.asadordelchapulin.encargado;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.asadordelchapulin.R;
import com.example.asadordelchapulin.AlertLoader;
import com.example.asadordelchapulin.Utils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPlatilloFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPlatilloFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    private String nombrePlatillo, descripcionPlatillo;
    private double precioPlatillo;

    private ImageView imgPlatillo;

    public static Categoria categoria;

    private EditText nombre, precio, descripcion;

    private static final int GALLERY_REQUEST_CODE = 123;
    private Uri selectedImageUri;

    public static ArrayList<String> tamanios;

    public AddPlatilloFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddPlatilloFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPlatilloFragment newInstance(String param1, String param2) {
        AddPlatilloFragment fragment = new AddPlatilloFragment();
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
        view=inflater.inflate(R.layout.fragment_add_platillo, container, false);
nombre=view.findViewById(R.id.etNombrePlatillo);
precio=view.findViewById(R.id.etPrecioPlatillo);
descripcion=view.findViewById(R.id.etDescripcionPlatillo);
imgPlatillo=view.findViewById(R.id.imgPortadaPlatillo);


        ((ImageButton)view.findViewById(R.id.btnSelectImgPlatillo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

        ((Button)view.findViewById(R.id.btnaddPlatilloCategoria)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nombre.getText().toString().equals("") && !precio.getText().toString().equals("")){
                    AlertLoader alertLoader=new AlertLoader(getActivity(),view);
                    nombrePlatillo=nombre.getText().toString();
                    precioPlatillo=Double.parseDouble(precio.getText().toString());
                    if(selectedImageUri!=null){
                        Utils.uploadImagePlatillo(nombrePlatillo,selectedImageUri,getContext());
                    }
                    categoria.addProducto(new Platillo(nombrePlatillo, descripcion.getText().toString(),precioPlatillo,true, categoria.getArea()),alertLoader);
                    Toast.makeText(getContext(),"Platillo agregado correctamente",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Campos vacíos", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgPlatillo.setImageURI(selectedImageUri);
        }
    }
}