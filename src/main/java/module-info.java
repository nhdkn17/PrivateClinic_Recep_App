module com.privateclinicms {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires jbcrypt;
    requires annotations;
    requires com.google.gson;
    requires jakarta.mail;

    opens com.privateclinicms to javafx.fxml;
    opens com.privateclinicms.client.controller to javafx.fxml;
    opens com.privateclinicms.client.controller.other to javafx.fxml;
    opens com.privateclinicms.client.controller.medicalThread to javafx.fxml;
    opens com.privateclinicms.client.network to com.google.gson;
    opens com.privateclinicms.shared.model to com.google.gson, javafx.base;

    exports com.privateclinicms;
    exports com.privateclinicms.client.controller;
    exports com.privateclinicms.client.controller.other;
    exports com.privateclinicms.client.controller.medicalThread;

    opens com.privateclinicms.client.utils to javafx.base;
    opens com.privateclinicms.shared.config to javafx.base;
    opens com.privateclinicms.shared.protocol to com.google.gson;
}