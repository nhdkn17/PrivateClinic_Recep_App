package com.privateclinicms;

import com.privateclinicms.client.controller.other.Dialog;
import com.privateclinicms.shared.model.NhanVien;
import com.privateclinicms.shared.model.SectionNhanVien;
import com.privateclinicms.shared.util.JDBCUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainController {
    @FXML
    private AnchorPane mainContent;

    @FXML
    public void initialize() {
        loadDashboardContent();
    }

    @FXML
    private void loadDashboardContent() {
        loadFXMLContent("Dashboard.fxml");
    }

    @FXML
    private void loadPatientContent() {
        loadFXMLContent("PatientView.fxml");
    }

    @FXML
    private void loadAppointmentContent() {
        loadFXMLContent("AppointmentView.fxml");
    }

    @FXML
    private void loadMedicineContent() {
        loadFXMLContent("MedicineView.fxml");
    }

    @FXML
    private void loadWarehouseContent() {
        loadFXMLContent("WarehouseView.fxml");
    }

    private void loadFXMLContent(String fxmlFile) {
        try {
            mainContent.getChildren().clear();
            Node node = FXMLLoader.load(getClass().getResource(fxmlFile));
            mainContent.getChildren().add(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void actionLogout(ActionEvent event) {
        try {
            NhanVien nhanVien = SectionNhanVien.getNhanVien();
            if (nhanVien != null) {
                markUserAsLoggedOut(nhanVien.getMaNhanVien());
            }

            Stage currentStage = (Stage) mainContent.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(ClinicApp.class.getResource("AuthView.fxml"));
            Scene loginScene = new Scene(loader.load());
            Stage loginStage = new Stage();
            loginStage.getIcons().add(new Image(getClass().getResource("/image/Clinic-logo.png").toString()));
            loginStage.setScene(loginScene);
            loginStage.setTitle("Đăng Nhập PC Recep");
            loginStage.show();
        } catch (Exception e) {
            Dialog.showNotice("Lỗi", "Không thể đăng xuất!", false);
            e.printStackTrace();
        }
    }

    public void markUserAsLoggedOut(int maNhanVien) {
        Integer maTaiKhoan = getMaTaiKhoanFromNhanVien(maNhanVien);
        if (maTaiKhoan == null) return;

        String query = "DELETE FROM DangNhapHienTai WHERE MaTaiKhoan = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, maTaiKhoan);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
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
}
