package com.kubrakocyigit.sharefreely_proje1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Interface {
    @GET("begeniler.php")
    Call<BegeniPojo> begeni(@Query("begenen_id") String begenen_id, @Query("gonderi_id") String gonderi_id);
}
