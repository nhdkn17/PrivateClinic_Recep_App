package com.privateclinicms.controller;

import com.privateclinicms.controller.other.Dialog;
import com.privateclinicms.dao.BenhNhanDAO;
import com.privateclinicms.model.BenhNhan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PatientController {
    @FXML
    private TableView<BenhNhan> tableView;
    @FXML
    private TableColumn<BenhNhan, String> colTen;
    @FXML
    private TableColumn<BenhNhan, Date> colNgaySinh;
    @FXML
    private TableColumn<BenhNhan, String> colGioiTinh;
    @FXML
    private TableColumn<BenhNhan, String> colSoDienThoai;
    @FXML
    private TableColumn<BenhNhan, String> colEmail;
    @FXML
    private TableColumn<BenhNhan, String> colDiaChi;
    @FXML
    private TextField txtTen;
    @FXML
    private DatePicker dateNgaySinh;
    @FXML
    private ComboBox<String> comboGioiTinh;
    @FXML
    private TextField txtSoDienThoai;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtDiaChi;
    @FXML
    private TextField txtTimKiem;
    @FXML
    private Pane chartPane;

    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private ObservableList<BenhNhan> benhNhanList;
    private BenhNhan selectedBenhNhan;

    @FXML
    public void initialize() {
        comboGioiTinh.setItems(FXCollections.observableArrayList("Nam", "Nữ"));
        loadBenhNhanData();

        colTen.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTenBenhNhan()));
        colNgaySinh.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getNgaySinh()));
        colGioiTinh.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getGioiTinh()));
        colSoDienThoai.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getSoDienThoai()));
        colEmail.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getEmail()));
        colDiaChi.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDiaChi()));

        tableView.setOnMouseClicked(this::onRowSelected);
    }

    private void loadBenhNhanData() {
        benhNhanList = FXCollections.observableArrayList(benhNhanDAO.getAll());
        tableView.setItems(benhNhanList);
        showBenhNhanChartByNgayKham();
    }

    private void clearForm() {
        txtTen.clear();
        dateNgaySinh.setValue(null);
        comboGioiTinh.setValue(null);
        txtSoDienThoai.clear();
        txtEmail.clear();
        txtDiaChi.clear();
        selectedBenhNhan = null;
    }

    public void onRowSelected(MouseEvent event) {
        selectedBenhNhan = tableView.getSelectionModel().getSelectedItem();
        if (selectedBenhNhan != null) {
            txtTen.setText(selectedBenhNhan.getTenBenhNhan());
            dateNgaySinh.setValue(selectedBenhNhan.getNgaySinh().toLocalDate());
            comboGioiTinh.setValue(selectedBenhNhan.getGioiTinh());
            txtSoDienThoai.setText(selectedBenhNhan.getSoDienThoai());
            txtEmail.setText(selectedBenhNhan.getEmail());
            txtDiaChi.setText(selectedBenhNhan.getDiaChi());
        }
    }

    @FXML
    private void handleAdd() {
        try {
            BenhNhan benhNhan = new BenhNhan();
            benhNhan.setTenBenhNhan(txtTen.getText());
            benhNhan.setNgaySinh(Date.valueOf(dateNgaySinh.getValue()));
            benhNhan.setGioiTinh(comboGioiTinh.getValue());
            benhNhan.setSoDienThoai(txtSoDienThoai.getText());
            benhNhan.setEmail(txtEmail.getText());
            benhNhan.setDiaChi(txtDiaChi.getText());
            benhNhan.setNgayKham(Date.valueOf(LocalDate.now()));

            benhNhanDAO.add(benhNhan);
            Dialog.showNotice("Thành Công", "Đã thêm thành công!", true);
            loadBenhNhanData();
            clearForm();
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.showNotice("Lỗi", "Không thể thêm bệnh nhân!", false);
        }
    }

    @FXML
    private void handleUpdate() {
        try {
            if (selectedBenhNhan != null) {
                selectedBenhNhan.setTenBenhNhan(txtTen.getText());
                selectedBenhNhan.setNgaySinh(Date.valueOf(dateNgaySinh.getValue()));
                selectedBenhNhan.setGioiTinh(comboGioiTinh.getValue());
                selectedBenhNhan.setSoDienThoai(txtSoDienThoai.getText());
                selectedBenhNhan.setEmail(txtEmail.getText());
                selectedBenhNhan.setDiaChi(txtDiaChi.getText());

                benhNhanDAO.update(selectedBenhNhan);
                Dialog.showNotice("Thành Công", "Đã cập nhật thành công!", true);
                loadBenhNhanData();
                clearForm();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.showNotice("Lỗi", "Không thể cập nhật bệnh nhân!", false);
        }
    }

    @FXML
    private void handleDelete() {
        try {
            if (selectedBenhNhan != null) {
                benhNhanDAO.delete(selectedBenhNhan.getMaBenhNhan());

                Dialog.showNotice("Thành công", "Đã xóa thành công!", true);
                loadBenhNhanData();
                clearForm();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.showNotice("Lỗi", "Không thể xóa bệnh nhân!", false);
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void showBenhNhanChartByNgayKham() {
        chartPane.getChildren().clear();

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Ngày khám");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số bệnh nhân");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Thống kê số bệnh nhân theo ngày khám");
        barChart.setLegendVisible(false);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Map<String, Long> countByDate = benhNhanList.stream()
                .filter(bn -> bn.getNgayKham() != null)
                .collect(Collectors.groupingBy(
                        bn -> bn.getNgayKham().toLocalDate().format(formatter),
                        Collectors.counting()
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        countByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()))
                );

        barChart.getData().add(series);
        barChart.setPrefSize(chartPane.getPrefWidth(), chartPane.getPrefHeight());

        chartPane.getChildren().add(barChart);
    }

    @FXML
    private void handleTimKiem() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            loadBenhNhanData();
            return;
        }

        List<BenhNhan> filteredList = new ArrayList<>();
        for (BenhNhan benhNhan : benhNhanList) {
            if (benhNhan.getTenBenhNhan().toLowerCase().contains(keyword)) {
                filteredList.add(benhNhan);
            }
        }
        hienThiDanhSachBenhNhan(filteredList);
    }

    private void hienThiDanhSachBenhNhan(List<BenhNhan> list) {
        benhNhanList = FXCollections.observableArrayList(list);
        tableView.setItems(benhNhanList);
        showBenhNhanChartByNgayKham();
    }
}
