package com.kubrakocyigit.sharefreely_proje1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class AcilisLogo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acilis_logo);
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(1000); //istediğiniz değerleri yazabilirsiniz (3000 = 3 saniye)
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    //buraya 3 saniye bittikten sonra ne gerçekleşmesini istiyorsanız onu yazın
                    //örnek olarak diğer activity'e geçmesini söyledim
                    Intent intent = new Intent(AcilisLogo.this, GirisEkrani.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        timerThread.start();
    }
}