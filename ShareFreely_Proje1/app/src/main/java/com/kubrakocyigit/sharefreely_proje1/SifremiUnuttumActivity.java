package com.kubrakocyigit.sharefreely_proje1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SifremiUnuttumActivity extends AppCompatActivity {
    private TextInputLayout textInputLayout;
    private TextInputEditText email;
    private static final String url = "http://192.168.1.40/ShareFreely/sifreyiSifirla.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sifremi_unuttum);
        textInputLayout = findViewById(R.id.textInputLayout);
        email = findViewById(R.id.emailsifremiunuttum);

        findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInputLayout.setError(null);

                if (email.getText().toString().trim().length() == 0) {
                    textInputLayout.setError("Lütfen mail adresinizi giriniz");
                } else if (!email.getText().toString().contains("@")) {
                    textInputLayout.setError("Lütfen geçerli bir mail adresi giriniz");
                } else {
                    istekGonder(email.getText().toString());
                }

            }
        });
    }
    private void istekGonder(final String mail) {

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Json verisi: ", response);

                JSONObject jsonObject = null;
                String durum = null;
                String mesaj = null;
                try {
                    jsonObject = new JSONObject(response);
                    durum = jsonObject.getString("status");
                    mesaj = jsonObject.getString("mesaj");

                } catch (JSONException e) {
                    Log.e("Json parse hatası", e.getLocalizedMessage());
                }

                if (durum.equals("200")) {
                    new AlertDialog.Builder(SifremiUnuttumActivity.this)
                            .setMessage("Şifrenizi yenilemeniz için size bir mail gönderdik. Lütfen e-postanızı kontrol ediniz.")
                            .show();

                } else {
                    Snackbar.make(findViewById(R.id.rootSifremiUnuttum), mesaj, Snackbar.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> degerler = new HashMap<>();
                degerler.put("mail", mail);
                return degerler;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SifremiUnuttumActivity.this);
        requestQueue.add(request);

    }
}