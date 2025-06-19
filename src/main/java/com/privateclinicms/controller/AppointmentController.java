package com.privateclinicms.controller;

import com.privateclinicms.controller.other.Dialog;
import com.privateclinicms.dao.BacSiDAO;
import com.privateclinicms.dao.BenhNhanDAO;
import com.privateclinicms.dao.DashboardService;
import com.privateclinicms.dao.LichKhamDAO;
import com.privateclinicms.model.BacSi;
import com.privateclinicms.model.BenhNhan;
import com.privateclinicms.model.LichKham;
import com.privateclinicms.model.LichKhamModel;
import com.privateclinicms.util.JDBCUtil;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class AppointmentController {
    @FXML
    private GridPane gridHourColumn;
    @FXML
    private GridPane gridDoctors;
    @FXML
    private ScrollPane scrollPaneDoctors;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<BenhNhan> cboBenhNhan;
    @FXML
    private ComboBox<BacSi> cboBacSi;
    @FXML
    private TextField txtDateTimeDetail;
    @FXML
    private ComboBox<String> cboStatus;
    @FXML
    private TextField txtNote;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnClear;

    private static final int GIO_BAT_DAU = 7;
    private static final int GIO_KET_THUC = 17;

    private LichKhamDAO lichKhamDAO;
    private DashboardService dashboardService;
    private LichKham lichKhamDangChon;

    @FXML
    public void initialize() {
        Connection conn = JDBCUtil.getConnection();
        dashboardService = new DashboardService(conn);
        lichKhamDAO = new LichKhamDAO();
        lichKhamDangChon = new LichKham();

        datePicker.setValue(LocalDate.now());
        hienThiLichKhamTheoNgay(LocalDate.now());

        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            hienThiLichKhamTheoNgay(newDate);
        });

        loadDanhSachBenhNhan();
        loadDanhSachBacSi();
    }

    @FXML
    private void handleAdd() {
        try {
            BenhNhan bn = cboBenhNhan.getValue();
            BacSi bs = cboBacSi.getValue();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime gioBatDau = LocalDateTime.parse(txtDateTimeDetail.getText(), formatter);
            String trangThai = cboStatus.getValue();
            String ghiChu = txtNote.getText();

            if (bn == null || bs == null) return;

            LichKham lichMoi = new LichKham(bn.getMaBenhNhan(), bs.getMaBacSi(), Timestamp.valueOf(gioBatDau), trangThai, ghiChu);
            lichKhamDAO.add(lichMoi);
            Dialog.showNotice("Thành công", "Đã thêm thành công!", true);
            clearForm();
            hienThiLichKhamTheoNgay(datePicker.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.showNotice("Lỗi", "Không thể thêm lịch khám!", false);
        }
    }

    @FXML
    private void handleEdit() {
        if (lichKhamDangChon == null) return;

        BenhNhan bn = cboBenhNhan.getValue();
        BacSi bs = cboBacSi.getValue();
        String trangThai = cboStatus.getValue();
        String ghiChu = txtNote.getText();

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime gioBatDau = LocalDateTime.parse(txtDateTimeDetail.getText(), formatter);

            LichKham lichDaSua = new LichKham(
                    lichKhamDangChon.getMaLichKham(),
                    bn.getMaBenhNhan(),
                    bs.getMaBacSi(),
                    Timestamp.valueOf(gioBatDau),
                    trangThai,
                    ghiChu
            );

            lichKhamDAO.update(lichDaSua);
            Dialog.showNotice("Thành công", "Đã cập nhật thành công!", true);
            clearForm();
            hienThiLichKhamTheoNgay(datePicker.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.showNotice("Lỗi", "Không thể cập nhật lịch khám!", false);
        }
    }

    @FXML
    private void handleDelete() {
        try {
            if (lichKhamDangChon == null) return;

            lichKhamDAO.delete(lichKhamDangChon.getMaLichKham());
            Dialog.showNotice("Thành công", "Đã xóa thành công!", true);
            lichKhamDangChon = null;
            clearForm();
            hienThiLichKhamTheoNgay(datePicker.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.showNotice("Lỗi", "không thể xóa lịch khám!", false);
        }
    }

    public void hienThiLichKhamTheoNgay(LocalDate selectedDate) {
        hienThiLichKhamTheoNgay(selectedDate, null);
    }

    public void hienThiLichKhamTheoNgay(LocalDate selectedDate, String keyword) {
        try {
            gridDoctors.getChildren().clear();
            gridDoctors.getRowConstraints().clear();

            final int totalRows = 12;
            final double totalHeight = 554.0;
            final double rowHeight = totalHeight / totalRows;

            for (int i = 0; i < totalRows; i++) {
                var rcHour = new javafx.scene.layout.RowConstraints();
                rcHour.setPrefHeight(rowHeight);
                rcHour.setMinHeight(rowHeight);
                rcHour.setMaxHeight(rowHeight);
                gridHourColumn.getRowConstraints().add(rcHour);

                var rcDoctor = new javafx.scene.layout.RowConstraints();
                rcDoctor.setPrefHeight(rowHeight);
                rcDoctor.setMinHeight(rowHeight);
                rcDoctor.setMaxHeight(rowHeight);
                gridDoctors.getRowConstraints().add(rcDoctor);
            }

            Label headerHour = new Label("Giờ");
            headerHour.setPrefHeight(rowHeight);
            headerHour.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
            gridHourColumn.add(headerHour, 0, 0);

            for (int h = GIO_BAT_DAU; h <= GIO_KET_THUC; h++) {
                Label hourLabel = new Label(String.format("%02dh", h));
                hourLabel.setPadding(new Insets(5));
                hourLabel.setPrefHeight(rowHeight);
                hourLabel.setMaxHeight(rowHeight);
                hourLabel.setMinHeight(rowHeight);
                hourLabel.setStyle("-fx-border-color: #D3D3D3; -fx-border-width: 0 0 1 0; -fx-alignment: center;");
                gridHourColumn.add(hourLabel, 0, h - GIO_BAT_DAU + 1);
            }

            List<LichKhamModel> lichList = lichKhamDAO.getLichKhamTheoNgay(selectedDate);
            if (keyword != null && !keyword.isEmpty()) {
                lichList.removeIf(lich ->
                        !lich.getTenBenhNhan().toLowerCase().contains(keyword.toLowerCase()) &&
                                !lich.getTenBacSi().toLowerCase().contains(keyword.toLowerCase()));
            }

            Map<String, Integer> bacSiToCol = new TreeMap<>();
            int colIndex = 0;
            for (LichKhamModel lich : lichList) {
                if (!bacSiToCol.containsKey(lich.getTenBacSi())) {
                    bacSiToCol.put(lich.getTenBacSi(), colIndex++);
                }
            }

            for (Map.Entry<String, Integer> entry : bacSiToCol.entrySet()) {
                Label bsLabel = new Label(entry.getKey());
                bsLabel.setStyle("-fx-border-color: #D3D3D3; -fx-border-width: 0 0 1 0; -fx-font-weight: bold; -fx-padding: 5; -fx-alignment: center;");
                bsLabel.setPrefHeight(rowHeight);
                bsLabel.setMaxHeight(rowHeight);
                bsLabel.setMinHeight(rowHeight);
                gridDoctors.add(bsLabel, entry.getValue(), 0);
            }

            for (LichKhamModel lich : lichList) {
                int row = lich.getGioBatDau().getHour() - GIO_BAT_DAU + 1;
                int col = bacSiToCol.get(lich.getTenBacSi());

                VBox card = taoCardLichKham(lich);
                card.setPrefHeight(rowHeight - 10);
                gridDoctors.add(card, col, row);
            }
            for (int row = 1; row <= GIO_KET_THUC - GIO_BAT_DAU + 1; row++) {
                for (int col = 0; col < bacSiToCol.size(); col++) {
                    boolean isOccupied = false;
                    for (Node node : gridDoctors.getChildren()) {
                        if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                            isOccupied = true;
                            break;
                        }
                    }
                    if (!isOccupied) {
                        Pane cell = new Pane();
                        cell.setPrefSize(180, 60);
                        cell.setStyle("-fx-border-color: #E0E0E0; -fx-border-width: 0 1 1 0;");
                        gridDoctors.add(cell, col, row);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox taoCardLichKham(LichKhamModel lich) {
        VBox card = new VBox();
        card.setPadding(new Insets(6));
        card.setSpacing(4);
        card.setPrefWidth(180);

        if (Objects.equals(lich.getTrangThai(), "Hoàn thành")) {
            card.setStyle("-fx-background-color: #C8E6C9; -fx-border-color: #B0BEC5; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");
        } else if (Objects.equals(lich.getTrangThai(), "Hủy")) {
            card.setStyle("-fx-background-color: #FFCDD2; -fx-border-color: #B0BEC5; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");
        } else if (Objects.equals(lich.getTrangThai(), "Chờ xác nhận")) {
            card.setStyle("-fx-background-color: #FFF9C4; -fx-border-color: #B0BEC5; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");
        } else card.setStyle("-fx-background-color: #E0F7FA; -fx-border-color: #B0BEC5; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");

        Label tenBN = new Label("Bệnh nhân " + lich.getTenBenhNhan());
        tenBN.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        card.getChildren().addAll(tenBN);

        card.setOnMouseClicked(e -> hienThiChiTiet(lich));
        return card;
    }

    private void hienThiChiTiet(LichKhamModel lich) {
        for (BacSi bs : cboBacSi.getItems()) {
            if (bs.getTenBacSi().equals(lich.getTenBacSi())) {
                cboBacSi.setValue(bs);
                break;
            }
        }
        for (BenhNhan bn : cboBenhNhan.getItems()) {
            if (bn.getTenBenhNhan().equals(lich.getTenBenhNhan())) {
                cboBenhNhan.setValue(bn);
                break;
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = lich.getGioBatDau().format(formatter);
        txtDateTimeDetail.setText(formattedDateTime);
        cboStatus.setValue(lich.getTrangThai());
        txtNote.setText(lich.getGhiChu());

        setLichKhamDangChonTheoModel(lich);

        btnEdit.setDisable(false);
        btnDelete.setDisable(false);
    }

    private void loadDanhSachBenhNhan() {
        List<BenhNhan> list = new BenhNhanDAO().getAll();
        cboBenhNhan.getItems().addAll(list);

        cboBenhNhan.setConverter(new StringConverter<>() {
            @Override
            public String toString(BenhNhan bn) {
                return bn == null ? "" : bn.getTenBenhNhan();
            }
            @Override
            public BenhNhan fromString(String string) {
                return cboBenhNhan.getItems().stream()
                        .filter(bn -> bn.getTenBenhNhan().equals(string))
                        .findFirst().orElse(null);
            }
        });
    }
    private void loadDanhSachBacSi() {
        List<BacSi> list = new BacSiDAO().getAll();
        cboBacSi.getItems().addAll(list);

        cboBacSi.setConverter(new StringConverter<>() {
            @Override
            public String toString(BacSi bs) {
                return bs == null ? "" : bs.getTenBacSi();
            }

            @Override
            public BacSi fromString(String string) {
                return cboBacSi.getItems().stream()
                        .filter(bs -> bs.getTenBacSi().equals(string))
                        .findFirst().orElse(null);
            }
        });
    }

    private void setLichKhamDangChonTheoModel(LichKhamModel model) {
        try {
            List<LichKham> danhSachLich = lichKhamDAO.getLichKhamEntitiesTheoNgay(model.getGioBatDau().toLocalDate());
            for (LichKham lk : danhSachLich) {
                if (lk.getGioBatDau().toLocalDateTime().equals(model.getGioBatDau()) &&
                        lk.getGhiChu().equals(model.getGhiChu())) {
                    lichKhamDangChon = lk;
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void clearForm() {
        cboBacSi.setValue(null);
        cboBenhNhan.setValue(null);
        cboStatus.setValue(null);
        txtDateTimeDetail.clear();
        txtNote.clear();
    }
}
