package com.kubrakocyigit.sharefreely_proje1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Adapter extends BaseAdapter {
    private Context context;
    private Activity activity1;
    private List<ShareFreelyModel> modelList;
    private boolean silmeIslemi;
    private AppCompatActivity activity;
    private String begenen_id, gonderi_id;
    Interface anInterface;
    private LikeButton likeButton;
    private SharedPreferences preferences;
    JSONArray tweetler = null;
    private TextView textView;
    private static final String url_begeni = "http://192.168.1.40/ShareFreely/";

    public Adapter(Context context, List<ShareFreelyModel> modelList, boolean silmeIslemi) {

        this.modelList = modelList;
        this.context = context;
        this.silmeIslemi = silmeIslemi;
        activity = (AppCompatActivity) context;
    }

    @Override
    public int getCount() {
        if (modelList == null)
            return 0;

        return modelList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (modelList == null)
            return null;

        final LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.gonderi_list_item, parent, false);
        likeButton = (LikeButton) layout.findViewById(R.id.btnLike);
        TextView adSoyad = (TextView) layout.findViewById(R.id.textView7);
        TextView kullaniciAdi = (TextView) layout.findViewById(R.id.textView6);
        TextView tarihTv = (TextView) layout.findViewById(R.id.textView5);
        TextView textTv = (TextView) layout.findViewById(R.id.text);
        textView = (TextView) layout.findViewById(R.id.count);
        CircleImageView circleImageView = (CircleImageView) layout.findViewById(R.id.profile_image_tweet);
        ImageView imImageView = (ImageView) layout.findViewById(R.id.imageView);

        final ShareFreelyModel tweet = modelList.get(position);
        anInterface = getApiClient().create(Interface.class);
        preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        begenen_id = preferences.getString("id", "-1");
        adSoyad.setText(tweet.getAdSoyad());
        textTv.setText(tweet.getTweetText());
        kullaniciAdi.setText(tweet.getKullaniciAdi());
        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

                gonderi_id = modelList.get(position).getUuid();
                Call<BegeniPojo> request = anInterface.begeni(begenen_id, gonderi_id);
                request.enqueue(new Callback<BegeniPojo>() {
                    @Override
                    public void onResponse(Call<BegeniPojo> call, retrofit2.Response<BegeniPojo> response) {
                        if (response.body().isTf()) {
                            Toast.makeText(activity, response.body().getMesaj(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, response.body().getMesaj(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BegeniPojo> call, Throwable t) {

                    }
                });

            }

            @Override
            public void unLiked(LikeButton likeButton) {

            }
        });


        if (!tweet.getProfilPath().equals(""))
        //Picasso.with(context).load(tweet.getProfilPath()).into(circleImageView);
        {
            Picasso.get()
                    .load(tweet.getProfilPath())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .resize(60, 60)
                    .centerCrop()
                    .into(circleImageView);
        }

        if (!tweet.getResimPath().equals(""))
            //Picasso.with(context).load(tweet.getResimPath()).into(imImageView);
            Picasso.get().load(tweet.getResimPath()).into(imImageView);


        Date simdi = new Date();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date tarih = null;
        try {
            tarih = df.parse(tweet.getTarih());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int fark = (int) (simdi.getTime() - tarih.getTime());

        int gun = fark / (1000 * 60 * 60 * 24);
        int saat = fark / (1000 * 60 * 60);
        int dakika = fark / (1000 * 60);
        int saniye = fark / (1000);

        if (saniye == 0)
            tarihTv.setText("şimdi");

        if (saniye > 0 && dakika == 0)
            tarihTv.setText(saniye + "s");

        if (dakika > 0 && saat == 0)
            tarihTv.setText(dakika + "dk");

        if (saat > 0 && gun == 0)
            tarihTv.setText(saat + "sa");

        if (gun > 0)
            tarihTv.setText(gun + "gün");

        if (silmeIslemi) {
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout.setAlpha(.5f);
                    new AlertDialog.Builder(context)
                            .setTitle("Tweet Sil")
                            .setMessage("Tweeti silmek istediğinize emin misiniz?")
                            .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    istekGonderSil(position, tweet.getUuid(), layout);
                                }
                            })
                            .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    layout.setAlpha(1);
                                }
                            }).show();

                }
            });
        }

        return layout;
    }

    private void istekGonderSil(final int position, final String uuid, final View layout) {
        final ProgressDialog loading = ProgressDialog.show(context, "Tweet siliniyor...", "Lütfen bekleyiniz...", false, false);
        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.1.110/ShareFreely/gonderiSil.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                Log.d("Json bilgisi: ", response);

                String durum = null, mesaj = null;

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    durum = jsonObject.getString("status");
                    mesaj = jsonObject.getString("mesaj");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //herşey yolundaysa
                if (durum.equals("200")) {
                    Snackbar.make(((AppCompatActivity) context).findViewById(R.id.listview), mesaj, Snackbar.LENGTH_LONG).show();
                    modelList.remove(position);
                    notifyDataSetChanged();

                } else {
                    //request başarısız ise
                    Snackbar.make(((AppCompatActivity) context).findViewById(R.id.listview), mesaj, Snackbar.LENGTH_LONG).show();
                    layout.setAlpha(1);
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
                params.put("uuid", uuid);

                if (!modelList.get(position).getResimPath().equals(""))
                    params.put("path", modelList.get(position).getResimPath());

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);

    }

    public static Retrofit retrofit = null;

    public static Retrofit getApiClient() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder().baseUrl(url_begeni)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}


