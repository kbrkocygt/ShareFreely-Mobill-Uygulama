package com.kubrakocyigit.sharefreely_proje1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class KullaniciKaydi extends AppCompatActivity {
    private Button btnKaydol;
    private TextView lblZatenHesabimVar;
    private EditText txtKullaniciAdiKayit, txtSifreKayit, txtAdSoyadKayit, txtMailKayit;
    private ImageView imViewKullanici;
    private RequestQueue requestQueue; //istek kuyruğu
    private boolean istekGonderildi = false;
    private SharedPreferences preferences;
    private static final String url_kayit = "http://192.168.1.40/ShareFreely/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullanici_kaydi);
        getSupportActionBar().hide();
        tanimla();
        zatenHesabimVarDokun();
        hataMesajiTemizleme();
        //requestQueue olusturulan bir isteği gondermek ıcın kullanılır
        //Volley, bir HTTP kütüphanesidir. Android uygulamalar
        //için daha hızlı ve daha kolay ağ işlemleri yapılmasını saglar
        //Volley ile bir istek kuyruğu oluşturmak için newRequestQueue() metodunu kullanmamız gerekiyor.
        requestQueue = Volley.newRequestQueue(KullaniciKaydi.this);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (!internetBaglantiKontrol()) {
            Snackbar.make(findViewById(R.id.rootkayitEkrani), "İnternet bağlantınızı kontrol ediniz...", Snackbar.LENGTH_LONG).show();
        }
        findViewById(R.id.btnKaydol).setOnClickListener(new View.OnClickListener() {
            @Override
            //Edittexts dolu mu  bos mu kontrolu
            public void onClick(View v) {
                boolean durumadsoyad = TextUtils.isEmpty(txtAdSoyadKayit.getText());
                boolean durummail = TextUtils.isEmpty(txtMailKayit.getText());
                boolean durumsifre = TextUtils.isEmpty(txtSifreKayit.getText());
                boolean durumkullaniciadi = TextUtils.isEmpty(txtKullaniciAdiKayit.getText());


                if (durumadsoyad || durummail || durumsifre || durumkullaniciadi || !txtMailKayit.getText().toString().contains("@")) {

                    if (durumadsoyad)
                        txtAdSoyadKayit.setError("Lütfen ad ve soyadınızı giriniz");
                    if (durumsifre)
                        txtSifreKayit.setError("Lütfen şifrenizi giriniz");
                    if (durumkullaniciadi)
                        txtKullaniciAdiKayit.setError("Lütfen kullanıcı adınızı giriniz");

                    if (durummail)
                        txtMailKayit.setError("Lütfen mail adresinizi giriniz");
                    else if (!txtMailKayit.getText().toString().contains("@"))
                        txtMailKayit.setError("Lütfen geçerli bir mail adresi giriniz");

                } else {

                    //kayıt isteği gönderilecek
                    if (!internetBaglantiKontrol()) {
                        Snackbar.make(findViewById(R.id.rootkayitEkrani), "İnternet bağlantınızı kontrol ediniz...", Snackbar.LENGTH_LONG).show();
                    } else {
                        if (!istekGonderildi) {
                            istekGonderildi = true;
                            StringRequest request = new StringRequest(Request.Method.POST, url_kayit, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(KullaniciKaydi.this, response, Toast.LENGTH_SHORT).show();
                                    try {
                                        JSONObject json = new JSONObject(response);


                                        String durum = json.getString("status");

                                        if (durum.equals("404")) {
                                            Snackbar.make(findViewById(R.id.rootkayitEkrani), "Sunucu ile bağlantı kurulamadı...", Snackbar.LENGTH_LONG).show();
                                            istekGonderildi = false;
                                        } else if (durum.equals("400")) {
                                            Snackbar.make(findViewById(R.id.rootkayitEkrani), "Verilen bilgilerle kayıt yapılamadı. Kullanıcı adı daha önce kullanılmış.", Snackbar.LENGTH_LONG).show();
                                            istekGonderildi = false;
                                        } else if (durum.equals("200")) {
                                            new AlertDialog.Builder(KullaniciKaydi.this)
                                                    .setMessage("Kayıt işlemi başarılı bir şekilde yapıldı. Lütfen mail adresinizi kontrol ediniz.")
                                                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            finish();
                                                        }
                                                    }).show();


                                        }

                                    } catch (JSONException e) {

                                    }


                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(KullaniciKaydi.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                //veri tabanı kaydı
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> degerler = new HashMap<>();
                                    degerler.put("kullaniciAdi", txtKullaniciAdiKayit.getText().toString());
                                    degerler.put("sifre", txtSifreKayit.getText().toString());
                                    degerler.put("adSoyad", txtAdSoyadKayit.getText().toString());
                                    degerler.put("mail", txtMailKayit.getText().toString());
                                    return degerler;
                                }
                            };
                           //istek gonderiyoruz
                            requestQueue.add(request);

                        }
                    }

                }
            }
        });
    }


    public void tanimla() {
        lblZatenHesabimVar = (TextView) findViewById(R.id.lblZatenHesabimVar);
        btnKaydol = (Button) findViewById(R.id.btnKaydol);
        txtAdSoyadKayit = (EditText) findViewById(R.id.txtAdSoyadKayit);
        txtMailKayit = (EditText) findViewById(R.id.txtMailKayit);
        txtKullaniciAdiKayit = (EditText) findViewById(R.id.txtKullaniciAdiKayit);
        txtSifreKayit = (EditText) findViewById(R.id.txtSifreKayit);
    }

    boolean internetBaglantiKontrol() {

        ConnectivityManager baglantiYonetici = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (baglantiYonetici.getActiveNetworkInfo().isAvailable() &&
                baglantiYonetici.getActiveNetworkInfo().isConnected() &&
                baglantiYonetici.getActiveNetworkInfo() != null) {
            return true;
        } else {
            return false;
        }
    }

    public void zatenHesabimVarDokun() {
        lblZatenHesabimVar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)//dokunuldugu zaman
                {
                    ((TextView) v).setTextColor(Color.parseColor("#FFA500"));
                }
                if (event.getAction() == MotionEvent.ACTION_UP)//dokunuldugu zaman
                {
                    ((TextView) v).setTextColor(Color.BLACK);
                }
                return false;
            }
        });
    }

    public void hataMesajiTemizleme() {
        txtMailKayit.setError(null);
        txtSifreKayit.setError(null);
        txtKullaniciAdiKayit.setError(null);
        txtAdSoyadKayit.setError(null);
    }
//kayıt aktıvıty gecıs
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lblZatenHesabimVar:
                startActivity(new Intent(KullaniciKaydi.this, GirisEkrani.class));
                break;
        }
    }


}