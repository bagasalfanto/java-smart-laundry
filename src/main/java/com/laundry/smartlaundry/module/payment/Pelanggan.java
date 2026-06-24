package com.laundry.smartlaundry.module.payment;

/**
 * Class Pelanggan merepresentasikan data pelanggan laundry.
 *
 * <p>Yang paling penting bagi modul Payment & Billing adalah atribut
 * {@code member}. Kalau pelanggan adalah member, nanti dia berhak
 * mendapatkan diskon otomatis saat menghitung total biaya.</p>
 *
 * <p>Class ini sengaja dibuat sederhana agar mudah dipahami oleh
 * teman-teman yang baru belajar Pemrograman Berorientasi Objek (PBO).</p>
 */
public class Pelanggan {

    // --- Atribut (data milik setiap objek Pelanggan) ---
    private String idMember;   // ID unik pelanggan, contoh: "MBR-01"
    private String nama;       // nama pelanggan, contoh: "Andi"
    private String noTelp;     // nomor telepon, dipakai untuk mencari pelanggan
    private boolean member;    // true = member (dapat diskon), false = pelanggan biasa


    /**
     * Constructor untuk membuat objek Pelanggan baru.
     *
     * @param idMember ID unik pelanggan (contoh: "MBR-01")
     * @param nama     nama pelanggan
     * @param noTelp   nomor telepon pelanggan
     * @param member   status keanggotaan: true jika member, false jika bukan
     */
    public Pelanggan(String idMember, String nama, String noTelp, boolean member) {
        this.idMember = idMember;
        this.nama = nama;
        this.noTelp = noTelp;
        this.member = member;

    }

    // ==========================================
    // METHOD KHUSUS
    // ==========================================

    /**
     * Mengecek apakah pelanggan ini seorang member atau bukan.
     * Method inilah yang dipakai oleh proses billing untuk memutuskan
     * apakah diskon member perlu diberikan.
     *
     * @return true jika pelanggan adalah member
     */
    public boolean cekStatusMember() {
        return member;
    }

    // ==========================================
    // GETTER & SETTER (cara aman mengakses data privat)
    // ==========================================

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
