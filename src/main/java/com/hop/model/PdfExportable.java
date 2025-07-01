package com.hop.model;

import java.util.List;

public interface PdfExportable {
    List<String> toPdfRow();
}