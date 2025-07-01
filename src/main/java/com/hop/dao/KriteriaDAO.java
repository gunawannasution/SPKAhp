package com.hop.dao;

import com.hop.model.Kriteria;
import java.util.List;

public interface KriteriaDAO {
    boolean insert(Kriteria kriteria);
    List<Kriteria> getAll();
    Kriteria getById(int id);
    Kriteria getByKode(String kode);
    boolean update(Kriteria kriteria);
    boolean updateBobot(String namaKriteria, double bobot);
    boolean updateAllBobot(List<Kriteria> kriteriaList);
    boolean delete(int id);
    int count();
    boolean isKodeExist(String kode);
    
}
