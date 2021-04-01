package com.kubrakocyigit.sharefreely_proje1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AramaActivity extends AppCompatActivity {

    private MaterialSearchView searchView;
    private ListView listView;
    private AdapterArama adapter;
    private String id;
    private static final String url = "http://192.168.1.40/ShareFreely/selectusers.php";
    private RequestQueue requestQueue;
    private List<String> tavsiyeler;
    private List<ShareFreelyModel> modelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arama);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarArama);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        listView = (ListView) findViewById(R.id.listviewarama);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setHint("Kişilerde arayın");


        tavsiyeler = new ArrayList<>();
        modelList=new ArrayList<>();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // id değerini SharedPreferences den alıyoruz
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AramaActivity.this);
        id = preferences.getString("id", "-1");

        //activity ilk açıldığında istek göndererek sonuçları listede gösteriyoruz
        istekGonder("");


        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Snackbar.make(findViewById(R.id.container), "onQueryTextSubmit: " + query, Snackbar.LENGTH_LONG)
                        .show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Snackbar.make(findViewById(R.id.container), "onQueryTextChange: " + newText, Snackbar.LENGTH_LONG)
                        .show();
                istekGonder(newText);
                return false;
            }
        });



        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.arama_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView.setMenuItem(searchItem);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }


    private void istekGonder(final String newText) {

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Json verisi arama: ", response);
                //önce tavsiyeler ve modelList listelerini temizlememiz lazım
                tavsiyeler.clear();
                modelList.clear();

                String durum = null, mesaj = null;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    durum = jsonObject.getString("status");
                    mesaj = jsonObject.getString("mesaj");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (durum.equals("200")) {
                    JSONArray kullanicilar = null;
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        kullanicilar = jsonObject.getJSONArray("kullanicilar");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < kullanicilar.length(); i++) {

                        try {
                            JSONObject kisi = kullanicilar.getJSONObject(i);
                            ShareFreelyModel model = new ShareFreelyModel();

                            //tavsiye listesini dolduruyoruz
                            tavsiyeler.add(kisi.getString("adSoyad"));
                            tavsiyeler.add(kisi.getString("kullaniciAdi"));

                            //model listemizi dolduruyoruz
                            model.setAdSoyad(kisi.getString("adSoyad"));
                            model.setKullaniciAdi(kisi.getString("kullaniciAdi"));
                            model.setProfilPath(kisi.getString("avatar"));
                            model.setMail(kisi.getString("mail"));
                            model.setId(kisi.getString("id"));
                            modelList.add(model);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }//döngü sonu
                }else {
                    //request başarısız olursa veya hiç kayıt bulunamadıysa
                    Snackbar.make(findViewById(R.id.container), mesaj, Snackbar.LENGTH_LONG)
                            .show();
                }

                //tavsiyeler listesini diziye çevirip searchView e set ediyoruz
                String[] tavsiyelerArray = new String[tavsiyeler.size()];;
                for (int i=0; i<tavsiyeler.size(); i++) {
                    String s=tavsiyeler.get(i);
                    tavsiyelerArray[i]=s;
                }

                //searchView.setSuggestions(tavsiyelerArray);

                //adaptere verileri gönderip listview e setediyoruz
                adapter=new AdapterArama(modelList,AramaActivity.this);
                listView.setAdapter(adapter);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("word", newText);
                return params;
            }
        };
        requestQueue.add(request);
    }

}