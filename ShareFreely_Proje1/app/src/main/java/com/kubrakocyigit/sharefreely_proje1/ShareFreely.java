package com.kubrakocyigit.sharefreely_proje1;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShareFreely extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences preferences;
    private String id;
    private TabLayout tabLayout;
    private TextView kAdiSoyadi, kmail;
    private CircleImageView profilFoto;
    private LikeButton likeButton;
    private ViewPager viewPager;
    private static final String url_profil_bilgileri = "http://192.168.1.40/ShareFreely/profilBilgileri.php";
    //viewpager iconnlarımız
    private int[] tabIcons = {
            R.drawable.ic_home,
            R.drawable.ic_notifacation,
            R.drawable.ic_message
    };


    @Override
    protected void onResume() {
        super.onResume();
        if (preferences.getBoolean("ProfilChanged", false)) {
            setProfilBilgileri(preferences.getString("id", "-1"));
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("ProfilChanged", false);
            editor.commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_freely);
        //giriş ekranından gelip gelmediğini kontrol ediyoruz
        boolean girisekranindangeldim = getIntent().getBooleanExtra("animasyon", false);
        if (girisekranindangeldim) {
            //--------------giriş animasyonumuzu oluşturuyoruz--------------------------
            final LinearLayout view = new LinearLayout(ShareFreely.this);
            ImageView icon = new ImageView(ShareFreely.this);
            view.setGravity(Gravity.CENTER);
            view.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            icon.setImageResource(R.drawable.logo);

            //iconun genişlik ve yükseklik özelliğini  250 olarak veriyoruz ve view nesnesine ekliyoruz
            view.addView(icon, 250, 250);
            getWindow().addContentView(view, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));


            Animation scaleAnim = AnimationUtils.loadAnimation(ShareFreely.this, R.anim.giris_animasyonu);
            icon.clearAnimation();
            icon.startAnimation(scaleAnim);

            scaleAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.0f);
                    animator.setDuration(300);
                    animator.setInterpolator(new LinearInterpolator());
                    animator.start();
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            //animasyon bittikten sonra görünürlük özelliği GONE olacak yani hiç orada yokmuş gibi olacak
                            view.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            //-----------Animasyon işlemlerinin sonu-----------------
        }
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            //fab butona tıklayınca gonderı atma ekranına geciş....
            public void onClick(View view) {
                startActivity(new Intent(ShareFreely.this, ShareFreelyGonder.class));

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        //tanımlamalar
        LinearLayout layout = (LinearLayout) navigationView.getHeaderView(0);
        kAdiSoyadi = (TextView) layout.findViewById(R.id.kAdiSoyadi);
        kmail = (TextView) layout.findViewById(R.id.kMail);
        profilFoto = (CircleImageView) layout.findViewById(R.id.profilFoto);
        preferences = PreferenceManager.getDefaultSharedPreferences(ShareFreely.this);
        id = preferences.getString("id", "-1");
        setProfilBilgileri(id);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                toolbar.setTitle(tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private void setProfilBilgileri(final String id) {

//Profil bilgileri set etme....
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_profil_bilgileri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Json verisi", response);

                String durum = "", mesaj = "", adsoyad = "", avatar = "", mail = "";
                try {
                    //veritabani guncelleme
                    JSONObject jsonObject = new JSONObject(response);
                    durum = jsonObject.getString("status");
                    mesaj = jsonObject.getString("mesaj");
                    avatar = jsonObject.getString("avatar");
                    adsoyad = jsonObject.getString("adSoyad");
                    mail = jsonObject.getString("mail");

                } catch (JSONException e) {
                    Log.e("Json parse hatası", e.getLocalizedMessage());
                }

                if (durum.equals("200")) {
                    setProfil(adsoyad, mail, avatar);
                } else {
                    Snackbar.make(findViewById(R.id.fab), mesaj, Snackbar.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //veri tabanındaki id ye göre set
                Map<String, String> degerler = new HashMap<>();
                degerler.put("id", id);
                return degerler;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private void setProfil(String adsoyad, String mail, String avatar) {
        Log.d("AVATAR", avatar);


        //profil fotoğrafının olup olmadığını kontrol ediyoruz
        if (!avatar.equals("")) {
            Picasso.get()
                    .load(avatar)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .resize(70, 70)
                    .centerCrop()
                    .into(profilFoto);
        }

        this.kAdiSoyadi.setText(adsoyad);
        this.kmail.setText(mail);

    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            startActivity(new Intent(ShareFreely.this, AramaActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new AnasayfaFragment(), "Anasayfa");
        adapter.addFrag(new BildirimlerFragment(), "Bildirimler");
        adapter.addFrag(new MesajlarFragment(), "Mesajlar");
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.arama_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profil_menu) {
            // Profil activity e geçiş
            startActivity(new Intent(ShareFreely.this, ProfilActivity.class));
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(ShareFreely.this, ShareFreely.class));

        } else if (id == R.id.nav_add) {
            startActivity(new Intent(ShareFreely.this, ShareFreelyGonder.class));
        }
             else if (id == R.id.nav_kisi_ara) {
                startActivity(new Intent(ShareFreely.this, AramaActivity.class));
            } else if (id == R.id.nav_cikis) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ShareFreely.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("benihatirla", false);
                editor.putString("id", "-1");
                editor.commit();
                startActivity(new Intent(ShareFreely.this, GirisEkrani.class));
                this.finish();
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }


        class ViewPagerAdapter extends FragmentPagerAdapter {

            private final List<Fragment> mFragmentList = new ArrayList<>();
            private final List<String> mFragmentTitleList = new ArrayList<>();

            public ViewPagerAdapter(FragmentManager manager) {
                super(manager);
            }

            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    Fragment fragment = mFragmentList.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", id);
                    fragment.setArguments(bundle);
                    return fragment;
                }
                return mFragmentList.get(position);
            }


            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            public void addFrag(Fragment fragment, String title) {
                mFragmentList.add(fragment);
                mFragmentTitleList.add(title);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentTitleList.get(position);
            }
        }


    }
