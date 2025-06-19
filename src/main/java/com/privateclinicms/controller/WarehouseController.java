package com.privateclinicms.controller;

import com.privateclinicms.controller.other.Dialog;
import com.privateclinicms.dao.ThuocDAO;
import com.privateclinicms.model.Thuoc;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class WarehouseController {
    @FXML
    private FlowPane cardContainer;
    @FXML
    private TextField txtSearch;
    @FXML
    private Label lblTongSoLuong;
    @FXML
    private Button btnAdd, btnEdit, btnDelete, btnRefresh;
    @FXML
    private ToggleButton btnLowStock;
    @FXML
    private TextField txtTenThuoc;
    @FXML
    private TextField txtLoaiThuoc;
    @FXML
    private TextField txtDonVi;
    @FXML
    private TextField txtGia;
    @FXML
    private TextField txtSoLuongTon;

    private ThuocDAO thuocDAO = new ThuocDAO();
    private List<Thuoc> currentList;
    private Thuoc selectedThuoc = null;

    @FXML
    public void initialize() {
        currentList = thuocDAO.getAll();
        loadMedicineCards(currentList);
        updateTotalLabel();
    }

    public void loadMedicineCards(List<Thuoc> danhSachThuoc) {
        cardContainer.getChildren().clear();
        for (Thuoc thuoc : danhSachThuoc) {
            HBox card = createMedicineCard(thuoc);
            cardContainer.getChildren().add(card);
        }
    }

    private HBox createMedicineCard(Thuoc thuoc) {
        HBox card = new HBox(10);
        card.setStyle("-fx-background-color: #d5f2f7; -fx-border-color: #ccc; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10");
        card.setPrefWidth(204);
        card.setMinHeight(70);

        Label nameLable = new Label(thuoc.getTenThuoc());
        nameLable.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label quantityLabel = new Label("Số lượng tồn: " + thuoc.getSoLuongTon());

        VBox info = new VBox(5);
        info.getChildren().addAll(
                nameLable, quantityLabel
        );

        Button btnSelect = new Button("Chi tiết");
//        btnSelect.setStyle("-fx-background-color: transparent;");
//
//        Image icon = new Image(getClass().getResourceAsStream("/image/details_icon.png"));
//        ImageView iconView = new ImageView(icon);
//        iconView.setFitHeight(16);
//        iconView.setFitWidth(16);
//
//        btnSelect.setGraphic(iconView);

        btnSelect.setOnAction(e -> showDetails(thuoc));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(info, spacer, btnSelect);
        return card;
    }

    private void showDetails(Thuoc thuoc) {
        selectedThuoc = thuoc;
        txtTenThuoc.setText(thuoc.getTenThuoc());
        txtLoaiThuoc.setText(thuoc.getLoaiThuoc());
        txtDonVi.setText(thuoc.getDonVi());
        txtGia.setText(String.valueOf(thuoc.getGia()));
        txtSoLuongTon.setText(String.valueOf(thuoc.getSoLuongTon()));
    }

    private void updateTotalLabel() {
        int total = currentList.stream().mapToInt(Thuoc::getSoLuongTon).sum();
        lblTongSoLuong.setText("Tổng số lượng: " + total);
    }

    @FXML
    public void onSearch() {
        String keyword = txtSearch.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            loadMedicineCards(currentList);
            return;
        }

        List<Thuoc> filteredList = new ArrayList<>();
        for (Thuoc thuoc : currentList) {
            if (thuoc.getTenThuoc().toLowerCase().contains(keyword) ||
                thuoc.getLoaiThuoc().toLowerCase().contains(keyword)) {
                filteredList.add(thuoc);
            }
        }
        loadMedicineCards(filteredList);
    }

    @FXML
    public void onRefresh() {
        clearDetails();
    }

    private void clearDetails() {
        selectedThuoc = null;
        txtTenThuoc.clear();
        txtLoaiThuoc.clear();
        txtDonVi.clear();
        txtGia.clear();
        txtSoLuongTon.clear();
    }

    @FXML
    public void onFilterLowStock() {
        if (btnLowStock.isSelected()) {
            currentList = thuocDAO.getThuocSapHet();
            loadMedicineCards(currentList);
        } else {
            currentList = thuocDAO.getAll();
            loadMedicineCards(currentList);
        }
        updateTotalLabel();
    }

    @FXML
    public void onAdd() {
        try {
            String ten = txtTenThuoc.getText().trim();
            String loai = txtLoaiThuoc.getText().trim();
            String donVi = txtDonVi.getText().trim();
            double gia = Double.parseDouble(txtGia.getText().trim());
            int soLuong = Integer.parseInt(txtSoLuongTon.getText().trim());

            if (ten.isEmpty() || loai.isEmpty() || donVi.isEmpty()) {
                Dialog.showNotice("Lỗi", "Vui lòng nhập đầy đủ thông tin thuốc.", false);
                return;
            }

            Thuoc newThuoc = new Thuoc();
            newThuoc.setTenThuoc(ten);
            newThuoc.setLoaiThuoc(loai);
            newThuoc.setDonVi(donVi);
            newThuoc.setGia(gia);
            newThuoc.setSoLuongTon(soLuong);

            thuocDAO.add(newThuoc);
            currentList = thuocDAO.getAll();
            loadMedicineCards(currentList);
            updateTotalLabel();
            clearDetails();
            Dialog.showNotice("Thành công", "Đã thêm thành công!", true);
        } catch (NumberFormatException e) {
            Dialog.showNotice("Lỗi", "Giá hoặc số lượng tồn không hợp lệ!", false);
        } catch (Exception e) {
            Dialog.showNotice("Lỗi", "Không thể thêm thuốc!", false);
        }
    }

    @FXML
    public void onEdit() {
        if (selectedThuoc == null) {
            Dialog.showNotice("Lỗi", "Vui lòng chọn thuốc để sửa!", false);
            return;
        }

        try {
            String ten = txtTenThuoc.getText().trim();
            String loai = txtLoaiThuoc.getText().trim();
            String donVi = txtDonVi.getText().trim();
            double gia = Double.parseDouble(txtGia.getText().trim());
            int soLuong = Integer.parseInt(txtSoLuongTon.getText().trim());

            if (ten.isEmpty() || loai.isEmpty() || donVi.isEmpty()) {
                Dialog.showNotice("Lỗi", "Vui lòng nhập đầy đủ thông tin thuốc.", false);
                return;
            }

            selectedThuoc.setTenThuoc(ten);
            selectedThuoc.setLoaiThuoc(loai);
            selectedThuoc.setDonVi(donVi);
            selectedThuoc.setGia(gia);
            selectedThuoc.setSoLuongTon(soLuong);

            thuocDAO.update(selectedThuoc);
            currentList = thuocDAO.getAll();
            loadMedicineCards(currentList);
            updateTotalLabel();
            clearDetails();
            Dialog.showNotice("Thành công", "Đã cập nhật thành công!", true);
        } catch (NumberFormatException e) {
            Dialog.showNotice("Lỗi", "Giá hoặc số lượng tồn không hợp lệ!", false);
        } catch (Exception e) {
            Dialog.showNotice("Lỗi", "Không thể cập nhật thuốc!", false);
        }
    }

    @FXML
    public void onDelete() {
        if (selectedThuoc == null) {
            Dialog.showNotice("Lỗi", "Vui lòng chọn thuốc để xóa!", false);
            return;
        }

        try {
            thuocDAO.delete(selectedThuoc.getMaThuoc());
            currentList = thuocDAO.getAll();
            loadMedicineCards(currentList);
            updateTotalLabel();
            clearDetails();
            Dialog.showNotice("Thành công", "Đã xóa thành công!", true);
        } catch (Exception e) {
            Dialog.showNotice("Lỗi", "Không thể xóa thuốc!", false);
        }
    }
}
