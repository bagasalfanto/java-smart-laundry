CREATE TABLE transaksi (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    pelanggan_id BIGINT NOT NULL,
    layanan_id BIGINT NOT NULL,
    staff_id BIGINT NOT NULL,
    berat DECIMAL(8, 2) NOT NULL,
    subtotal DECIMAL(12, 2) NOT NULL,
    diskon DECIMAL(12, 2) NOT NULL DEFAULT 0,
    total_bayar DECIMAL(12, 2) NOT NULL,
    order_status VARCHAR(20) NOT NULL DEFAULT 'ANTRIAN',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'BELUM_LUNAS',
    tanggal_masuk DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tanggal_selesai DATETIME NULL,
    paid_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaksi_pelanggan
        FOREIGN KEY (pelanggan_id) REFERENCES pelanggan(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_transaksi_layanan
        FOREIGN KEY (layanan_id) REFERENCES layanan(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_transaksi_staff
        FOREIGN KEY (staff_id) REFERENCES users(id)
        ON DELETE RESTRICT,
    CONSTRAINT chk_transaksi_berat CHECK (berat > 0),
    CONSTRAINT chk_transaksi_subtotal CHECK (subtotal >= 0),
    CONSTRAINT chk_transaksi_diskon CHECK (diskon >= 0),
    CONSTRAINT chk_transaksi_total CHECK (total_bayar >= 0),
    CONSTRAINT chk_transaksi_order_status CHECK (order_status IN ('ANTRIAN', 'PROSES', 'SELESAI')),
    CONSTRAINT chk_transaksi_payment_status CHECK (payment_status IN ('BELUM_LUNAS', 'LUNAS'))
);

CREATE INDEX idx_transaksi_order_status ON transaksi(order_status);
CREATE INDEX idx_transaksi_payment_status ON transaksi(payment_status);
CREATE INDEX idx_transaksi_paid_at ON transaksi(paid_at);
CREATE INDEX idx_transaksi_tanggal_masuk ON transaksi(tanggal_masuk);
