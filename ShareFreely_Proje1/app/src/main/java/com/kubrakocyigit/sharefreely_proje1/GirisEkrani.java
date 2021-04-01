package com.kubrakocyigit.sharefreely_proje1;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Context;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

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

public class

GirisEkrani extends AppCompatActivity {
    private TextView lblSifreUnuttum, lblKayitOl;
    private CheckBox cbBeniHatirla;
    private Button btnGiris;
    private EditText txtSifre, txtKullaniciAdi;
    private RequestQueue requestQueue;
    private static final String url_login = "http://192.168.1.40/ShareFreely/login.php";
    private SharedPreferences preferences;
    private boolean istekGonderildi = false;
    private ObjectAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giris_main);
        getSupportActionBar().hide();
        tanimla();
        yeniHesapOlusturDokun();
        sifremiUnuttumDokun();
        hataMesajiTemizleme();
        //requestQueue olusturulan bir isteği gondermek ıcın kullanılır
        //Volley, bir HTTP kütüphanesidir. Android uygulamalar
        //için daha hızlı ve daha kolay ağ işlemleri yapılmasını saglar
        //Volley ile bir istek kuyruğu oluşturmak için newRequestQueue() metodunu kullanmamız gerekiyor.
        requestQueue = Volley.newRequestQueue(GirisEkrani.this);
        //Key-value mantığı ile çalışan Shared Preferences veri tutar
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        findViewById(R.id.lblSifreUnuttum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GirisEkrani.this,SifremiUnuttumActivity.class));
            }
        });

        //beni hatırla kayıtlıysa Ana ekrana geçiş
        if (preferences.getBoolean("benihatirla", false)) {
            Intent intent = new Intent(GirisEkrani.this, ShareFreely.class);
            intent.putExtra("animasyon", true);
            startActivity(intent);
            GirisEkrani.this.finish();

        }

        if (!internetBaglantiKontrol()) {
            //Toast dan farkı Snackbar'a buton ekleyebilir veya
            // istediğimiz an sürükleyip ekrandan kaybedebiliriz.
            Snackbar.make(findViewById(R.id.rootgirisEkrani), "İnternet bağlantınızı kontrol ediniz...", Snackbar.LENGTH_LONG).show();
        }


    }

    private boolean internetBaglantiKontrol() {
        //internetimiz acıksa true kapalıysa false dondur
        ConnectivityManager baglantiYonetici = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (baglantiYonetici.getActiveNetworkInfo().isAvailable() &&
                baglantiYonetici.getActiveNetworkInfo().isConnected() &&
                baglantiYonetici.getActiveNetworkInfo() != null) {
            return true;
        } else {
            return false;
        }
    }

    public void tanimla() {
        lblKayitOl = (TextView) findViewById(R.id.lblKayitOl);
        lblSifreUnuttum = (TextView) findViewById(R.id.lblSifreUnuttum);
        btnGiris = (Button) findViewById(R.id.btnGiris);
        txtKullaniciAdi = (EditText) findViewById(R.id.txtKullaniciAdi);
        txtSifre = (EditText) findViewById(R.id.txtSifre);
        cbBeniHatirla = (CheckBox) findViewById(R.id.cbBeniHatirla);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lblKayitOl:
                startActivity(new Intent(GirisEkrani.this, KullaniciKaydi.class));
                break;
            case R.id.btnGiris:
                boolean durumsifre = TextUtils.isEmpty(txtSifre.getText());//boolen deger dondurme
                boolean durumkullaniciadi = TextUtils.isEmpty(txtKullaniciAdi.getText());
                if (durumsifre || durumkullaniciadi) {

                    if (durumsifre)
                        txtSifre.setError("Lütfen şifrenizi giriniz");
                    if (durumkullaniciadi)
                        txtKullaniciAdi.setError("Lütfen kullanıcı adınızı giriniz");


                } else {
                    if (!internetBaglantiKontrol()) {
                        Snackbar.make(findViewById(R.id.rootgirisEkrani), "İnternet bağlantınızı kontrol ediniz...", Snackbar.LENGTH_LONG).show();
                    } else {
                        if (!istekGonderildi) {
                            istekGonderildi = true;
                            //string bir deger alıyoruz burada
                            StringRequest request = new StringRequest(Request.Method.POST, url_login, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {


                                    istekGonderildi = false;

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String durum = jsonObject.getString("status");
                                        String mesaj = jsonObject.getString("mesaj");

                                        if (durum.equals("200")) {

                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("id", jsonObject.getString("id"));
                                            editor.putBoolean("benihatirla", cbBeniHatirla.isChecked());
                                            editor.commit();
                                            //ana ekrana geçiş
                                            Intent intent = new Intent(GirisEkrani.this, ShareFreely.class);
                                            intent.putExtra("animasyon", true);
                                            startActivity(intent);
                                            GirisEkrani.this.finish();


                                        } else {
                                            Snackbar.make(findViewById(R.id.rootgirisEkrani), mesaj, Snackbar.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("Error.Response", error.toString());
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                  //veritabanı kayıt ıslemı
                                    Map<String, String> degerler = new HashMap<>();
                                    degerler.put("kullaniciAdi", txtKullaniciAdi.getText().toString());
                                    degerler.put("sifre", txtSifre.getText().toString());
                                    return degerler;
                                }
                            };
                            //istegimizi gonderiyoruz
                            requestQueue.add(request);
                        }
                    }
                    break;
                }
        }


    }

    public void sifremiUnuttumDokun() {
        lblSifreUnuttum.setOnTouchListener(new View.OnTouchListener() {
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

    public void yeniHesapOlusturDokun() {
        lblKayitOl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)//dokunuldugu zaman
                {
                    ((TextView) v).setTextColor(Color.parseColor("#FFA500"));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((TextView) v).setTextColor(Color.BLACK);
                }
                return false;
            }
        });
    }

    public void hataMesajiTemizleme() {

        txtSifre.setError(null);
        txtKullaniciAdi.setError(null);

    }


}








