package com.privateclinicms.shared.model;

public class NhanVien {
    private int maNhanVien;
    private String hoTen;
    private String vaiTro;
    private String soDienThoai;
    private String email;

    public NhanVien(int maNhanVien, String hoTen, String vaiTro, String soDienThoai, String email) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.vaiTro = vaiTro;
        this.soDienThoai = soDienThoai;
        this.email = email;
    }

    public NhanVien(String hoTen, String vaiTro, String soDienThoai, String email) {
        this.hoTen = hoTen;
        this.vaiTro = vaiTro;
        this.soDienThoai = soDienThoai;
        this.email = email;
    }

    public NhanVien() {
    }

    public int getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(int maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "NhanVien{" +
                "maNhanVien=" + maNhanVien +
                ", hoTen='" + hoTen + '\'' +
                ", vaiTro='" + vaiTro + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
