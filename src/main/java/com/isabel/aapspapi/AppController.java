package com.isabel.aapspapi;

import com.isabel.aapspapi.domain.Country;
import com.isabel.aapspapi.service.CountriesService;
import com.isabel.aapspapi.util.Alerts;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

/**
 * @author Isabel
 */

public class AppController implements Initializable {

    public TableView<Country> tvCountries;
    public ListView lvCountriesEu;
    public ComboBox<String> cbContinent;
    public Button btCountriesEu;
    public TextField tfCapital, tfPopulation, tfCapitalCountryEu, tfLanguage;
    public ProgressIndicator piAllCountries, piAllCountriesEu;

    private CountriesService countriesService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fijarColumnasTabla();

        String[] continents = new String[]{"África", "América", "Asia", "Europa", "Oceanía"};
        cbContinent.setValue("Selecciona");
        cbContinent.setItems(FXCollections.observableArrayList(continents));

        countriesService = new CountriesService();
    }

    @FXML
    public void allCountries(Event event){
        String selection = cbContinent.getValue();

        switch (selection){
            case "África":
                CompletableFuture.runAsync(() -> loadingCountries("Africa"));
                loading();
                break;
            case "América":
                CompletableFuture.runAsync(() -> loadingCountries("Americas"));
                loading();
                break;
            case "Asia":
                CompletableFuture.runAsync(() -> loadingCountries("Asia"));
                loading();
                break;
            case "Europa":
                CompletableFuture.runAsync(() -> loadingCountries("Europe"));
                loading();
                break;
            case "Oceanía":
                CompletableFuture.runAsync(() -> loadingCountries("Oceania"));
                loading();
                break;
            default:
                Alerts.alertInformation("Elige una opción");
                break;
        }
    }


    @FXML
    public void allCountriesEu(Event event){

    }

    @FXML
    public void countryDetail(Event event){

    }


    public void fijarColumnasTabla(){
        Field[] fields = Country.class.getDeclaredFields();
        for(Field field : fields){

            TableColumn<Country, String> column = new TableColumn<>(field.getName());
            column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
            tvCountries.getColumns().add(column);
        }
        tvCountries.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void loadingCountries(String continent) {
        List<Country> countries = null;
        try {
            countries = countriesService.getAllCountriesRegion(continent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tvCountries.setItems(FXCollections.observableArrayList(countries));
    }

    public void loading(){

    }
}
