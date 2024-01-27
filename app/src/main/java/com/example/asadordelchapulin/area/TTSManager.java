package com.example.asadordelchapulin.area;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.List;
import java.util.Locale;

public class TTSManager{
    private TextToSpeech mTts=null;
    private boolean isLoaded=false;

    public TTSManager(){}
    protected TTSManager(Parcel in) {
        isLoaded = in.readByte() != 0;
    }



    public void init(Context context){
        try{
            mTts=new TextToSpeech(context,onInitListener);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public TextToSpeech getTextToSpeech(){
        return mTts;
    }

    private TextToSpeech.OnInitListener onInitListener=new TextToSpeech.OnInitListener(){
        public void onInit(int status){
            Locale spanish=new Locale("es","MX");
            if(status==TextToSpeech.SUCCESS){
                List<TextToSpeech.EngineInfo> engines = mTts.getEngines();

                // Seleccionar el motor deseado (por ejemplo, el primer motor en la lista)
                if (!engines.isEmpty()) {
                    TextToSpeech.EngineInfo engineInfo = engines.get(0);
                    mTts.setEngineByPackageName(engineInfo.name);
                }
                int result=mTts.setLanguage(spanish);
                isLoaded=true;
                if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                    Log.e("error","Este lenguaje no esta permitido");
                }
            }
            else {
                Log.e("error","Fallo al Inicializar");
            }
        }
    };

    public void shutDown(){
        mTts.shutdown();
    }

    public void addQueue(String text){
        if(isLoaded)
            mTts.speak(text,TextToSpeech.QUEUE_ADD,null);
        else{
            Log.e("error","TTS Not Initialized");
        }
    }

    public void initQueue(String text){
        if(isLoaded){
            mTts.speak(text,TextToSpeech.QUEUE_FLUSH,null);
        }
        else {
            Log.e("error","TTS Not Initialized");
        }

    }


}
