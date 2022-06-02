package com.example.exampleroom.IO;
import com.example.exampleroom.models.PublicApi;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface PxApiService {

    // https://api.publicapis.org/entries?description=pictures
    @GET
    Call<PublicApi> getEntries(@Url String url);

}
