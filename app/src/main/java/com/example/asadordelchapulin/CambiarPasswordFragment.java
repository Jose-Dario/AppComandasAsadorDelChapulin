package com.example.asadordelchapulin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CambiarPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CambiarPasswordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private EditText contraseniaActual,contraseniaNueva,contraseniaConfirmada;
    private ImageButton verNueva,verConfirmada;

    public static String password;

    private boolean isVisible1=false,isVisible2=false;

    public CambiarPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CambiarPasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CambiarPasswordFragment newInstance(String param1, String param2) {
        CambiarPasswordFragment fragment = new CambiarPasswordFragment();
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
        view=inflater.inflate(R.layout.fragment_cambiar_password, container, false);
        contraseniaActual=view.findViewById(R.id.etContraseniaActual);
        contraseniaNueva=view.findViewById(R.id.etContraseniaNueva);
        contraseniaConfirmada=view.findViewById(R.id.etConfirmacionContrasenia);
        verNueva=view.findViewById(R.id.btnVerContraseniaNueva);
        verConfirmada=view.findViewById(R.id.btnVerContraseniaConfirmada);

        verNueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVisible1=!isVisible1;
                if(isVisible1){
                    verNueva.setImageResource(R.drawable.baseline_visibility_24);
                    contraseniaNueva.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else {
                    verNueva.setImageResource(R.drawable.baseline_visibility_off_24);
                    contraseniaNueva.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        verConfirmada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVisible2=!isVisible2;
                if(isVisible2){
                    verConfirmada.setImageResource(R.drawable.baseline_visibility_24);
                    contraseniaConfirmada.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else {
                    verConfirmada.setImageResource(R.drawable.baseline_visibility_off_24);
                    contraseniaConfirmada.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        ((Button)view.findViewById(R.id.btnActualizarContrasenia)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contraseniaActual.getText().toString().equals("") || contraseniaNueva.getText().toString().equals("") || contraseniaConfirmada.getText().toString().equals("")){
                    Toast.makeText(getContext(),"Ingrese todos los campos solicitados",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(contraseniaActual.getText().toString().equalsIgnoreCase(password)){
                        AlertLoader alertLoader=new AlertLoader(getActivity(),view);
                        PerfilFragment.firebaseAuth.getCurrentUser().updatePassword(contraseniaNueva.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    alertLoader.dimiss("Contraseña actualizada");
                                }
                                else {
                                    alertLoader.showError("Error al actualizar la contraseña");
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(getContext(),"La contraseña actual no coincide", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }
}