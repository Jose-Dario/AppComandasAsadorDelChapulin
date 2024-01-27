package com.example.asadordelchapulin;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.navigation.Navigation;


public class AlertLoader {
    private AlertDialog.Builder alertDialog;
    private ProgressBar progressBar;
    private ImageButton imageButton;
    private TextView textView;
    private Button button;
    private AlertDialog alert;

    private View view;

    public AlertLoader(Activity activity, View view){
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.card_loader, null);
        progressBar=dialogView.findViewById(R.id.progreso);
        imageButton=dialogView.findViewById(R.id.imgListo);
        textView=dialogView.findViewById(R.id.tvExito);
        button=dialogView.findViewById(R.id.btnCerrar);
        // Crea el AlertDialog
        alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setView(dialogView);
        alert= alertDialog.create();
        alert.show();
        alert.setCancelable(false);
        this.view=view;
    }

    public AlertLoader(Activity activity){
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.card_loader, null);
        progressBar=dialogView.findViewById(R.id.progreso);
        imageButton=dialogView.findViewById(R.id.imgListo);
        textView=dialogView.findViewById(R.id.tvExito);
        button=dialogView.findViewById(R.id.btnCerrar);
        // Crea el AlertDialog
        alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setView(dialogView);
        alert= alertDialog.create();
        alert.show();
        alert.setCancelable(false);
    }

    public void finalizarLoader(){
        alert.dismiss();
    }

    public void showError(String error){
        progressBar.setVisibility(View.INVISIBLE);
        imageButton.setImageResource(R.drawable.baseline_error_24);
        imageButton.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
        textView.setText(error);
        textView.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
    }

    public void dimiss(String text){
        progressBar.setVisibility(View.INVISIBLE);
        imageButton.setImageResource(R.drawable.baseline_task_alt_24);
        imageButton.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
        textView.setText(text);
        textView.setVisibility(View.VISIBLE);
        Navigation.findNavController(view).popBackStack();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
    }

    public void minimizar(String text){
        progressBar.setVisibility(View.INVISIBLE);
        imageButton.setImageResource(R.drawable.baseline_task_alt_24);
        imageButton.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
        textView.setText(text);
        textView.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
    }


}
