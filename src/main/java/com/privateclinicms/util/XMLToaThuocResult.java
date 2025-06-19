package com.privateclinicms.util;

import com.privateclinicms.model.MedicineRow;

import java.time.LocalDate;
import java.util.List;

public class XMLToaThuocResult {
    private int maBenhNhan;
    private LocalDate ngayKeDon;
    private String tenBenhNhan;
    private List<MedicineRow> chiTietThuoc;

    public XMLToaThuocResult(int maBenhNhan, LocalDate ngayKeDon, String tenBenhNhan, List<MedicineRow> chiTietThuoc) {
        this.maBenhNhan = maBenhNhan;
        this.ngayKeDon = ngayKeDon;
        this.tenBenhNhan = tenBenhNhan;
        this.chiTietThuoc = chiTietThuoc;
    }

    public XMLToaThuocResult(LocalDate ngayKeDon, String tenBenhNhan, List<MedicineRow> chiTietThuoc) {
        this.ngayKeDon = ngayKeDon;
        this.tenBenhNhan = tenBenhNhan;
        this.chiTietThuoc = chiTietThuoc;
    }

    public XMLToaThuocResult() {
    }

    public int getMaBenhNhan() {
        return maBenhNhan;
    }

    public void setMaBenhNhan(int maBenhNhan) {
        this.maBenhNhan = maBenhNhan;
    }

    public LocalDate getNgayKeDon() {
        return ngayKeDon;
    }

    public void setNgayKeDon(LocalDate ngayKeDon) {
        this.ngayKeDon = ngayKeDon;
    }

    public String getTenBenhNhan() {
        return tenBenhNhan;
    }

    public void setTenBenhNhan(String tenBenhNhan) {
        this.tenBenhNhan = tenBenhNhan;
    }

    public List<MedicineRow> getChiTietThuoc() {
        return chiTietThuoc;
    }

    public void setChiTietThuoc(List<MedicineRow> chiTietThuoc) {
        this.chiTietThuoc = chiTietThuoc;
    }
}

