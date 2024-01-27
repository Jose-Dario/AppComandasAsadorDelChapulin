package com.example.asadordelchapulin.area;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Switch;
import com.example.asadordelchapulin.AdapterComandasCocina;
import com.example.asadordelchapulin.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComandasCocinaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComandasCocinaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GridView gridView;

    private View view;
    public static AdapterComandasCocina adapterComandasCocina;

    private Switch lectura;

    public static boolean lecturaAuto=false;

    public ComandasCocinaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComandasCocinaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComandasCocinaFragment newInstance(String param1, String param2) {
        ComandasCocinaFragment fragment = new ComandasCocinaFragment();
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
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                getActivity().moveTaskToBack(true);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_comandas_cocina, container, false);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridView=view.findViewById(R.id.contenedorComandasCocina);
        gridView.setAdapter(adapterComandasCocina);
        lectura=view.findViewById(R.id.switchLecturaAutomatica);
        lectura.setChecked(lecturaAuto);

        ((Switch)view.findViewById(R.id.switchLecturaAutomatica)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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