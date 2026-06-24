ALTER TABLE transaksi DROP CONSTRAINT chk_transaksi_order_status;
ALTER TABLE transaksi ADD CONSTRAINT chk_transaksi_order_status CHECK (order_status IN ('ANTRIAN', 'PROSES_CUCI', 'PROSES_SETRIKA', 'SELESAI'));
