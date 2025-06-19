package com.privateclinicms.controller;

import com.privateclinicms.controller.other.Dialog;
import com.privateclinicms.dao.BenhNhanDAO;
import com.privateclinicms.dao.ChiTietToaThuocDAO;
import com.privateclinicms.dao.ThuocDAO;
import com.privateclinicms.dao.ToaThuocDAO;
import com.privateclinicms.model.ChiTietToaThuoc;
import com.privateclinicms.model.MedicineRow;
import com.privateclinicms.model.ToaThuoc;
import com.privateclinicms.util.XMLToaThuocLoader;
import com.privateclinicms.util.XMLToaThuocResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;

public class MedicineController {
    @FXML
    private TableView<MedicineRow> tableMedicine;
    @FXML
    private TableColumn<MedicineRow, String> colMedicineName;
    @FXML
    private TableColumn<MedicineRow, String> colUnit;
    @FXML
    private TableColumn<MedicineRow, Integer> colQuantityOrdered;
    @FXML
    private TableColumn<MedicineRow, Integer> colQuantityAvailable;
    @FXML
    private TableColumn<MedicineRow, Integer> colQuantityTaken;
    @FXML
    private TableColumn<MedicineRow, Double> colPrice;
    @FXML
    private TableColumn<MedicineRow, Double> colTotalPrice;
    @FXML
    private TextField txtPatientName;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button btnConfirm;
    @FXML
    private Button btnCancel;
    @FXML
    private Label lblTotalPrice;
    @FXML
    private Button btnImportXML;

    private final ObservableList<MedicineRow> medicineList = FXCollections.observableArrayList();
    BenhNhanDAO benhNhanDAO;
    ToaThuocDAO toaThuocDAO;
    ThuocDAO thuocDAO;
    ChiTietToaThuocDAO chiTietToaThuocDAO;

    @FXML
    private void initialize() {
        benhNhanDAO = new BenhNhanDAO();
        toaThuocDAO = new ToaThuocDAO();
        thuocDAO = new ThuocDAO();
        chiTietToaThuocDAO = new ChiTietToaThuocDAO();

        colMedicineName.setCellValueFactory(cellData -> cellData.getValue().tenThuocProperty());
        colUnit.setCellValueFactory(cellData -> cellData.getValue().donViProperty());
        colQuantityOrdered.setCellValueFactory(cellData -> cellData.getValue().soLuongKeProperty().asObject());
        colQuantityAvailable.setCellValueFactory(cellData -> cellData.getValue().soLuongTonProperty().asObject());
        colQuantityTaken.setCellValueFactory(cellData -> cellData.getValue().soLuongLayProperty().asObject());
        colPrice.setCellValueFactory(cellData -> cellData.getValue().giaProperty().asObject());
        colTotalPrice.setCellValueFactory(cellData -> cellData.getValue().thanhTienProperty().asObject());

        tableMedicine.setEditable(true);

        colQuantityTaken.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colQuantityTaken.setOnEditCommit(event -> {
            MedicineRow row = event.getRowValue();
            int newValue = event.getNewValue();

            if (newValue >= 0 && newValue <= row.getSoLuongKe() && newValue <= row.getSoLuongTon()) {
                row.setSoLuongLay(newValue);
                updateTotalPrice();
            } else {
                Dialog.showNotice("Lỗi", "Số lượng lấy vượt mức số lượng trong kho", false);
                tableMedicine.refresh();
            }
        });

        btnImportXML.setOnAction(event -> handleImportFromXML());

        tableMedicine.setItems(medicineList);
    }

    private void updateTotalPrice() {
        double total = 0;
        for (MedicineRow row : medicineList) {
            total += row.getThanhTien();
        }
        lblTotalPrice.setText(String.format("%,.0f VNĐ", total));
    }

    @FXML
    private void handleConfirmMedicineTaking() {
        if (datePicker.getValue() == null) {
            Dialog.showNotice("Lỗi", "Vui lòng chọn ngày lấy thuốc!", false);
            return;
        }
        String tenBenhNhan = txtPatientName.getText().trim();
        if (tenBenhNhan.isEmpty()) {
            Dialog.showNotice("Lỗi", "Chưa nhập tên bệnh nhân!", false);
            return;
        }
        Integer maBenhNhan = benhNhanDAO.getMaBenhNhanByTen(tenBenhNhan);
        if (maBenhNhan == null) {
            Dialog.showNotice("Lỗi", "Không tìm thấy mã bệnh nhân!", false);
            return;
        }

        LocalDate ngayLayThuoc = datePicker.getValue();

        boolean hasMedicineTaken = false;
        for (MedicineRow row : medicineList) {
            if (row.getSoLuongLay() > 0) {
                hasMedicineTaken = true;
                break;
            }
        }

        if (!hasMedicineTaken) {
            Dialog.showNotice("Thông báo", "Chưa có thuốc nào được lấy!", false);
            return;
        }

        ToaThuoc toaThuoc = new ToaThuoc();
        toaThuoc.setMaBenhNhan(maBenhNhan);
        toaThuoc.setNgayLayThuoc(ngayLayThuoc);

        for (MedicineRow row : medicineList) {
            if (row.getSoLuongLay() > 0) {
                ChiTietToaThuoc ct = new ChiTietToaThuoc();
                ct.setMaThuoc(row.getMaThuoc());
                ct.setSoLuong(row.getSoLuongLay());
                ct.setLieuLuong("Uống theo chỉ định");
                ct.setThanhTien(BigDecimal.valueOf(row.getThanhTien()));
                toaThuoc.getChiTietToaThuocList().add(ct);
            }
        }

        int maToaThuoc = toaThuocDAO.add(toaThuoc);
        if (maToaThuoc == -1) {
            Dialog.showNotice("Lỗi", "Không thể thêm toa thuốc!", false);
            return;
        }
        toaThuoc.setMaToaThuoc(maToaThuoc);

        for (ChiTietToaThuoc ct : toaThuoc.getChiTietToaThuocList()) {
            ct.setMaToaThuoc(maToaThuoc);
            chiTietToaThuocDAO.add(ct);
        }

        for (MedicineRow row : medicineList) {
            if (row.getSoLuongLay() > 0) {
                int soLuongMoi = row.getSoLuongTon() - row.getSoLuongLay();
                thuocDAO.capNhatSoLuongTon(row.getMaThuoc(), soLuongMoi);
            }
        }

        Dialog.showNotice("Thành công", "Đã xác nhận lấy thuốc.", true);
        clearForm();
    }

    @FXML
    private void handleCancel() {
        clearForm();
    }

    private void clearForm() {
        txtPatientName.clear();
        datePicker.setValue(null);
        medicineList.clear();
        lblTotalPrice.setText("0 VNĐ");
        tableMedicine.refresh();
    }

    @FXML
    private void handleImportFromXML() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file XML");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            XMLToaThuocResult result = XMLToaThuocLoader.loadFullInfoFromFile(file);

            txtPatientName.setText(result.getTenBenhNhan());

            medicineList.clear();
            medicineList.addAll(result.getChiTietThuoc());
            tableMedicine.setItems(medicineList);
            updateTotalPrice();
        }
    }

    @FXML
    private void handleTakeAllPrescribed() {
        for (MedicineRow row : medicineList) {
            int soLuongKe = row.getSoLuongKe();
            int soLuongTon = row.getSoLuongTon();
            int soLuongLay = Math.min(soLuongKe, soLuongTon);
            row.setSoLuongLay(soLuongLay);
        }
        tableMedicine.refresh();
        updateTotalPrice();
    }
}
