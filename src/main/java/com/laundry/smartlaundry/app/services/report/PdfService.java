package com.laundry.smartlaundry.app.services.report;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import com.laundry.smartlaundry.app.models.Laporan;
import com.laundry.smartlaundry.app.models.Transaksi;
import com.laundry.smartlaundry.app.enums.PaymentStatus;
import com.laundry.smartlaundry.app.repositories.TransaksiRepository;

@Service
public class PdfService {

    private final TransaksiRepository transaksiRepository;

    public PdfService(TransaksiRepository transaksiRepository) {
        this.transaksiRepository = transaksiRepository;
    }

    public byte[] generateLaporanPdf(Laporan laporan) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);

            document.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // Title
            Paragraph title = new Paragraph("Laporan Pendapatan Smart Laundry", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            // Subtitle
            Paragraph subtitle = new Paragraph("Periode: " + laporan.getPeriode() + "\nTanggal: " + laporan.getTanggalMulai() + " s/d " + laporan.getTanggalSelesai(), headerFont);
            subtitle.setAlignment(Paragraph.ALIGN_CENTER);
            subtitle.setSpacingAfter(20f);
            document.add(subtitle);

            // Query transactions
            LocalDateTime start = laporan.getTanggalMulai().atStartOfDay();
            LocalDateTime end = laporan.getTanggalSelesai().atTime(23, 59, 59);
            List<Transaksi> transaksiList = transaksiRepository.findByPaymentStatusAndPaidAtBetween(PaymentStatus.LUNAS, start, end);

            // Table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 3f, 3f, 3f, 2f});

            // Table Headers
            String[] headers = {"No", "Invoice", "Pelanggan", "Layanan", "Total (Rp)"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setPadding(5f);
                table.addCell(cell);
            }

            // Table Rows
            int no = 1;
            for (Transaksi t : transaksiList) {
                table.addCell(new Phrase(String.valueOf(no++), normalFont));
                table.addCell(new Phrase(t.getInvoiceNumber(), normalFont));
                table.addCell(new Phrase(t.getPelanggan().getNama(), normalFont));
                table.addCell(new Phrase(t.getLayanan().getNamaPaket(), normalFont));
                table.addCell(new Phrase(t.getTotalBayar().toString(), normalFont));
            }

            document.add(table);

            // Total Summary
            Paragraph total = new Paragraph("Total Pendapatan: Rp " + laporan.getTotalPendapatan(), headerFont);
            total.setAlignment(Paragraph.ALIGN_RIGHT);
            total.setSpacingBefore(20f);
            document.add(total);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}
