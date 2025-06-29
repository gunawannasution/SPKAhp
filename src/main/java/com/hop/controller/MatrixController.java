package com.hop.controller;

import com.hop.dao.MatrixDAO;
import com.hop.model.Matrix;

import java.util.List;

public class MatrixController {

    private final MatrixDAO matrixDAO;
    
    
    public MatrixController(MatrixDAO matrixDAO) {
        this.matrixDAO = matrixDAO;
    }

    public boolean simpanMatrixNormalisasi(List<Matrix> matrixList) {
        return matrixDAO.simpanMatrix(matrixList);
    }
}
