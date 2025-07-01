package com.hop.ui;

import com.hop.dao.AlternatifDAOImpl;
import com.hop.model.Alternatif;
import com.hop.report.ReportUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LaporanPanel extends JPanel {

    private JButton btnCetakReport;

    public LaporanPanel() {
        initComponents();
        initEvents();
    }

    private void initComponents() {
        setLayout(new FlowLayout());

        btnCetakReport = new JButton("Cetak Laporan Alternatif");
        add(btnCetakReport);
    }

    private void initEvents() {
        btnCetakReport.addActionListener(e -> {
            try {
                AlternatifDAOImpl dao = new AlternatifDAOImpl();
                List<Alternatif> dataList = dao.getAll();

                ReportUtil.generatePdfReport(
                    dataList,
                    new String[]{"ID", "NIK", "Nama", "Jabatan", "Alamat"},
                    "Laporan Data Alternatif",
                    "alternatif_report.pdf",
                    "Bandung",
                    "Gunawan"
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal mencetak laporan: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
