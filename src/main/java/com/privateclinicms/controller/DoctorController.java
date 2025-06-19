package com.privateclinicms.controller;

import com.privateclinicms.controller.other.Dialog;
import com.privateclinicms.dao.BacSiDAO;
import com.privateclinicms.model.BacSi;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.controlsfx.control.textfield.TextFields;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DoctorController {
    @FXML
    private FlowPane flowCardBacSi;
    @FXML
    private TextField txtTenBacSi;
    @FXML
    private TextField txtChuyenKhoa;
    @FXML
    private TextField txtSoDienThoai;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtTimKiem;
    @FXML
    private Pane paneBieuDo;

    private List<BacSi> danhSachBacSi = new ArrayList<>();
    private BacSiDAO bacSiDAO = new BacSiDAO();
    private BacSi currentEditingBacSi;

    @FXML
    public void initialize() {
        loadDanhSachBacSi();
        setupAutoCompleteChuyenKhoa();
        Map<String, Integer> data = bacSiDAO.laySoBacSiTheoChuyenKhoa();
        loadBieuDoSoBacSi(data);
    }

    private void setupAutoCompleteChuyenKhoa() {
        List<String> dsChuyenKhoa = List.of(
                "Nội khoa", "Ngoại khoa", "Sản phụ khoa", "Nhi khoa",
                "Tai mũi họng", "Răng hàm mặt", "Da liễu", "Tim mạch",
                "Ngoại thần kinh", "Tâm thần", "Mắt", "Nội tiết",
                "Hồi sức cấp cứu", "Phục hồi chức năng"
        );
        TextFields.bindAutoCompletion(txtChuyenKhoa, dsChuyenKhoa);
    }

    @FXML
    private void handleThem() {
        try {
            String ten = txtTenBacSi.getText().trim();
            String chuyenKhoa = txtChuyenKhoa.getText().trim();
            String sdt = txtSoDienThoai.getText().trim();
            String email = txtEmail.getText().trim();

            if (ten.isEmpty()) {
                Dialog.showNotice("Lỗi", "Tên bác sĩ không được để trống", false);
                return;
            }

            BacSi bacSiMoi = new BacSi(ten, chuyenKhoa, sdt, email);
            bacSiDAO.add(bacSiMoi);

            Dialog.showNotice("Thành công", "Thêm bác sĩ thành công!", true);
            loadDanhSachBacSi();
            clearForm();

        } catch (Exception e) {
            Dialog.showNotice("Lỗi", "Thêm bác sĩ thất bại!\n" + e.getMessage(), false);
        }
        Map<String, Integer> data = bacSiDAO.laySoBacSiTheoChuyenKhoa();
        loadBieuDoSoBacSi(data);
    }

    @FXML
    private void handleSua() {
        if (currentEditingBacSi == null) {
            Dialog.showNotice("Lỗi", "Chưa chọn bác sĩ để sửa", false);
            return;
        }

        try {
            currentEditingBacSi.setTenBacSi(txtTenBacSi.getText().trim());
            currentEditingBacSi.setChuyenKhoa(txtChuyenKhoa.getText().trim());
            currentEditingBacSi.setSoDienThoai(txtSoDienThoai.getText().trim());
            currentEditingBacSi.setEmail(txtEmail.getText().trim());

            bacSiDAO.update(currentEditingBacSi);

            Dialog.showNotice("Thành công", "Cập nhật bác sĩ thành công!", true);
            loadDanhSachBacSi();
            clearForm();
            currentEditingBacSi = null;

        } catch (Exception e) {
            Dialog.showNotice("Lỗi", "Cập nhật bác sĩ thất bại!\n" + e.getMessage(), false);
        }
        Map<String, Integer> data = bacSiDAO.laySoBacSiTheoChuyenKhoa();
        loadBieuDoSoBacSi(data);
    }

    @FXML
    private void handleTimKiem() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            loadDanhSachBacSi();
            return;
        }

        List<BacSi> filteredList = new ArrayList<>();
        for (BacSi bacSi : danhSachBacSi) {
            if (bacSi.getTenBacSi().toLowerCase().contains(keyword) ||
                    bacSi.getChuyenKhoa().toLowerCase().contains(keyword)) {
                filteredList.add(bacSi);
            }
        }
        hienThiDanhSachBacSi(filteredList);
    }

    @FXML
    private void clearForm() {
        txtTenBacSi.clear();
        txtChuyenKhoa.clear();
        txtSoDienThoai.clear();
        txtEmail.clear();
    }

    public void loadBieuDoSoBacSi(Map<String, Integer> soBacSiMoiKhoa) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Khoa");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số bác sĩ");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Số bác sĩ theo chuyên khoa");
        barChart.setPrefWidth(paneBieuDo.getPrefWidth());
        barChart.setPrefHeight(paneBieuDo.getPrefHeight());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Bác sĩ");

        for (Map.Entry<String, Integer> entry : soBacSiMoiKhoa.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);

        paneBieuDo.getChildren().clear();
        paneBieuDo.getChildren().add(barChart);
    }

    public void loadDanhSachBacSi() {
        danhSachBacSi = bacSiDAO.getAll();
        hienThiBacSiBangCard();
    }

    private void hienThiDanhSachBacSi(List<BacSi> list) {
        flowCardBacSi.getChildren().clear();

        for (BacSi bacSi : list) {
            VBox card = taoCardBacSi(bacSi);
            flowCardBacSi.getChildren().add(card);
        }
    }

    public void loadBacSiForEdit(BacSi bacSi) {
        this.currentEditingBacSi = bacSi;
        txtTenBacSi.setText(bacSi.getTenBacSi());
        txtChuyenKhoa.setText(bacSi.getChuyenKhoa());
        txtSoDienThoai.setText(bacSi.getSoDienThoai());
        txtEmail.setText(bacSi.getEmail());
    }

    private void hienThiBacSiBangCard() {
        flowCardBacSi.getChildren().clear();

        for (BacSi bacSi : danhSachBacSi) {
            VBox card = taoCardBacSi(bacSi);
            flowCardBacSi.getChildren().add(card);
        }
    }

    private VBox taoCardBacSi(BacSi bacSi) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: #c1f7cf; -fx-padding: 10px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-cursor: hand;");
        card.setPrefWidth(250);

        Label lblTen = new Label("👨‍⚕️ " + bacSi.getTenBacSi());
        lblTen.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label lblChuyenKhoa = new Label("🩺 " + bacSi.getChuyenKhoa());
        Label lblSDT = new Label("📞 " + bacSi.getSoDienThoai());

        card.getChildren().addAll(lblTen, lblChuyenKhoa, lblSDT);

        card.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                hienThiThongTinChiTiet(bacSi);
            }
        });
        return card;
    }

    private void hienThiThongTinChiTiet(BacSi bacSi) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/privateclinicms/DoctorDetailView.fxml"));
            Parent root = loader.load();

            DoctorDetailController controller = loader.getController();
            controller.setDoctorController(this);
            Stage stage = new Stage();
            controller.setDialogStage(stage);
            controller.setBacSi(bacSi);

            stage.setTitle("Thông tin chi tiết bác sĩ");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
