package Utils;

import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Set;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author SwichBlade15
 */
/**
 * Utilidad para exportar datos a archivos Excel (.xlsx) utilizando Apache POI.
 */
public class Export_Excel {

    /**
     * Exporta un ResultSet a un archivo Excel, omitiendo columnas
     * especificadas.
     *
     * @param rs
     * @param sheetName
     * @param filePath
     * @param columnsToSkip
     * @throws java.lang.Exception
     */
    public static void export(ResultSet rs,
            String sheetName,
            String filePath,
            Set<Integer> columnsToSkip) throws Exception {
        export(rs, sheetName, filePath, columnsToSkip, null);
    }

    /**
     * Exporta un ResultSet a un archivo Excel, omitiendo columnas y
     * anteponiendo filas si extraRows != null.
     *
     * @param rs
     * @param sheetName
     * @param filePath
     * @param columnsToSkip
     * @param extraRows
     * @throws java.lang.Exception
     */
    public static void export(ResultSet rs,
            String sheetName,
            String filePath,
            Set<Integer> columnsToSkip,
            List<Object[]> extraRows) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);
            int currentRow = 0;

            // filas extra
            if (extraRows != null) {
                for (Object[] rowData : extraRows) {
                    Row row = sheet.createRow(currentRow++);
                    for (int c = 0; c < rowData.length; c++) {
                        Cell cell = row.createCell(c);
                        Object val = rowData[c];
                        cell.setCellValue(val != null ? val.toString() : "");
                    }
                }
                currentRow++; // línea en blanco
            }

            // metadatos del ResultSet
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            // encabezados
            Row header = sheet.createRow(currentRow++);
            int headerIdx = 0;
            for (int c = 1; c <= colCount; c++) {
                if (columnsToSkip.contains(c - 1)) {
                    continue;
                }
                Cell cell = header.createCell(headerIdx++);
                cell.setCellValue(meta.getColumnLabel(c));
            }

            // datos
            while (rs.next()) {
                Row row = sheet.createRow(currentRow++);
                int cellIdx = 0;
                for (int c = 1; c <= colCount; c++) {
                    if (columnsToSkip.contains(c - 1)) {
                        continue;
                    }
                    Cell cell = row.createCell(cellIdx++);
                    Object val = rs.getObject(c);
                    cell.setCellValue(val != null ? val.toString() : "");
                }
            }

            // autoajuste de columnas
            for (int i = 0; i < headerIdx; i++) {
                sheet.autoSizeColumn(i);
            }

            // escribir fichero
            try (FileOutputStream out = new FileOutputStream(filePath)) {
                workbook.write(out);
            }
        }
    }

    /**
     * Exporta un TableModel a Excel, omitiendo columnas y anteponiendo filas.
     *
     * @param model
     * @param sheetName
     * @param filePath
     * @param columnsToSkip
     * @param extraRows
     * @throws java.lang.Exception
     */
    public static void export(TableModel model,
            String sheetName,
            String filePath,
            Set<Integer> columnsToSkip,
            List<Object[]> extraRows) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet(sheetName);
            int currentRow = 0;

            // filas extra
            if (extraRows != null) {
                for (Object[] rowData : extraRows) {
                    Row row = sheet.createRow(currentRow++);
                    for (int c = 0; c < rowData.length; c++) {
                        Cell cell = row.createCell(c);
                        Object val = rowData[c];
                        cell.setCellValue(val != null ? val.toString() : "");
                    }
                }
                currentRow++; // línea en blanco
            }

            // encabezados
            int colCount = model.getColumnCount();
            Row header = sheet.createRow(currentRow++);
            int headerIdx = 0;
            for (int c = 0; c < colCount; c++) {
                if (columnsToSkip.contains(c)) {
                    continue;
                }
                Cell cell = header.createCell(headerIdx++);
                cell.setCellValue(model.getColumnName(c));
            }

            // datos
            int rowCount = model.getRowCount();
            for (int r = 0; r < rowCount; r++) {
                Row row = sheet.createRow(currentRow++);
                int cellIdx = 0;
                for (int c = 0; c < colCount; c++) {
                    if (columnsToSkip.contains(c)) {
                        continue;
                    }
                    Cell cell = row.createCell(cellIdx++);
                    Object val = model.getValueAt(r, c);
                    cell.setCellValue(val != null ? val.toString() : "");
                }
            }

            // autoajuste de columnas
            for (int i = 0; i < headerIdx; i++) {
                sheet.autoSizeColumn(i);
            }

            // escribir fichero
            try (FileOutputStream out = new FileOutputStream(filePath)) {
                workbook.write(out);
            }
        }
    }

    /**
     * Atajo: Exporta un TableModel sin filas extra.
     *
     * @param model
     * @param sheetName
     * @param filePath
     * @param columnsToSkip
     * @throws java.lang.Exception
     */
    public static void export(TableModel model,
            String sheetName,
            String filePath,
            Set<Integer> columnsToSkip) throws Exception {
        export(model, sheetName, filePath, columnsToSkip, null);
    }

    /**
     * Atajo: Exporta un JTable sin filas extra.
     *
     * @param table
     * @param sheetName
     * @param filePath
     * @param columnsToSkip
     * @throws java.lang.Exception
     */
    public static void export(JTable table,
            String sheetName,
            String filePath,
            Set<Integer> columnsToSkip) throws Exception {
        export(table.getModel(), sheetName, filePath, columnsToSkip, null);
    }

    /**
     * Atajo: Exporta un JTable con filas extra.
     *
     * @param table
     * @param sheetName
     * @param filePath
     * @param columnsToSkip
     * @param extraRows
     * @throws java.lang.Exception
     */
    public static void export(JTable table,
            String sheetName,
            String filePath,
            Set<Integer> columnsToSkip,
            List<Object[]> extraRows) throws Exception {
        export(table.getModel(), sheetName, filePath, columnsToSkip, extraRows);
    }

    /**
     * Atajo: Exporta un JTable sin filas extra.
     *
     * @param table
     * @param sheetName
     * @param filePath
     * @throws java.lang.Exception
     */
    public static void export(JTable table,
            String sheetName,
            String filePath) throws Exception {
        export(table.getModel(), sheetName, filePath, null, null);
    }
}
