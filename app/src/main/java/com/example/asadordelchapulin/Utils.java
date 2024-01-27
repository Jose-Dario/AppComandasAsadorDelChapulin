package com.example.asadordelchapulin;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Utils {
    static StorageReference storageReference= FirebaseStorage.getInstance().getReference();
    static FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private static ArrayList<String> medioPago=new ArrayList<>();

    public static String ipPrinter;
    static File directorioImagenes = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    private static final String[] UNIDADES = {
            "", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve"
    };

    private static final String[] DECENAS = {
            "", "", "veinte", "treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa"
    };

    private static final String[] DIEZ_A_DIECINUEVE = {
            "diez", "once", "doce", "trece", "catorce", "quince", "dieciséis", "diecisiete", "dieciocho", "diecinueve"
    };

    private static final String[] CENTENAS = {
            "", "ciento", "doscientos", "trescientos", "cuatrocientos", "quinientos", "seiscientos", "setecientos", "ochocientos", "novecientos"
    };

    public static void downloadImageProfile(Usuario usuario,ImageView imageView){
        String path=usuario.getProfileReference()+".jpg";
        try {
            // Crea un archivo temporal para almacenar la imagen
            File localFile = File.createTempFile(usuario.getNombre(),"jpg");

            // Descarga la imagen desde Firebase Cloud Storage al archivo temporal
            storageReference.child("usuarios").child(path).getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Carga el archivo temporal en un Bitmap
                            imageView.setImageBitmap(BitmapFactory.decodeFile(localFile.getAbsolutePath()));
                            usuario.setImg(true);
                            usuario.setImgProfile(BitmapFactory.decodeFile(localFile.getAbsolutePath()));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            usuario.setImg(true);
                            // Manejar errores en caso de que la descarga falle
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadImageProfile(ImageView imageView){
        String path=firebaseAuth.getCurrentUser().getUid()+".jpg";
        try {
            // Crea un archivo temporal para almacenar la imagen
            File localFile = File.createTempFile(firebaseAuth.getCurrentUser().getUid(),"jpg");

            // Descarga la imagen desde Firebase Cloud Storage al archivo temporal
            storageReference.child("usuarios").child(path).getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Carga el archivo temporal en un Bitmap
                            if(imageView!=null){
                                imageView.setImageBitmap(BitmapFactory.decodeFile(localFile.getAbsolutePath()));
                            }
                            PerfilFragment.usuario.setImgProfile(BitmapFactory.decodeFile(localFile.getAbsolutePath()));
                            PerfilFragment.usuario.setImg(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            PerfilFragment.usuario.setImg(true);
                            // Manejar errores en caso de que la descarga falle
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void uploadImageProfile(Uri imageUri, Context context) {
        if (imageUri != null) {
            StorageReference photoRef = storageReference.child("usuarios").child(firebaseAuth.getCurrentUser().getUid() + ".jpg");

            photoRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Foto subida exitosamente
                            //getImageProfile(context);
                            Toast.makeText(context, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error al subir la foto
                            Toast.makeText(context, "Se produjo un error al subir la imagen", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public static void uploadImageCategoria(String nombre,Uri imageUri,Context context){
        if (imageUri != null) {
            StorageReference photoRef = storageReference.child("categorias").child(nombre + ".jpg");

            photoRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Foto subida exitosamente
                            //getImageProfile(context);
                            Toast.makeText(context, "Se subió la imagen correctamente", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error al subir la foto
                            Toast.makeText(context, "Se produjo un error al subir la imagen", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public static void uploadImagePlatillo(String nombre,Uri imageUri,Context context){
        if (imageUri != null) {
            StorageReference photoRef = storageReference.child("platillos").child(nombre + ".jpg");

            photoRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Foto subida exitosamente
                            //getImageProfile(context);
                            Toast.makeText(context, "Se subió la imagen correctamente", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error al subir la foto
                            Toast.makeText(context, "Se produjo un error al subir la imagen", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public static void removeImgCategoria(String nombre, Context context){
        StorageReference photoRef = storageReference.child("categorias").child(nombre + ".jpg");
        photoRef.delete();
    }

    public static void removeImgPlatillo(String nombre, Context context){
        StorageReference photoRef = storageReference.child("platillos").child(nombre + ".jpg");
        photoRef.delete();
    }

    public static Bitmap convertUriToBitmap(Uri uri, Context context) {
        try {
            // Utiliza un ContentResolver para abrir la imagen a partir de su URI
            ContentResolver contentResolver = context.getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);

            // Convierte el flujo de entrada en un objeto Bitmap
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String centrar(String string){
        String nuevo="";
        if(string.length()<24){
            int espacios = 24-string.length();
            for (int i=0;i<espacios/2;i++){
                nuevo+=" ";
            }
            nuevo+=string;
        }
        else {
            nuevo=string;
        }
        return nuevo;
    }

    public static String imprimirProducto(String producto,int cantidad, double precio){
        String cadena="";
        if(cantidad<10){
            cadena+="   "+cantidad;
        } else if (cantidad<100) {
            cadena+="  "+cantidad;
        }
        else if(cantidad<1000){
            cadena+=" "+cantidad;
        }
        cadena+="   ";

        if(producto.length()<28){
            cadena+=producto;
            for (int i=0;i<28-producto.length();i++){
                cadena+=" ";
            }
        }
        else {
            cadena+=producto.substring(0,25)+"...";
        }

        double subtotal=precio* cantidad;
        //total+=subtotal;
        String numeroFormateado = String.format("%.2f", subtotal);
        cadena+="$";
        for (int i=0;i<10-numeroFormateado.length();i++){
            cadena+=" ";
        }
        cadena+=numeroFormateado+"\n";

        return cadena;
    }

    public static String imprimirDescripcion(String descripcion){
        String aux="";
        if(descripcion.length()<35){
            return "        "+descripcion;
        }
        else{
            String [] palabras=descripcion.split(" ");
            String subcadena="       ";
            for (String palabra:palabras){
                if(subcadena.length()+palabra.length()>40){
                    aux+=subcadena+"\n";
                    subcadena="       ";
                    subcadena+=palabra+" ";
                }
                else{
                    subcadena+=palabra+" ";
                }
            }
            if(!descripcion.contains(subcadena)){
                aux+=subcadena;
            };
        }
        return aux;
    }



    public static String imprimirTotal(double total){
            String string="";

            String cadena="Total $";
        String numeroFormateado = String.format("%.2f", total);
        for (int i=0;i<10-numeroFormateado.length();i++){
            cadena+=" ";
        }
        cadena+=numeroFormateado;
            for (int i=0;i<46-cadena.length();i++){
                string+=" ";
            }
            string+=cadena;
            return string;
    }



    public static String imprimirFecha(Date date){

        // Define el formato que deseas
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM/yyyy hh:mm a", new Locale("es", "ES"));

        // Convierte la fecha al formato deseado
        return sdf.format(date);
    }

    public static String convertirNumeroEnPalabras(double numero) {
        int parteEntera = (int) numero;
        int parteDecimal = (int) ((numero - parteEntera) * 100);

        String palabrasParteEntera = convertirParteEnteraEnPalabras(parteEntera);

        String resultado = palabrasParteEntera + " pesos";
        return resultado.trim();
    }

    private static String convertirParteEnteraEnPalabras(int parteEntera) {
        if (parteEntera == 0) {
            return "cero";
        }

        String palabras = "";

        // Procesar millones, miles y unidades
        int millones = parteEntera / 1000000;
        if (millones > 0) {
            palabras += convertirCentenasEnPalabras(millones) + " millones ";
            parteEntera %= 1000000;
        }

        int miles = parteEntera / 1000;
        if (miles > 0) {
            if(miles==1){
                palabras+=" mil ";
            }
            else{
                palabras += convertirCentenasEnPalabras(miles) + " mil ";
            }
            parteEntera %= 1000;
        }

        if (parteEntera > 0) {
            palabras += convertirCentenasEnPalabras(parteEntera);
        }

        return palabras.trim();
    }

    private static String convertirCentenasEnPalabras(int numero) {
        String palabras = "";
        // Procesar centenas
        int centenas = numero / 100;
        if (centenas > 0) {
            palabras += CENTENAS[centenas] + " ";
            numero %= 100;
        }
        // Procesar decenas
        int decenas = numero / 10;
        if (decenas >= 2) {
            palabras += DECENAS[decenas] + " y ";
            numero %= 10;
        } else if (decenas == 1) {
            palabras += DIEZ_A_DIECINUEVE[numero % 10] + " ";
            numero = 0;
        }
        // Procesar unidades
        if (numero > 0) {
            palabras += UNIDADES[numero] + " ";
        }

        return palabras.trim();
    }

    public static String printFecha(Date date){
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        return formato.format(date);
    }

    public static String getTotalCenter(String string){
        if(string.length()<25){
            return string;
        }
        else{
            String general="";
            String aux="";
            String [] palabras=string.split(" ");
            for (String palabra:palabras){
                aux+=palabra;
                if(aux.length()>24){
                    aux.replace(palabra,"");
                    general+=aux+"\n";
                    aux+="[C]"+palabra;
                }
            }
            return general;
        }
    }

    public static ArrayList<String> getMediosPago(){
        if(medioPago.size()==0){
            medioPago.add("Efectivo");
            medioPago.add("Transferencia");
        }
        return medioPago;
    }

    public static Uri getUri(String nombreArchivo){
        String rutaCompleta = directorioImagenes.getAbsolutePath() + File.separator + nombreArchivo.toUpperCase()+".jpg";
        File archivoImagen = new File(rutaCompleta);
        if (archivoImagen.exists()) {
            return Uri.fromFile(archivoImagen);
        } else {
            return null;
        }
    }

    public static void cargarImagen(Context context,Uri uri,ImageView imageView){
        if(uri!=null)
        Glide.with(context).load(uri).into(imageView);
    }
}
