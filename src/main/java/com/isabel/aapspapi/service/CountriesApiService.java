package com.isabel.aapspapi.service;

import com.isabel.aapspapi.domain.Country;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

/**
 * @author Isabel
 */

public interface CountriesApiService {

    //Todos los países
    @GET("/rest/v2/all")
    Call<List<Country>> getAllCountries();

    //Países por Continente
    @GET("/rest/v2/region/{region}")
    Call<List<Country>> getAllCountriesRegion(@Path("region") String region);
}
