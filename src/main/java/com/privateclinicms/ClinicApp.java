package com.privateclinicms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class ClinicApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClinicApp.class.getResource("AuthView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1530, 787);
        stage.getIcons().add(new Image(getClass().getResource("/image/Clinic-logo.png").toString()));
        stage.setTitle("Đăng Nhập");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
