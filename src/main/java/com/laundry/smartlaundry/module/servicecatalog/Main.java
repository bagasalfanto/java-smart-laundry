package com.laundry.smartlaundry.module.servicecatalog;

/**
 * Class Main sederhana untuk mendemonstrasikan fitur Service & Catalog Manager.
 * Menguji fungsionalitas Admin (CRUD) dan Staff (Price Estimator).
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== SISTEM MANAJEMEN OPERASIONAL LAUNDRY DIGITAL ===");
        System.out.println("Modul: Service & Catalog Manager (Bagian: Shellyn)\n");

        ServiceCatalogManager manager = new ServiceCatalogManager();

        // ==========================================
        // SIMULASI AKTOR: ADMIN
        // ==========================================
        System.out.println(">>> SIMULASI LOGIN ADMIN: MENGELOLA KATALOG LAUNDRY");
        
        // 1. CREATE: Tambah Layanan Baru
        Layanan cuciKering = new Layanan("LYN-01", "Cuci Kering", 6000.0, 2);
        Layanan cuciSetrika = new Layanan("LYN-02", "Cuci Setrika", 8000.0, 3);
        Layanan paketExpress = new Layanan("LYN-03", "Express 1 Hari", 12000.0, 1);
        
        manager.tambahLayanan(cuciKering);
        manager.tambahLayanan(cuciSetrika);
        manager.tambahLayanan(paketExpress);

        // 2. READ: Lihat Semua Layanan
        manager.tampilkanSemuaLayanan();

        // 3. UPDATE: Mengubah Harga Layanan (misal bahan baku naik)
        System.out.println("\n[Skenario] Admin menaikkan harga Cuci Setrika karena inflasi.");
        manager.updateLayanan("LYN-02", "Cuci Setrika", 9000.0, 3);
        manager.tampilkanSemuaLayanan();

        // 4. DELETE: Menghapus Layanan
        System.out.println("\n[Skenario] Admin menghapus paket Cuci Kering karena sepi peminat.");
        manager.hapusLayanan("LYN-01");
        manager.tampilkanSemuaLayanan();

        // ==========================================
        // SIMULASI AKTOR: STAFF
        // ==========================================
        System.out.println("\n>>> SIMULASI LOGIN STAFF: MELAYANI PELANGGAN");
        System.out.println("[Skenario] Pelanggan datang membawa 4.5 kg pakaian kotor dan ingin estimasi harga paket Express.");
        
        // Staff menggunakan fitur Price Estimator (Read-Only ke data katalog)
        manager.estimasiTotalHarga("LYN-03", 4.5);
        
        System.out.println("\n[Skenario] Pelanggan lain bertanya estimasi untuk Cuci Setrika seberat 3 kg.");
        manager.estimasiTotalHarga("LYN-02", 3.0);

        System.out.println("\n=== DEMONSTRASI MODUL SELESAI ===");
    }
}
