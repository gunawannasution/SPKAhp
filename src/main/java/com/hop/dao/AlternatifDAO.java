package com.hop.dao;

import com.hop.model.Alternatif;
import java.util.List;

public interface AlternatifDAO {

    boolean insert(Alternatif alt);

    boolean update(Alternatif alt);

    boolean delete(int id);

    List<Alternatif> getAll();

    // Tambahan untuk validasi NIK
    boolean isNikExists(String nik);

    boolean isNikExists(String nik, int excludeId);
    Alternatif getById(int id); 
}
