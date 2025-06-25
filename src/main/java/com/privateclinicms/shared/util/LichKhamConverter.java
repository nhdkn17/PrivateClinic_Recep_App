package com.privateclinicms.shared.util;

import com.privateclinicms.shared.model.LichKham;
import com.privateclinicms.shared.model.LichKhamModel;

import java.sql.Timestamp;

public class LichKhamConverter {

    public static LichKhamModel toModel(LichKham lichKham, String tenBenhNhan, String tenBacSi) {
        return new LichKhamModel(
                lichKham.getMaLichKham(),
                tenBenhNhan,
                tenBacSi,
                lichKham.getGioBatDau().toLocalDateTime(),
                lichKham.getTrangThai(),
                lichKham.getGhiChu()
        );
    }

    public static LichKham fromModel(LichKhamModel model, int maBenhNhan, int maBacSi) {
        return new LichKham(
                model.getMaLichKham(),
                maBenhNhan,
                maBacSi,
                Timestamp.valueOf(model.getGioBatDau()),
                model.getTrangThai(),
                model.getGhiChu()
        );
    }
}
