package com.laundry.smartlaundry.module.payment;

// Data pelanggan. Yang penting di sini status member-nya,
// soalnya member dapat diskon pas hitung total.
public class Pelanggan {

    private String idMember;   // contoh: "MBR-01"
    private String nama;
    private String noTelp;     // dipakai buat nyari pelanggan
    private boolean member;    // true = member (dapat diskon)

    public Pelanggan(String idMember, String nama, String noTelp, boolean member) {
        this.idMember = idMember;
        this.nama = nama;
        this.noTelp = noTelp;
        this.member = member;
    }

    // true kalau member -> dipakai buat nentuin diskon
    public boolean cekStatusMember() {
        return member;
    }

    // getter & setter
    public String getIdMember() {
        return idMember;
    }

    public void setIdMember(String idMember) {
        this.idMember = idMember;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public boolean isMember() {
        return member;
    }

    public void setMember(boolean member) {
        this.member = member;
    }

}
