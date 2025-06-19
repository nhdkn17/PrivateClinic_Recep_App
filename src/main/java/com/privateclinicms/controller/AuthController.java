package com.privateclinicms.controller;

import com.privateclinicms.dao.UserDAO;
import com.privateclinicms.model.User;
import com.privateclinicms.controller.other.Dialog;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class AuthController {

    @FXML
    private TextField loginUsernameField;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    private TextField loginPasswordVisibleField;
    @FXML
    private Button loginButton, toRegisterButton;
    @FXML
    private TextField registerUsernameField;
    @FXML
    private PasswordField registerPasswordField;
    @FXML
    private TextField registerPasswordVisibleField;
    @FXML
    private PasswordField registerConfirmPasswordField;
    @FXML
    private TextField registerConfirmPasswordVisibleField;
    @FXML
    private Button registerButton, toLoginButton;
    @FXML
    private Pane slidingImage;
    @FXML
    private CheckBox registerShowPasswordCheckBox, loginShowPasswordCheckBox;

    private UserDAO userDAO = new UserDAO();
    private TranslateTransition slideTransition;
    @FXML
    private void initialize() {
        slideTransition = new TranslateTransition(Duration.millis(500), slidingImage);
        slideTransition.setToX(0);

        loginPasswordVisibleField.setVisible(false);
        loginPasswordVisibleField.textProperty().bindBidirectional(loginPasswordField.textProperty());
        loginShowPasswordCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            loginPasswordField.setVisible(!newVal);
            loginPasswordVisibleField.setVisible(newVal);
        });

        registerPasswordVisibleField.setVisible(false);
        registerConfirmPasswordVisibleField.setVisible(false);
        registerPasswordVisibleField.textProperty().bindBidirectional(registerPasswordField.textProperty());
        registerConfirmPasswordVisibleField.textProperty().bindBidirectional(registerConfirmPasswordField.textProperty());
        registerShowPasswordCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            registerPasswordField.setVisible(!newVal);
            registerConfirmPasswordField.setVisible(!newVal);
            registerPasswordVisibleField.setVisible(newVal);
            registerConfirmPasswordVisibleField.setVisible(newVal);
        });
    }

    @FXML
    private void handleLogin() {
        String username = loginUsernameField.getText();
        String password = loginPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            Dialog.showNotice("Lỗi", "Vui lòng nhập tên đăng nhập và mật khẩu", false);
            return;
        }

        User user = userDAO.authenticate(username, password);
        if (user != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/privateclinicms/MainView.fxml"));
                Scene scene = new Scene(loader.load(), 1530, 787);
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Quản Lý Phòng Khám");
                stage.setResizable(true);
                stage.show();
            } catch (IOException e) {
                Dialog.showNotice("Lỗi", "Lỗi khi tải giao diện chính: " + e.getMessage(), false);
            }
        } else {
            Dialog.showNotice("Lỗi", "Sai tên đăng nhập hoặc mật khẩu", false);
        }
    }

    @FXML
    private void handleRegister() {
        String username = registerUsernameField.getText();
        String password = registerPasswordField.getText();
        String confirmPassword = registerConfirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Dialog.showNotice("Lỗi", "Vui lòng điền đầy đủ thông tin", false);
            return;
        }

        if (!password.equals(confirmPassword)) {
            Dialog.showNotice("Lỗi", "Mật khẩu xác nhận không khớp", false);
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        if (userDAO.addUser(user)) {
            Dialog.showNotice("Thành Công", "Đăng ký thành công! Vui lòng đăng nhập.", true);
            showLogin();
        } else {
            Dialog.showNotice("Lỗi", "Tên đăng nhập đã tồn tại hoặc lỗi khi đăng ký", false);
        }
    }

    @FXML
    private void showRegister() {
        slideTransition.setToX(765);
        slideTransition.play();
    }

    @FXML
    private void showLogin() {
        slideTransition.setToX(0);
        slideTransition.play();
    }
}
