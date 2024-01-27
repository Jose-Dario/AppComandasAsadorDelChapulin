package com.example.asadordelchapulin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.EditText;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class ImprimirTicket extends AsyncTask<Void, Void, String> {
    private OnTaskCompleted listener;
    private String texto;

    private Bitmap logo;

    public ImprimirTicket(OnTaskCompleted listener,String texto) {
        this.listener = listener;
        this.texto=texto;

    }

    public  ImprimirTicket (OnTaskCompleted listener, Bitmap logo)
    {
        this.listener=listener;
        this.logo=logo;
    }

    @Override
    protected String doInBackground(Void... voids) {
       // EscPosPrinter posPrinter=new EscPosPrinter();


        return "correcto";
    }

     //   try {
//            Socket socket = new Socket("192.168.0.100", 9100);
//               OutputStream outputStream= socket.getOutputStream();
//            // Inicializar la impresora
//            outputStream.write(new byte[]{0x1B, 0x40});
//
//            // Configurar el codepage a WCP1252
//            outputStream.write(new byte[]{0x1B, 0x74, 0x10});
//
//            // Seleccionar el modo de impresión de imagen monocromática
//            outputStream.write(new byte[]{0x1B, 0x55, 0x00});
//
//            // Establecer el ancho de la imagen (ajustar según sea necesario)
//            int widthInBytes = (logo.getWidth() + 7) / 8;
//            outputStream.write(new byte[]{0x1D, 0x76, 0x30, (byte) widthInBytes, 0x00});

            // Enviar los datos de la imagen
//            outputStream.write(imgData);
//
//            // Avanzar una línea para prevenir la superposición con el próximo texto
//            outputStream.write(new byte[]{0x0A});
//
//            // Cortar el papel (ajustar según sea necesario)
//            outputStream.write(new byte[]{0x1D, 0x56, 0x00});
//
//            // Limpiar el búfer y cerrar la conexión
//            outputStream.flush();
//            outputStream.close();
//            Socket socket = new Socket("192.168.0.100", 9100);
//            OutputStream outputStream= socket.getOutputStream();
//            // Inicia la impresión
//            outputStream.write(27); // ASCII para el carácter ESC
//            outputStream.write('@');
//
//            //imprimir adecuadamente los caracteres
//
//            // Especificar el conjunto de caracteres Windows-1252
//            //byte[] bytes = texto.getBytes(Charset.forName("Windows-1252"));
//
//
//            // Ahora puedes usar estos bytes para imprimir o guardar el texto en Windows-1252
//            //String nuevoTexto = new String(bytes, Charset.forName("Windows-1252"));
//
//            byte[] imageData = convertBitmapToByteArray(logo);
//
//
//// Envía los datos de la imagen a la impresora
//            try {
//                outputStream.write(imageData);
//                outputStream.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//// Función para convertir un Bitmap en una matriz de bytes en formato ESC/POS
//
//            //imprime
//            //outputStream.write();
//
//
//            // Corta el papel
//            outputStream.write(29); // ASCII para el carácter GS
//            outputStream.write('V');
//            outputStream.write(66);
//            outputStream.write(0);
//
//            // Cierra la conexión
//            outputStream.close();
//            socket.close();
           // return "correcto";
        //}
//
//        catch (IOException e) {
//            return e.toString();
//        }
    //}

//    private byte[] convertBitmapToBytes(Bitmap bitmap) {
//        int height = bitmap.getHeight();
//        int width = bitmap.getWidth();
//
//        // Obtener los píxeles de la imagen
//        int[] pixels = new int[width * height];
//        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
//
//        // Convertir los píxeles a escala de grises y empaquetarlos en bytes
//        byte[] imageData = new byte[width * height / 8];
//        int index = 0;
//
//        for (int y = 0; y < height; y += 8) {
//            for (int x = 0; x < width; x++) {
//                byte pixelByte = 0;
//                for (int k = 0; k < 8 && y + k < height; k++) {
//                    int pixelColor = pixels[(y + k) * width + x];
//                    if (Color.red(pixelColor) < 128) {
//                        pixelByte |= (byte) (0x80 >> k);
//                    }
//                }
//                imageData[index++] = pixelByte;
//            }
//        }
//
//        return imageData;
//    }







    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // Una vez que la tarea asincrónica haya terminado, llamas al método de la interfaz
        listener.onTaskCompleted(result);
    }
}


