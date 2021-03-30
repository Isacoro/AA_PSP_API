package com.isabel.aapspapi.util;

import javafx.scene.control.Alert;

public class Alerts {

    public static void alertError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(mensaje);
        alert.show();
    }

    public static void alertInformation(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(mensaje);
        alert.show();
    }
}
