package com.hop.dao;
import com.hop.model.HasilAlternatif;
import java.util.List;

public interface HasilAlternatifDAO {
    void insert(HasilAlternatif hasil);
    void deleteAll();
    List<HasilAlternatif> getAll();
}
