package com.phuongnambaophu.apptracnghiemtohopkhoi_a00;

public class CauHoi {

    private String mssv = "65132069";
    private String cau_hoi;
    private String dap_an_a;
    private String dap_an_b;
    private String dap_an_c;
    private String dap_an_d;
    private String dap_an_dung;

    public CauHoi() {
    }

    public CauHoi(String cau_hoi, String dap_an_a, String dap_an_b, String dap_an_c, String dap_an_d, String dap_an_dung) {
        this.cau_hoi = cau_hoi;
        this.dap_an_a = dap_an_a;
        this.dap_an_b = dap_an_b;
        this.dap_an_c = dap_an_c;
        this.dap_an_d = dap_an_d;
        this.dap_an_dung = dap_an_dung;
    }

    public String getCau_hoi() {
        return cau_hoi;
    }

    public void setCau_hoi(String cau_hoi) {
        this.cau_hoi = cau_hoi;
    }

    public String getDap_an_a() {
        return dap_an_a;
    }

    public void setDap_an_a(String dap_an_a) {
        this.dap_an_a = dap_an_a;
    }

    public String getDap_an_b() {
        return dap_an_b;
    }

    public void setDap_an_b(String dap_an_b) {
        this.dap_an_b = dap_an_b;
    }

    public String getDap_an_c() {
        return dap_an_c;
    }

    public void setDap_an_c(String dap_an_c) {
        this.dap_an_c = dap_an_c;
    }

    public String getDap_an_d() {
        return dap_an_d;
    }

    public void setDap_an_d(String dap_an_d) {
        this.dap_an_d = dap_an_d;
    }

    public String getDap_an_dung() {
        return dap_an_dung;
    }

    public void setDap_an_dung(String dap_an_dung) {
        this.dap_an_dung = dap_an_dung;
    }
}