package com.hop.report;

import com.hop.model.PdfExportable;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportUtil {
public static <T extends PdfExportable> void generatePdfReport(
        List<T> dataList,
        String[] headers,
        String reportTitle,
        String outputFilePath,
        String kota,
        String namaDirektur
    ) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(outputFilePath));
        document.open();

        addKopSurat(document);

        Paragraph title = new Paragraph(reportTitle,
                new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(20);
        title.setSpacingAfter(20);
        document.add(title);

        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);

        // Jika ingin set lebar kolom bisa sesuaikan di sini, contoh all sama rata:
        float[] columnWidths = new float[headers.length];
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = 1f;
        }
        table.setWidths(columnWidths);

        addTableHeader(table, headers);

        for (T item : dataList) {
            List<String> row = item.toPdfRow();
            for (String cellValue : row) {
                table.addCell(cellValue);
            }
        }

        document.add(table);

        addFooter(document, kota, namaDirektur);

        document.close();

        // pdf langsung terbuka
        openPdf(outputFilePath);
    }

    private static void addTableHeader(PdfPTable table, String[] headers) {
        Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        BaseColor bgColor = new BaseColor(63, 81, 181);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headFont));
            cell.setBackgroundColor(bgColor);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private static void addKopSurat(Document document) throws Exception {
        URL imageUrl = ReportUtil.class.getClassLoader().getResource("images/kopOK.png");
        if (imageUrl == null) {
            throw new RuntimeException("File gambar kop surat tidak ditemukan di resources/images/kopOK.png");
        }
        byte[] imageBytes = readImageBytes(imageUrl);
        Image kop = Image.getInstance(imageBytes);
        kop.scaleToFit(530, 100);
        kop.setAlignment(Image.ALIGN_CENTER);
        document.add(kop);
    }

    private static void addFooter(Document document, String kota, String namaDirektur) throws DocumentException {
        String tanggal = new SimpleDateFormat("dd MMMM yyyy").format(new Date());

        Paragraph tempatTanggal = new Paragraph(kota + ", " + tanggal,
                new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL));
        tempatTanggal.setAlignment(Element.ALIGN_RIGHT);
        tempatTanggal.setSpacingBefore(30);
        document.add(tempatTanggal);

        Paragraph jabatan = new Paragraph("Direktur",
                new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL));
        jabatan.setAlignment(Element.ALIGN_RIGHT);
        jabatan.setSpacingBefore(5);
        document.add(jabatan);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        Paragraph nama = new Paragraph(namaDirektur,
                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
        nama.setAlignment(Element.ALIGN_RIGHT);
        document.add(nama);
    }

    private static byte[] readImageBytes(URL imageUrl) throws Exception {
        try (var is = imageUrl.openStream(); var baos = new java.io.ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = is.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }
            return baos.toByteArray();
        }
    }

    private static void openPdf(String filePath) {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", filePath).start();
            } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                new ProcessBuilder("open", filePath).start();
            } else { // linux and others
                new ProcessBuilder("xdg-open", filePath).start();
            }
        } catch (Exception e) {
            System.err.println("Gagal membuka file PDF: " + e.getMessage());
        }
    }
}

