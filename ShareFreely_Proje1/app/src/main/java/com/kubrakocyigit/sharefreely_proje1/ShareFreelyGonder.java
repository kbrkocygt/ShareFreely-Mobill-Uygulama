package com.kubrakocyigit.sharefreely_proje1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShareFreelyGonder extends AppCompatActivity {
    private TextView sayac;
    private EditText gonderiYazisi;
    private ImageView gonderiResmi;
    private ImageButton resimEkleme;
    private FloatingActionButton fab;
    private Bitmap bitmap;
    private SharedPreferences preferences;
    private RequestQueue requestQueue;
    private String id;
    private String gonderi_id;
    //sabitler , birden cok seyi bir arada  yapma
    private static final int SADECE_RESIM = 1;
    private static final int SADECE_TEXT = 2;
    private static final int TEXT_VE_RESIM = 3;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String url_tweetler = "http://192.168.1.40/ShareFreely/gonderiler.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_freely_gonder);

        sayac = (TextView) findViewById(R.id.sayac);
        gonderiYazisi = (EditText) findViewById(R.id.gonderiYazisi);
        gonderiResmi = (ImageView) findViewById(R.id.gonderiResmi);
        resimEkleme = (ImageButton) findViewById(R.id.resimEkleme);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        preferences = PreferenceManager.getDefaultSharedPreferences(ShareFreelyGonder.this);
        id = preferences.getString("id", "-1");
        requestQueue = Volley.newRequestQueue(ShareFreelyGonder.this);

        gonderiYazisi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //Toast.makeText(TweetGonder.this, "start:"+start+" before: "+before+" count:"+count, Toast.LENGTH_SHORT).show();
                //int i = 280 - start; ->hatalı kullanım
                //int i = 280 - s.length(); // boşluklarda karakter sayılacaksa
                int i = 280 - s.toString().trim().length();
                sayac.setText(String.valueOf(i));

                if (i < 0) {
                    sayac.setTextColor(Color.RED);
                    fab.hide();
                } else {
                    sayac.setTextColor(Color.parseColor("#444444"));
                    //sayac.setTextColor(Color.parseColor("#444")); -->hatalı kullanım
                    fab.show();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        resimEkleme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //resim seçme isteği gönderilecek
                resimSecmeIstegi();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //tweet gönderme isteği yapılacak

                if (gonderiYazisi.getText().toString().trim().length() != 0 && bitmap != null) {
                    // hem text hem resim
                    istekGonder(TEXT_VE_RESIM);
                }

                if (gonderiYazisi.getText().toString().trim().length() != 0 && bitmap == null) {
                    // sadece text
                    istekGonder(SADECE_TEXT);
                }

                if (gonderiYazisi.getText().toString().trim().length() == 0 && bitmap != null) {
                    // sadece resim
                    istekGonder(SADECE_RESIM);
                }

                if (gonderiYazisi.getText().toString().trim().length() == 0 && bitmap == null) {
                    // ikiside boş
                    Snackbar.make(findViewById(R.id.fab), "Hiçbir tweet oluşturmadınız.", Snackbar.LENGTH_LONG).show();
                }


            }
        });
    }

    private void istekGonder(final int istekTuru) {
        final ProgressDialog loading = ProgressDialog.show(ShareFreelyGonder.this, "Tweet gönderiliyor...", "Lütfen bekleyiniz...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_tweetler, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Json bilgisi: ", response);
                loading.dismiss();

                String durum = null, mesaj = null;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    durum = jsonObject.getString("status");
                    mesaj = jsonObject.getString("mesaj");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (durum.equals("200")) {
                    Snackbar.make(findViewById(R.id.fab), "Tweet başarılı bir şekilde gönderildi.", Snackbar.LENGTH_LONG).show();
                    gonderiYazisi.setText("");
                    gonderiResmi.setImageBitmap(null);

                } else {
                    Snackbar.make(findViewById(R.id.fab), mesaj, Snackbar.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                String uuid = UUID.randomUUID().toString();

                switch (istekTuru) {

                    case SADECE_RESIM:
                        //Bitmap dan String e dönüştürülüyor
                        String image = getStringImage(bitmap);

                        //parametreler ekleniyor
                        params.put("id", id);
                        params.put("uuid", uuid);
                        params.put("resim", image);
                        params.put("istek_turu", String.valueOf(SADECE_RESIM));
                        break;

                    case SADECE_TEXT:
                        //parametreler ekleniyor
                        params.put("id", id);
                        params.put("uuid", uuid);
                        params.put("text", gonderiYazisi.getText().toString());
                        params.put("istek_turu", String.valueOf(SADECE_TEXT));
                        break;

                    case TEXT_VE_RESIM:
                        //Bitmap dan String e dönüştürülüyor
                        String image2 = getStringImage(bitmap);

                        //parametreler ekleniyor
                        params.put("id", id);
                        params.put("uuid", uuid);
                        params.put("resim", image2);
                        params.put("text", gonderiYazisi.getText().toString());
                        params.put("istek_turu", String.valueOf(TEXT_VE_RESIM));
                        break;
                }
                return params;
            }
        };


        requestQueue.add(stringRequest);


    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void resimSecmeIstegi() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Resim seçiniz"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            gonderiResmi.setImageBitmap(bitmap);
        }
    }

}