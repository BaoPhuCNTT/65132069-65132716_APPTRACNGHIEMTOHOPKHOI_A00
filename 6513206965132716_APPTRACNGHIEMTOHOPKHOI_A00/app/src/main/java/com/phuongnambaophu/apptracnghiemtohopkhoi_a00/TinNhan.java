package com.phuongnambaophu.apptracnghiemtohopkhoi_a00;

public class TinNhan {

    private String mssv_Nam = "65132069";
    private String mssv_Phu = "65132716";

    private String noiDung;
    private boolean isNguoiDung;

    public TinNhan(String noiDung, boolean isNguoiDung) {
        this.noiDung = noiDung;
        this.isNguoiDung = isNguoiDung;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public boolean isNguoiDung() {
        return isNguoiDung;
    }
}