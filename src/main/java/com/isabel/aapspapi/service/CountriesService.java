package com.isabel.aapspapi.service;

import com.isabel.aapspapi.domain.Country;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

import java.io.IOException;
import java.util.List;

import static com.isabel.aapspapi.util.Constants.URL_BASE;

/**
 * @author Isabel
 */

public class CountriesService {

    private CountriesApiService countryApiService;

    //Constructor
    public CountriesService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        countryApiService = retrofit.create(CountriesApiService.class);
    }


    //Lista de todos los países según el continente
    public List<Country> getAllCountriesRegion(String region) {
        Call<List<Country>> callAllCountries = countryApiService.getAllCountriesRegion(region);

        try {
            Response<List<Country>> response = callAllCountries.execute();
            return response.body();
        }catch (IOException io){
            io.printStackTrace();
        }
        return null;
    }

    //Lista de todos los países usando RxJava
    public Observable<List<Country>> getAllCountries(){
        return countryApiService.getAllCountries();
    }
}
