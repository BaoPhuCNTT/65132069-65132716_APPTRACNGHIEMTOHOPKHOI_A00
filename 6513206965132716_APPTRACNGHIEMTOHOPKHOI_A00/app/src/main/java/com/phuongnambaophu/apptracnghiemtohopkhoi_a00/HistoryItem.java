package com.phuongnambaophu.apptracnghiemtohopkhoi_a00;

public class HistoryItem {
    private int id;
    private String monHoc;
    private String ngayGio;
    private String diemSo;
    private String chiTietBaiThi;

    public HistoryItem(int id, String monHoc, String ngayGio, String diemSo, String chiTietBaiThi) {
        this.id = id;
        this.monHoc = monHoc;
        this.ngayGio = ngayGio;
        this.diemSo = diemSo;
        this.chiTietBaiThi = chiTietBaiThi;
    }

    public int getId() { return id; }
    public String getMonHoc() { return monHoc; }
    public String getNgayGio() { return ngayGio; }
    public String getDiemSo() { return diemSo; }
    public String getChiTietBaiThi() { return chiTietBaiThi; }
}