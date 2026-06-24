-- Mengubah tipe data stok menjadi DECIMAL(10,2) untuk mendukung input desimal
ALTER TABLE inventaris MODIFY COLUMN stok DECIMAL(10,2) NOT NULL DEFAULT 0.00;

