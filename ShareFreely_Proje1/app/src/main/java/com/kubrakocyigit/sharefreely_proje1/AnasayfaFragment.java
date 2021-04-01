package com.kubrakocyigit.sharefreely_proje1;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnasayfaFragment extends Fragment {
    private String id;
    private Context context;
    private List<ShareFreelyModel> modelList;
    /* private RecyclerView recyclerView;*/
    private ListView listView;
    private TextView notText;
    private SwipeRefreshLayout refreshLayout;


    private String url = "http://192.168.1.40/ShareFreely/getGonderiler.php";


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.ansayfa_fragment, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (ListView) ((AppCompatActivity) context).findViewById(R.id.listview);
        notText = (TextView) ((AppCompatActivity) context).findViewById(R.id.noText);
        notText = (TextView) ((AppCompatActivity) context).findViewById(R.id.noText);
        refreshLayout = (SwipeRefreshLayout) ((AppCompatActivity) context).findViewById(R.id.refresh);

        // refreshLayout a 3 tane renk değeri veriyoruz. İşlem uzadıkçe sırayla verilen renk değerlerini alacak
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent), Color.BLUE, Color.GREEN);

        modelList = new ArrayList<>();
        id = getArguments().getString("id");
        istekGonder();




        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                modelList.clear();
                istekGonderRefresh();
            }
        });


        Log.d("Volley işlemleri testi", ".............................................");


    }


    private void istekGonder() {
        /*final ProgressDialog loading = ProgressDialog.show(context, "Tweetler yükleniyor...", "Lütfen bekleyiniz...", false, false);*/
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Json bilgisi Tweetler: ", response);

                String durum = null, mesaj = null;
                JSONArray tweetler = null;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    durum = jsonObject.getString("status");
                    mesaj = jsonObject.getString("mesaj");
                    tweetler = jsonObject.getJSONArray("tweetler");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //herşey yolundaysa
                if (durum.equals("200")) {

                    if (tweetler.length() == 0) {
                        notText.setText("Hiçbir tweet bulunamadı...");
                    } else {

                        for (int i = 0; i < tweetler.length(); i++) {
                            JSONObject tweet;
                            ShareFreelyModel model = new ShareFreelyModel();
                            try {
                                tweet = tweetler.getJSONObject(i);
                                model.setAdSoyad(tweet.getString("adSoyad"));
                                model.setKullaniciAdi(tweet.getString("kullaniciAdi"));
                                model.setProfilPath(tweet.getString("avatar"));
                                model.setResimPath(tweet.getString("path"));
                                model.setTweetText(tweet.getString("text"));
                                model.setTarih(tweet.getString("date"));
                                model.setUuid(tweet.getString("uuid"));//tweet silme uuid
                            } catch (JSONException e) {
                                Log.e("json parse hatası", e.getLocalizedMessage());
                            }

                            modelList.add(model);

                        }

                        setAdapter();
                    }


                } else {
                    //request başarısız ise
                    Snackbar.make(listView
                            , mesaj, Snackbar.LENGTH_LONG).show();
                }
                Log.d("Volley işlemleri testi", "request işlemler tamamlandı.........................");


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
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }//istekGonder metodu sonu

    private void setAdapter() {
        Adapter adapter = new Adapter(context, modelList, true);
        listView.setAdapter(adapter);

    }

    private void istekGonderRefresh() {

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                refreshLayout.setRefreshing(false);//
                Log.d("Json bilgisi Tweetler: ", response);

                String durum = null, mesaj = null;
                JSONArray tweetler = null;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    durum = jsonObject.getString("status");
                    mesaj = jsonObject.getString("mesaj");
                    tweetler = jsonObject.getJSONArray("tweetler");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //herşey yolundaysa
                if (durum.equals("200")) {

                    if (tweetler.length() == 0) {
                        notText.setText("Hiçbir tweet bulunamadı...");
                    } else {

                        for (int i = 0; i < tweetler.length(); i++) {
                            JSONObject tweet;
                            ShareFreelyModel model = new ShareFreelyModel();
                            try {
                                tweet = tweetler.getJSONObject(i);
                                model.setAdSoyad(tweet.getString("adSoyad"));
                                model.setKullaniciAdi(tweet.getString("kullaniciAdi"));
                                model.setProfilPath(tweet.getString("avatar"));
                                model.setResimPath(tweet.getString("path"));
                                model.setTweetText(tweet.getString("text"));
                                model.setTarih(tweet.getString("date"));
                                model.setUuid(tweet.getString("uuid"));
                            } catch (JSONException e) {
                                Log.e("json parse hatası", e.getLocalizedMessage());
                            }

                            modelList.add(model);

                        }
                        setAdapter();
                    }

                } else {
                    //request başarısız ise
                    Snackbar.make(listView, mesaj, Snackbar.LENGTH_LONG).show();
                }
                Log.d("Volley işlemleri testi", "request işlemler tamamlandı.........................");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                refreshLayout.setRefreshing(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

}

