package Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class ExcelExporter {

    private ExcelExporter() {
    }

    /**
     * Método central que escribe los datos genéricos en un archivo Excel.
     *
     * @param headers La lista de encabezados para las columnas.
     * @param dataRows Una lista de listas, donde cada lista interna es una fila
     * de datos.
     * @param sheetName El nombre de la hoja de Excel.
     * @param filePath La ruta completa del archivo a guardar.
     * @throws IOException Si ocurre un error de escritura.
     */
    public static void export(List<String> headers, List<List<Object>> dataRows, String sheetName, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // Estilo para celdas de fecha
            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

            // Fila de encabezados
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                headerRow.createCell(i).setCellValue(headers.get(i));
            }

            // Filas de datos
            int rowNum = 1;
            for (List<Object> rowData : dataRows) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < rowData.size(); i++) {
                    Cell cell = row.createCell(i);
                    setCellValue(cell, rowData.get(i), dateCellStyle);
                }
            }

            // Autoajuste del tamaño de las columnas
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Escribir el archivo de salida
            try (FileOutputStream out = new FileOutputStream(filePath)) {
                workbook.write(out);
            }
        }
    }

    private static void setCellValue(Cell cell, Object value, CellStyle dateCellStyle) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof LocalDate) {
            cell.setCellValue((LocalDate) value);
            cell.setCellStyle(dateCellStyle);
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) value);
            cell.setCellStyle(dateCellStyle);
        } else if (value instanceof Date) { // Para compatibilidad con tipos antiguos
            cell.setCellValue((Date) value);
            cell.setCellStyle(dateCellStyle);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * Exporta una lista de datos con una sección de resumen al principio.
     *
     * @param headers La lista de encabezados para las columnas de la tabla.
     * @param dataRows La lista de filas de datos de la tabla.
     * @param sheetName El nombre de la hoja de Excel.
     * @param filePath La ruta del archivo a guardar.
     * @param summaryRows Una lista de filas para el resumen (cada fila es un
     * Object[]).
     * @throws IOException Si ocurre un error de escritura.
     */
    public static void export(List<String> headers, List<List<Object>> dataRows, String sheetName,
            String filePath, List<Object[]> summaryRows) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

            int rowNum = 0;

            // 1. Escribir las filas del resumen primero
            if (summaryRows != null && !summaryRows.isEmpty()) {
                for (Object[] summaryRowData : summaryRows) {
                    Row row = sheet.createRow(rowNum++);
                    for (int i = 0; i < summaryRowData.length; i++) {
                        Cell cell = row.createCell(i);
                        setCellValue(cell, summaryRowData[i], dateCellStyle);
                    }
                }
            }

            // 2. Escribir los encabezados de la tabla
            Row headerRow = sheet.createRow(rowNum++);
            for (int i = 0; i < headers.size(); i++) {
                headerRow.createCell(i).setCellValue(headers.get(i));
            }

            // 3. Escribir las filas de datos de la tabla
            for (List<Object> rowData : dataRows) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < rowData.size(); i++) {
                    Cell cell = row.createCell(i);
                    setCellValue(cell, rowData.get(i), dateCellStyle);
                }
            }

            // Autoajuste del tamaño de las columnas
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream out = new FileOutputStream(filePath)) {
                workbook.write(out);
            }
        }
    }
}
