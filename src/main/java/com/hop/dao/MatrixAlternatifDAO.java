package com.hop.dao;

import com.hop.model.MatrixAlternatif;

import java.util.List;


public interface MatrixAlternatifDAO {
    boolean insert(MatrixAlternatif m);
    boolean update(MatrixAlternatif m);
    boolean delete(int idAlternatif, int idKriteria);
    List<MatrixAlternatif> getAll();
    MatrixAlternatif getByAlternatifAndKriteria(int idAlternatif, int idKriteria);
    List<MatrixAlternatif> getByKriteria(int idKriteria);

}

