package com.privateclinicms.util;

import com.privateclinicms.dao.ThuocDAO;
import com.privateclinicms.model.MedicineRow;
import com.privateclinicms.model.Thuoc;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class XMLToaThuocLoader {
    public static XMLToaThuocResult loadFullInfoFromFile(File xmlFile) {
        List<MedicineRow> list = new ArrayList<>();
        ThuocDAO thuocDAO = new ThuocDAO();
        int maBenhNhan = -1;
        LocalDate ngayKeDon = null;
        String tenBenhNhan = "";

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            Element root = doc.getDocumentElement();
            maBenhNhan = Integer.parseInt(root.getAttribute("maBenhNhan"));
            ngayKeDon = LocalDate.parse(root.getAttribute("ngayKeDon"));

            Element thongTin = (Element) root.getElementsByTagName("ThongTinBenhNhan").item(0);
            tenBenhNhan = getTagValue(thongTin, "TenBenhNhan");

            NodeList chiTietList = root.getElementsByTagName("ChiTiet");
            for (int i = 0; i < chiTietList.getLength(); i++) {
                Element thuocElem = (Element) chiTietList.item(i);
                int maThuoc = Integer.parseInt(getTagValue(thuocElem, "MaThuoc"));
                String tenThuoc = getTagValue(thuocElem, "TenThuoc");
                String donVi = getTagValue(thuocElem, "DonVi");
                int soLuongKe = Integer.parseInt(getTagValue(thuocElem, "SoLuong"));
                double gia = Double.parseDouble(getTagValue(thuocElem, "Gia"));

                Thuoc thuocFromDB = thuocDAO.getById(maThuoc);
                int soLuongTon = (thuocFromDB != null) ? thuocFromDB.getSoLuongTon() : 0;

                MedicineRow row = new MedicineRow(maThuoc, tenThuoc, donVi, soLuongKe, soLuongTon, gia);
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new XMLToaThuocResult(maBenhNhan, ngayKeDon, tenBenhNhan, list);
    }

    private static String getTagValue(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() > 0 && list.item(0).getFirstChild() != null) {
            return list.item(0).getFirstChild().getNodeValue();
        }
        return "";
    }
}

