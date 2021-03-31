package com.isabel.aapspapi.service;

import com.isabel.aapspapi.domain.Country;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

import java.util.List;

/**
 * @author Isabel
 */

public interface CountriesApiService {

    //Todos los países con RxJava
    @GET("/rest/v2/all")
    Observable<List<Country>> getAllCountries();

    //Países por Continente
    @GET("/rest/v2/region/{region}")
    Call<List<Country>> getAllCountriesRegion(@Path("region") String region);
}
