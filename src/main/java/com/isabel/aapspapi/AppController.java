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
import javafx.stage.FileChooser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Isabel
 */

public class AppController implements Initializable {

    public TableView<Country> tvCountries;
    public ListView<Country> lvCountries;
    public ComboBox<String> cbContinent;
    public TextField tfRegion, tfSubregion, tfCapital, tfSearchCountrie, tfPopulation;
    public Button btExportCsv, btExportZip;
    public ProgressIndicator piAllCountriesRegion, piAllCountries;
    public WebView wvFlag = new WebView();

    private CountriesService countriesService;
    private ObservableList<Country> listCountries;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fijarColumnasTabla();
        progressIndicatorActiveTable(false);
        progressIndicatorActiveList(false);

        String[] continents = new String[]{"África", "América", "Asia", "Europa", "Oceanía"};
        cbContinent.setValue("Selecciona");
        cbContinent.setItems(FXCollections.observableArrayList(continents));

        listCountries = FXCollections.observableArrayList();
        lvCountries.setItems(listCountries);

        countriesService = new CountriesService();
    }

    //Países por Continente elegido
    @FXML
    public void allCountriesContinent(Event event) {
        String selection = cbContinent.getValue();

        switch (selection) {
            case "África":
                CompletableFuture.runAsync(() -> loadingCountries("Africa"));
                progressIndicatorActiveTable(true);
                break;

            case "América":
                CompletableFuture.runAsync(() -> loadingCountries("Americas"));
                progressIndicatorActiveTable(true);
                break;

            case "Asia":
                CompletableFuture.runAsync(() -> loadingCountries("Asia"));
                progressIndicatorActiveTable(true);
                break;
            case "Europa":
                CompletableFuture.runAsync(() -> loadingCountries("Europe"));
                progressIndicatorActiveTable(true);
                break;

            case "Oceanía":
                CompletableFuture.runAsync(() -> loadingCountries("Oceania"));
                progressIndicatorActiveTable(true);
                break;

            default:
                Alerts.alertInformation("Elige una opción");
                break;
        }
    }

    //Mostrar detalle (bandera) por país elegido
    @FXML
    public void detailFlag(Event event) {
        Country country = tvCountries.getSelectionModel().getSelectedItem();
        wvFlag.getEngine().load(country.getFlag());
        wvFlag.setZoom(0.3);
    }

    //Fijar columnas de la tabla países por continente
    public void fijarColumnasTabla() {
        Field[] fields = Country.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals("flag"))
                continue;
            if (field.getName().equals("subregion"))
                continue;

            TableColumn<Country, String> column = new TableColumn<>(field.getName());
            column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
            tvCountries.getColumns().add(column);
        }
        tvCountries.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    //Carga de los países por continente
    public void loadingCountries(String continent) {
        List<Country> countries = countriesService.getAllCountriesRegion(continent);

        tvCountries.setItems(FXCollections.observableArrayList(countries));
        progressIndicatorActiveTable(false);
    }


    //Indicador de progreso de la tabla
    public void progressIndicatorActiveTable(boolean active) {
        piAllCountriesRegion.setVisible(active);
        piAllCountriesRegion.setProgress(-1);
    }

    //Todos los países de la api
    @FXML
    public void allCountries(Event event) {
        lvCountries.getItems().clear();
        progressIndicatorActiveList(true);

        countriesService.getAllCountries()
                .flatMap(Observable::from)
                .doOnCompleted(() -> progressIndicatorActiveList(false))
                .doOnError(throwable -> System.out.println("Error: " + throwable))
                .subscribeOn(Schedulers.from(Executors.newCachedThreadPool()))
                .subscribe(country -> listCountries.add(country));
    }

    //Método de selección de país en lista y los muestra en los textFields
    @FXML
    public void detailList(Event event) {
        Country countrySelection = lvCountries.getSelectionModel().getSelectedItem();
        tfRegion.setText(countrySelection.getRegion());
        tfSubregion.setText(countrySelection.getSubregion());
        tfCapital.setText(countrySelection.getCapital());
    }

    //Indicador de progreso de la lista
    public void progressIndicatorActiveList(boolean active) {
        piAllCountries.setVisible(active);
        piAllCountries.setProgress(-1);
    }

    //Busqueda por país elegido
    @FXML
    public void searchCountrie(Event event) {

        String nameCountrie = tfSearchCountrie.getText();

        countriesService.getAllCountries()
                .flatMap(Observable::from)
                .filter(country -> country.getName().startsWith(nameCountrie))
                .doOnCompleted(() -> {
                    System.out.println("Datos descargados");
                })
                .doOnError(throwable -> System.out.println("Error: " + throwable))
                .subscribeOn(Schedulers.from(Executors.newCachedThreadPool()))
                .subscribe(country -> tfPopulation.setText(String.valueOf(country.getPopulation())));
    }

    @FXML
    public void exportCSV(Event event) {
        exportCsvMethod();
        Alerts.alertInformation("Exportado el archivo csv");
    }

    @FXML
    public void exportZIP(Event event) {
//        CompletableFuture.supplyAsync(() -> exportCsvMethod()).thenAccept(value -> exportZipMethod(value));

    }

    private File exportCsvMethod() {

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(btExportCsv.getScene().getWindow());

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(file + ".csv");

            CSVPrinter printer = null;

            printer = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);


            List<Country> exportCSVList = listCountries;

            for (Country country : exportCSVList) {
                printer.printRecord(
                        country.getName()
                );
            }
            printer.close();

        } catch (IOException io) {
            Alerts.alertError("Error al exportar los datos");
        }
        return file;
    }

    private void exportZipMethod(File file) {

//        try {
//            fos = new FileOutputStream("compressed.zip");
//
//            ZipOutputStream zipOut = new ZipOutputStream(fos);
//
//            FileInputStream fis = new FileInputStream(file);
//            ZipEntry zipEntry = new ZipEntry(file.getName());
//            zipOut.putNextEntry(zipEntry);
//
//            byte[] bytes = new byte[1024];
//            int length;
//            while ((length = fis.read(bytes)) >= 0) {
//                zipOut.write(bytes, 0, length);
//            }
//
//            zipOut.close();
//            fis.close();
//            fos.close();
//        } catch (IOException io) {
//            Alerts.alertError("Error al exportar los datos");
//        }
//    }

    }
}
