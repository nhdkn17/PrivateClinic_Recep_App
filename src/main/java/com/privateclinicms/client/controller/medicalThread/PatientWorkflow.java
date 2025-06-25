package com.privateclinicms.client.controller.medicalThread;

import com.privateclinicms.client.controller.DashboardController;
import com.privateclinicms.client.utils.XMLExporter;
import com.privateclinicms.shared.dao.BenhNhanDAO;
import com.privateclinicms.shared.dao.ThuocDAO;

public class PatientWorkflow implements Runnable {
    private final int maBenhNhan;
    private final PatientThreadListener listener;

    BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    ThuocDAO thuocDAO = new ThuocDAO();
    XMLExporter exporter = new XMLExporter();
    DashboardController dashboardController = new DashboardController();

    public PatientWorkflow(int maBenhNhan, PatientThreadListener listener) {
        this.maBenhNhan = maBenhNhan;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            System.out.println("🔄 Bắt đầu quy trình khám cho bệnh nhân ID: " + maBenhNhan);

            Thread.sleep(5000);
            System.out.println("✅ Tiếp nhận xong cho bệnh nhân " + maBenhNhan);

            Thread.sleep(5000);
            System.out.println("✅ Đo sinh hiệu xong cho bệnh nhân " + maBenhNhan);

            Thread.sleep(5000);
            System.out.println("✅ Khám bác sĩ xong cho bệnh nhân " + maBenhNhan);

            Thread.sleep(5000);
            System.out.println("✅ Lấy thuốc xong cho bệnh nhân " + maBenhNhan);

            System.out.println("🎉 Hoàn tất quy trình cho bệnh nhân: " + maBenhNhan);
        } catch (InterruptedException e) {
            System.out.println("⚠️ Đã hủy khám cho bệnh nhân: " + maBenhNhan);
        } finally {
            if (listener != null) {
                listener.onPatientThreadComplete(maBenhNhan);
            }
        }
    }
}
