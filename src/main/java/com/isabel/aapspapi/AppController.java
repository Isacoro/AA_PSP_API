package com.isabel.aapspapi;

import com.isabel.aapspapi.domain.Country;
import com.isabel.aapspapi.service.CountriesService;
import com.isabel.aapspapi.util.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

/**
 * @author Isabel
 */

public class AppController implements Initializable {

    public TableView<Country> tvCountries;
    public ListView<Country> lvCountries;
    public ComboBox<String> cbContinent;
    public TextField tfCapital, tfLanguage;
    public ProgressIndicator piAllCountries, piAllCountriesEu;
    public WebView wvFlag = new WebView();

    private CountriesService countriesService;
    private ObservableList<Country> listCountries;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fijarColumnasTabla();

        progressIndicatorActive(false);

        String[] continents = new String[]{"África", "América", "Asia", "Europa", "Oceanía"};
        cbContinent.setValue("Selecciona");
        cbContinent.setItems(FXCollections.observableArrayList(continents));

        listCountries = FXCollections.observableArrayList();
        lvCountries.setItems(listCountries);

        countriesService = new CountriesService();
    }

    //Países por Continente elegido
    @FXML
    public void allCountriesContinent(Event event){
        String selection = cbContinent.getValue();

        switch (selection){
            case "África":
                CompletableFuture.runAsync(() -> loadingCountries("Africa"));
                progressIndicatorActive(true);
                break;
            case "América":
                CompletableFuture.runAsync(() -> loadingCountries("Americas"));
                progressIndicatorActive(true);
                break;
            case "Asia":
                CompletableFuture.runAsync(() -> loadingCountries("Asia"));
                progressIndicatorActive(true);
                break;
            case "Europa":
                CompletableFuture.runAsync(() -> loadingCountries("Europe"));
                progressIndicatorActive(true);
                break;
            case "Oceanía":
                CompletableFuture.runAsync(() -> loadingCountries("Oceania"));
                progressIndicatorActive(true);
                break;
            default:
                Alerts.alertInformation("Elige una opción");
                break;
        }
    }


    //Mostrar Detalle (bandera) por país elegido
    @FXML
    public void detailFlag(Event event){
        Country country = tvCountries.getSelectionModel().getSelectedItem();
        wvFlag.getEngine().load(country.getFlag());
    }


    //Fijar columnas de la tabla países por continente
    public void fijarColumnasTabla(){
        Field[] fields = Country.class.getDeclaredFields();
        for(Field field : fields){

            TableColumn<Country, String> column = new TableColumn<>(field.getName());
            column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
            tvCountries.getColumns().add(column);
        }
        tvCountries.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }


    //Carga de los países por continente
    public void loadingCountries(String continent) {
        List<Country> countries = null;

        try {
            countries = countriesService.getAllCountriesRegion(continent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tvCountries.setItems(FXCollections.observableArrayList(countries));
    }


    //Indicador de progreso
    public void progressIndicatorActive(boolean active){
        piAllCountries.setVisible(active);
        piAllCountries.setProgress(-1);
    }



    //Todos los países de la api
    @FXML
    public void allCountries(Event event){
        lvCountries.getItems().clear();

        countriesService.getAllCountries()
                .flatMap(Observable::from)
                .doOnCompleted(() -> System.out.println("Lista descargada"))
                .doOnError(throwable -> System.out.println("Error: " + throwable))
                .subscribeOn(Schedulers.from(Executors.newCachedThreadPool()))
                .subscribe(country -> listCountries.add(country));
    }

    @FXML
    public void detailList(Event event){
        Country countrySelection = lvCountries.getSelectionModel().getSelectedItem();
        tfCapital.setText(countrySelection.getCapital());
        tfLanguage.setText(countrySelection.getLanguage());


    }

    public void loadingDetails(Country country){
        tfCapital.setText(country.getCapital());
        tfLanguage.setText(country.getLanguage());

    }
}
