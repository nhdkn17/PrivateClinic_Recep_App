package com.privateclinicms.shared.model;

public class SectionNhanVien {
    private static NhanVien nhanVien;

    public static NhanVien getNhanVien() {
        return nhanVien;
    }

    public static void setNhanVien(NhanVien nv) {
        nhanVien = nv;
    }

    public static void clear() {
        nhanVien = null;
    }
}
