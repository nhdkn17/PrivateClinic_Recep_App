package com.privateclinicms.client.controller;

import com.privateclinicms.client.service.CL_AuthService;
import com.privateclinicms.client.controller.other.Dialog;
import com.privateclinicms.shared.model.NhanVien;
import com.privateclinicms.shared.model.SectionNhanVien;
import com.privateclinicms.shared.protocol.Response;
import com.privateclinicms.shared.util.JDBCUtil;
import com.privateclinicms.shared.util.JsonUtils;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AuthController {

    @FXML private TextField loginEmailField;
    @FXML private PasswordField loginPasswordField;
    @FXML private TextField loginPasswordVisibleField;
    @FXML private Button loginButton, toRegisterButton;

    @FXML private TextField registerEmailField;
    @FXML private PasswordField registerPasswordField;
    @FXML private TextField registerPasswordVisibleField;
    @FXML private PasswordField registerConfirmPasswordField;
    @FXML private TextField registerConfirmPasswordVisibleField;
    @FXML private Button registerButton, toLoginButton;

    @FXML private Pane slidingImage;
    @FXML private CheckBox registerShowPasswordCheckBox, loginShowPasswordCheckBox;
    @FXML private ComboBox<String> registerRoleComboBox;
    @FXML private Pane receptionistFields, doctorFields;
    @FXML private TextField receptionistNameField, receptionistPhoneField;
    @FXML private TextField doctorNameField, doctorSpecialtyField, doctorPhoneField;

    private final CL_AuthService CLAuthService = new CL_AuthService();
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

        registerRoleComboBox.getSelectionModel().selectFirst();
        registerRoleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateRoleFields(newVal));
    }

    @FXML
    private void handleLogin() throws IOException {
        String email = loginEmailField.getText();
        String password = loginPasswordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            Dialog.showNotice("Lỗi", "Vui lòng nhập email và mật khẩu", false);
            return;
        }

        Response res = CLAuthService.login(email, password);
        if (res == null) {
            Dialog.showNotice("Lỗi", "Không nhận được phản hồi từ server!", false);
            return;
        }
        if ("success".equals(res.getStatus())) {
            NhanVien nhanVien = JsonUtils.convertToObject(res.getData(), NhanVien.class);
            SectionNhanVien.setNhanVien(nhanVien);

            assert nhanVien != null;
            markUserAsLoggedIn(nhanVien.getMaNhanVien());
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/privateclinicms/MainView.fxml"));
                Scene scene = new Scene(loader.load(), 1530, 787);
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("PC Recep");
                stage.getIcons().add(new Image(getClass().getResource("/image/Clinic-logo.png").toString()));
                stage.setResizable(true);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                Dialog.showNotice("Lỗi", "Không thể tải giao diện chính: " + e.getMessage(), false);
            }
        } else {
            Dialog.showNotice("Lỗi", res.getMessage(), false);
        }
    }

    @FXML
    private void handleRegister() throws IOException {
        String email = registerEmailField.getText().trim();
        String password = registerPasswordField.getText().trim();
        String confirmPassword = registerConfirmPasswordField.getText().trim();
        String role = registerRoleComboBox.getValue();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Dialog.showNotice("Lỗi", "Vui lòng điền đầy đủ thông tin", false);
            return;
        }

        if (!password.equals(confirmPassword)) {
            Dialog.showNotice("Lỗi", "Mật khẩu xác nhận không khớp", false);
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("password", password);
        data.put("vaiTro", role);

        if ("LE_TAN".equals(role)) {
            String hoTen = receptionistNameField.getText().trim();
            String soDienThoai = receptionistPhoneField.getText().trim();

            if (hoTen.isEmpty() || soDienThoai.isEmpty()) {
                Dialog.showNotice("Lỗi", "Vui lòng nhập đầy đủ thông tin lễ tân", false);
                return;
            }

            data.put("hoTen", hoTen);
            data.put("soDienThoai", soDienThoai);

        } else if ("BAC_SI".equals(role)) {
            String tenBacSi = doctorNameField.getText().trim();
            String chuyenKhoa = doctorSpecialtyField.getText().trim();
            String soDienThoai = doctorPhoneField.getText().trim();

            if (tenBacSi.isEmpty() || chuyenKhoa.isEmpty() || soDienThoai.isEmpty()) {
                Dialog.showNotice("Lỗi", "Vui lòng nhập đầy đủ thông tin bác sĩ", false);
                return;
            }

            data.put("tenBacSi", tenBacSi);
            data.put("chuyenKhoa", chuyenKhoa);
            data.put("soDienThoai", soDienThoai);
        }

        Response res = CLAuthService.register(data);

        if (res != null && "success".equals(res.getStatus())) {
            Dialog.showNotice("Thành công", "Đăng ký thành công! Vui lòng đăng nhập.", true);
            showLogin();
        } else {
            Dialog.showNotice("Lỗi", res != null ? res.getMessage() : "Đăng ký thất bại", false);
        }
    }

    private Integer getMaTaiKhoanFromNhanVien(int maNhanVien) {
        String sql = "SELECT MaTaiKhoan FROM TaiKhoan WHERE MaNhanVien = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maNhanVien);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("MaTaiKhoan");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void markUserAsLoggedIn(int maNhanVien) {
        Integer maTaiKhoan = getMaTaiKhoanFromNhanVien(maNhanVien);
        if (maTaiKhoan == null) return;

        String query = "INSERT INTO DangNhapHienTai (MaTaiKhoan) VALUES (?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, maTaiKhoan);
            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            // đã tồn tại, bỏ qua
        } catch (SQLException e) {
            e.printStackTrace();
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

    private void updateRoleFields(String role) {
        receptionistFields.setVisible("LE_TAN".equals(role));
        doctorFields.setVisible("BAC_SI".equals(role));
    }
}
