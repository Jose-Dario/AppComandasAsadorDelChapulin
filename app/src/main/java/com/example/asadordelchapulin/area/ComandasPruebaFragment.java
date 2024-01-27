package com.example.asadordelchapulin.area;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.asadordelchapulin.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComandasPruebaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComandasPruebaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    private Switch lectura;

    public static boolean lecturaAuto=false;

    public ComandasPruebaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComandasPruebaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComandasPruebaFragment newInstance(String param1, String param2) {
        ComandasPruebaFragment fragment = new ComandasPruebaFragment();
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
        view= inflater.inflate(R.layout.fragment_comandas_prueba, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewPrueba);

        // Puedes personalizar el número de columnas según tus necesidades
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));


        // Establece el adaptador en el RecyclerView
        recyclerView.setAdapter(((MainArea)getActivity()).getAdapterCocinaPrueba());

        lectura=view.findViewById(R.id.switchLecAuto);
        lectura.setChecked(lecturaAuto);

       lectura.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    lecturaAuto=true;
                }
                else {
                    lecturaAuto=false;
                }
            }
        });


        return view;
    }
}