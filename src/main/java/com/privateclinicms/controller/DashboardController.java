package com.privateclinicms.controller;

import com.privateclinicms.controller.medicalThread.AddPatientController;
import com.privateclinicms.controller.medicalThread.PatientThreadListener;
import com.privateclinicms.controller.other.Dialog;
import com.privateclinicms.dao.BenhNhanDAO;
import com.privateclinicms.dao.DashboardService;
import com.privateclinicms.dao.LichKhamDAO;
import com.privateclinicms.dao.ThuocDAO;
import com.privateclinicms.model.BenhNhan;
import com.privateclinicms.model.LichKhamModel;
import com.privateclinicms.model.Thuoc;
import com.privateclinicms.util.JDBCUtil;
import com.privateclinicms.util.XMLExporter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class DashboardController implements PatientThreadListener {
    @FXML
    private Label lblSoBenhNhan;
    @FXML
    private Label lblSoLichHen;
    @FXML
    private Label lblSoDonThuoc;
    @FXML
    private Label lblDoanhThu;
    @FXML
    private TableView<LichKhamModel> tableAppointments;
    @FXML
    private DatePicker datePickerLichHen;
    @FXML
    private TableView<LichKhamModel> tableLichHen;
    @FXML
    private TableColumn<LichKhamModel, Integer> colMaLichKham;
    @FXML
    private TableColumn<LichKhamModel, String> colTenBenhNhan;
    @FXML
    private TableColumn<LichKhamModel, String> colTenBacSi;
    @FXML
    private TableColumn<LichKhamModel, String> colNgayKham;
    @FXML
    private TableColumn<LichKhamModel, String> colTrangThai;
    @FXML
    private ComboBox<Integer> cbThang;
    @FXML
    private ComboBox<Integer> cbNam;
    @FXML
    private Pane paneBieuDo;
    @FXML
    private Button btnBatDauKham;
    @FXML
    private Button btnHuyKham;

    private final Map<Integer, Thread> threadMap = new HashMap<>();
    private final ObservableList<String> ongoingPatients = FXCollections.observableArrayList();

    @FXML
    private ListView<String> lstThreads;


    private DashboardService dashboardService;
    private LichKhamDAO lichKhamDAO;
    private final Map<Integer, Thread> patientThreads = new HashMap<>();
    private BenhNhanDAO benhNhanDAO;
    private ThuocDAO thuocDAO;
    private XMLExporter exporter;

    public void initialize() {
        dashboardService = new DashboardService(JDBCUtil.getConnection());
        lichKhamDAO = new LichKhamDAO();
        benhNhanDAO = new BenhNhanDAO();
        thuocDAO = new ThuocDAO();
        exporter = new XMLExporter();
        loadDashboardData();

        colMaLichKham.setCellValueFactory(new PropertyValueFactory<>("maLichKham"));
        colTenBenhNhan.setCellValueFactory(new PropertyValueFactory<>("tenBenhNhan"));
        colTenBacSi.setCellValueFactory(new PropertyValueFactory<>("tenBacSi"));
        colNgayKham.setCellValueFactory(new PropertyValueFactory<>("gioBatDau"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        datePickerLichHen.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                loadAppointmentsByDate(newDate);
            }
        });

        datePickerLichHen.setValue(LocalDate.now());
        loadAppointmentsByDate(LocalDate.now());

        cbThang.setItems(FXCollections.observableArrayList(IntStream.rangeClosed(1, 12).boxed().toList()));

        int currentYear = LocalDate.now().getYear();
        List<Integer> years = IntStream.rangeClosed(currentYear - 5, currentYear).boxed().toList();
        cbNam.setItems(FXCollections.observableArrayList(years));

        cbThang.setValue(LocalDate.now().getMonthValue());
        cbNam.setValue(currentYear);

        handleXemDoanhThuTheoThang();

        lstThreads.setItems(ongoingPatients);
        lstThreads.setOnMouseClicked(event -> {
            String selectedItem = lstThreads.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                int maBenhNhan = extractMaBenhNhan(selectedItem);
                if (maBenhNhan != -1) {
                    showPatientDetail(maBenhNhan);
                }
            }
        });

    }

    public void loadDashboardData() {
        Connection conn = null;
        try {
            conn = JDBCUtil.getConnection();
            DashboardService dashboardService = new DashboardService(conn);

            lblSoBenhNhan.setText(String.valueOf(dashboardService.getSoBenhNhanHomNay()));
            lblSoLichHen.setText(String.valueOf(dashboardService.getSoLichHenHomNay()));
            lblSoDonThuoc.setText(String.valueOf(dashboardService.getSoDonThuocHomNay()));

            String doanhThu = dashboardService.getFormattedDoanhThuHomNay();
            lblDoanhThu.setText(doanhThu != null ? doanhThu + " VNĐ" : "0 VNĐ");

            loadAppointmentsToday();
        } catch (SQLException e) {
            e.printStackTrace();
            if (!isInAnimation()) {
                Dialog.showNotice("Lỗi", "Không thể tải dữ liệu từ Database", false);
            }
            resetUI();
        } finally {
            if (conn != null) {
                JDBCUtil.closeConnection(conn);
            }
        }
    }

    private void resetUI() {
        lblSoBenhNhan.setText("0");
        lblSoLichHen.setText("0");
        lblSoDonThuoc.setText("0");
        lblDoanhThu.setText("0 VND");
    }

    private boolean isInAnimation() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().contains("Animation") ||
                    element.getClassName().contains("Timeline")) {
                return true;
            }
        }
        return false;
    }

    private void loadAppointmentsToday() {
        try {
            List<LichKhamModel> appointments = lichKhamDAO.getLichKhamHomNay();

            ObservableList<LichKhamModel> data = FXCollections.observableArrayList(appointments);
            tableAppointments.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
            Dialog.showNotice("Lỗi", "Không thể tải danh sách lịch hẹn", false);
        }
    }

    private void loadAppointmentsByDate(LocalDate selectedDate) {
        try {
            List<LichKhamModel> appointments = lichKhamDAO.getLichKhamTheoNgay(selectedDate);

            ObservableList<LichKhamModel> data = FXCollections.observableArrayList(appointments);
            tableAppointments.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.showNotice("Lỗi", "Không thể tải danh sách lịch hẹn theo ngày", false);
        }
    }

    @FXML
    private void handleXemDoanhThuTheoThang() {
        Integer thang = cbThang.getValue();
        Integer nam = cbNam.getValue();

        if (thang != null && nam != null) {
            try {
                Map<Integer, BigDecimal> map = dashboardService.getTongTienTheoNgayTrongThang(thang, nam);

                paneBieuDo.getChildren().clear();

                YearMonth yearMonth = YearMonth.of(nam, thang);
                int daysInMonth = yearMonth.lengthOfMonth();

                NumberAxis xAxis = new NumberAxis(1, daysInMonth, 1);
                NumberAxis yAxis = new NumberAxis();
                xAxis.setLabel("Ngày");
                yAxis.setLabel("Doanh thu (VNĐ)");

                LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
                lineChart.setTitle("Doanh thu tháng " + thang + "/" + nam);
                lineChart.setLegendVisible(false);
                lineChart.setPrefSize(paneBieuDo.getPrefWidth(), paneBieuDo.getPrefHeight());

                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                for (Map.Entry<Integer, BigDecimal> entry : map.entrySet()) {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }

                lineChart.getData().add(series);
                paneBieuDo.getChildren().add(lineChart);

            } catch (SQLException e) {
                e.printStackTrace();
                Dialog.showNotice("Lỗi", "Không thể tải dữ liệu biểu đồ doanh thu.", false);
            }
        }
    }

    @FXML
    private void handleMoThemBenhNhan() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/privateclinicms/MedicalThread/AddPatient.fxml"));
            Parent root = loader.load();

            AddPatientController controller = loader.getController();
            controller.setDashboardController(this);

            Stage stage = new Stage();
            stage.setTitle("Thêm bệnh nhân");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPatientThread(int maBenhNhan, Thread thread) {
        threadMap.put(maBenhNhan, thread);
        String displayText = "Bệnh nhân #" + maBenhNhan + " đang được khám";

        ongoingPatients.add(displayText);
    }

    public void removePatientThread(int maBenhNhan) {
        patientThreads.remove(maBenhNhan);
        lstThreads.getItems().removeIf(item -> item.contains("#" + maBenhNhan));
    }

    @Override
    public void onPatientThreadComplete(int maBenhNhan) {
        Platform.runLater(() -> {
            threadMap.remove(maBenhNhan);
            ongoingPatients.removeIf(s -> s.contains("#" + maBenhNhan));
            BenhNhan bn = benhNhanDAO.getById(maBenhNhan);

            Dialog.showNotice("Thành công", "Đã khám xong bệnh nhân " + maBenhNhan, true);
            loadDashboardData();

            List<Thuoc> thuocNgauNhien = null;
            try {
                thuocNgauNhien = thuocDAO.getRandomThuoc(4);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            exporter.xuatToaThuocXML(bn, thuocNgauNhien);
        });
    }

    private int extractMaBenhNhan(String text) {
        try {
            String numberPart = text.replaceAll("\\D+", "");
            return Integer.parseInt(numberPart);
        } catch (Exception e) {
            return -1;
        }
    }
    private void showPatientDetail(int maBenhNhan) {
        try {
            BenhNhanDAO dao = new BenhNhanDAO();
            BenhNhan bn = dao.getById(maBenhNhan);
            if (bn != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông tin bệnh nhân");
                alert.setHeaderText("Chi tiết bệnh nhân #" + maBenhNhan);
                alert.setContentText(
                        "Tên: " + bn.getTenBenhNhan() + "\n" +
                        "Ngày sinh: " + bn.getNgaySinh() + "\n" +
                        "Giới tính: " + bn.getGioiTinh() + "\n" +
                        "SĐT: " + bn.getSoDienThoai() + "\n" +
                        "Email: " + bn.getEmail() + "\n" +
                        "Địa chỉ: " + bn.getDiaChi() + "\n" +
                        "Ngày khám: " + bn.getNgayKham()
                );
                alert.showAndWait();
            } else {
                Dialog.showNotice("Lỗi", "Không tìm thấy bệnh nhân!", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.showNotice("Lỗi", "Lỗi khi tải thông tin bệnh nhân!", false);
        }
    }

    @FXML
    private void handleHuyKham() {
        String selected = lstThreads.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Dialog.showNotice("Thông báo", "Vui lòng chọn bệnh nhân để hủy khám", false);
            return;
        }

        int maBenhNhan = extractMaBenhNhan(selected);
        if (threadMap.containsKey(maBenhNhan)) {
            Thread thread = threadMap.get(maBenhNhan);
            thread.interrupt();
            threadMap.remove(maBenhNhan);
            ongoingPatients.removeIf(s -> s.contains("#" + maBenhNhan));
            lstThreads.getItems().removeIf(s -> s.contains("ID: " + maBenhNhan));
            Dialog.showNotice("Thành công", "Đã hủy khám bệnh nhân #" + maBenhNhan, true);
        } else {
            Dialog.showNotice("Lỗi", "Không tìm thấy luồng khám cho bệnh nhân này", false);
        }
    }
}
