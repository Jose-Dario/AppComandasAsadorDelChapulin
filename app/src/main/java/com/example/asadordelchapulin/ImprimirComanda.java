package com.example.asadordelchapulin;

import android.os.AsyncTask;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

public class ImprimirComanda extends AsyncTask<Void, Void, String> {
    private OnTaskCompleted listener;
    private String texto;

    public ImprimirComanda(OnTaskCompleted listener,String texto) {
        this.listener = listener;
        this.texto=texto;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            Socket socket = new Socket("192.168.1.254", 9100);
            OutputStream outputStream= socket.getOutputStream();
            // Inicia la impresión
            outputStream.write(27); // ASCII para el carácter ESC
            outputStream.write('@');

            byte[] fontSizeCommand = new byte[]{0x1D, 0x21, 0x11};
            byte[] bytes = texto.getBytes(Charset.forName("Windows-1252"));

            try {
                outputStream.write(fontSizeCommand);

                // Ahora puedes usar estos bytes para imprimir o guardar el texto en Windows-1252
                //String nuevoTexto = new String(bytes, Charset.forName("Windows-1252"));

                //imprime
                outputStream.write(bytes);
                // Aquí envía el resto de tu contenido de impresión
            } catch (IOException e) {
                e.printStackTrace();
            }

            //imprimir adecuadamente los caracteres

            // Especificar el conjunto de caracteres Windows-1252



            // Corta el papel
            outputStream.write(29); // ASCII para el carácter GS
            outputStream.write('V');
            outputStream.write(66);
            outputStream.write(0);

            // Cierra la conexión
            outputStream.close();
            socket.close();
            return "correcto";
            //alertLoader.minimizar("Ticket Impreso");
        }

        catch (IOException e) {
            return e.toString();
            // alertLoader.showError(e.getMessage());
            //return e.toString();
            //alertLoader.showError(e.toString());
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Una vez que la tarea asincrónica haya terminado, llamas al método de la interfaz
        listener.onTaskCompleted(result);
    }
}
