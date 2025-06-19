package com.privateclinicms.util;

import com.privateclinicms.model.BenhNhan;
import com.privateclinicms.model.Thuoc;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class XMLExporter {
    public void xuatToaThuocXML(BenhNhan benhNhan, List<Thuoc> dsThuoc) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("ToaThuoc");
            root.setAttribute("maBenhNhan", String.valueOf(benhNhan.getMaBenhNhan()));
            root.setAttribute("ngayKeDon", LocalDate.now().toString());
            doc.appendChild(root);

            Element thongTin = doc.createElement("ThongTinBenhNhan");
            root.appendChild(thongTin);

            // Thêm các thẻ con
            String[] tags = {"TenBenhNhan", "NgaySinh", "GioiTinh", "SoDienThoai", "Email", "DiaChi"};
            String[] values = {
                    benhNhan.getTenBenhNhan(),
                    benhNhan.getNgaySinh().toString(),
                    benhNhan.getGioiTinh(),
                    benhNhan.getSoDienThoai(),
                    benhNhan.getEmail(),
                    benhNhan.getDiaChi()
            };

            for (int i = 0; i < tags.length; i++) {
                Element el = doc.createElement(tags[i]);
                el.setTextContent(values[i]);
                thongTin.appendChild(el);
            }

            Element dsChiTiet = doc.createElement("DanhSachChiTiet");
            root.appendChild(dsChiTiet);

            for (Thuoc thuoc : dsThuoc) {
                Element chiTiet = doc.createElement("ChiTiet");
                dsChiTiet.appendChild(chiTiet);

                chiTiet.appendChild(createElement(doc, "MaThuoc", String.valueOf(thuoc.getMaThuoc())));
                chiTiet.appendChild(createElement(doc, "TenThuoc", thuoc.getTenThuoc()));
                chiTiet.appendChild(createElement(doc, "DonVi", thuoc.getDonVi()));
                chiTiet.appendChild(createElement(doc, "SoLuong", String.valueOf(new Random().nextInt(3) + 1)));
                chiTiet.appendChild(createElement(doc, "Gia", String.valueOf(thuoc.getGia())));
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            File outputFile = new File("D:\\DoAnCoSo\\DACS1\\ToaThuoc\\toathuoc_BN" + benhNhan.getMaBenhNhan() + ".xml");
            StreamResult result = new StreamResult(outputFile);
            t.transform(source, result);

            System.out.println("Xuất file toa thuốc thành công: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Element createElement(Document doc, String name, String value) {
        Element el = doc.createElement(name);
        el.setTextContent(value);
        return el;
    }
}
