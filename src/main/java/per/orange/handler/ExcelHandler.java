package per.orange.handler;

import com.sun.corba.se.spi.orbutil.threadpool.Work;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.beans.ExceptionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelHandler {

    public static final String OFFICE_EXCEL_XLS = "xls";
    public static final String OFFICE_EXCEL_XLSX = "xlsx";

    public void writeOneRecord() {

    }

    public void readOneRecord() {

    }

    private String getSuffix(String s) {
        int index = s.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return s.substring(index + 1);
    }

    private Map<String, CellStyle> createStyle(Workbook workbook) {
        Map<String, CellStyle> styles = new HashMap<>();

        // 标题样式
        XSSFCellStyle titleStyle = (XSSFCellStyle) workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setLocked(true);
        titleStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBold(true);
        titleFont.setFontName("微软雅黑");
        titleStyle.setFont(titleFont);
        styles.put("title", titleStyle);

        // 文件头样式
        XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); //颜色填充方式
        headerStyle.setWrapText(true);
        headerStyle.setBorderRight(BorderStyle.THIN); // 设置边界
        headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        Font headerFont = workbook.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        titleFont.setFontName("微软雅黑");
        headerStyle.setFont(headerFont);
        styles.put("header", headerStyle);

        Font cellStyleFont = workbook.createFont();
        cellStyleFont.setFontHeightInPoints((short) 12);
        cellStyleFont.setColor(IndexedColors.BLUE_GREY.getIndex());
        cellStyleFont.setFontName("微软雅黑");

        // 正文样式A
        XSSFCellStyle cellStyleA = (XSSFCellStyle) workbook.createCellStyle();
        cellStyleA.setAlignment(HorizontalAlignment.CENTER);
        cellStyleA.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleA.setWrapText(true);
        cellStyleA.setBorderRight(BorderStyle.THIN);
        cellStyleA.setRightBorderColor(IndexedColors.BLACK.getIndex());

        cellStyleA.setFont(cellStyleFont);
        styles.put("cellA", cellStyleA);

        // 正文样式B
        XSSFCellStyle cellStyleB = (XSSFCellStyle) workbook.createCellStyle();
        cellStyleB.setAlignment(HorizontalAlignment.CENTER);
        cellStyleB.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleB.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        cellStyleB.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyleB.setWrapText(true);
        cellStyleB.setFont(cellStyleFont);
        styles.put("cellB", cellStyleB);

        return styles;
    }

    public boolean writeWholeExcel(String filePath, String sheetName, List<String> titles,
                                List<Map<String, Object>> values) throws IOException {
        boolean success = false;
        OutputStream outputstream = null;
        // todo exist 判断
        Files.createFile(Paths.get(filePath));
        if (StringUtils.isBlank(filePath)) {
            throw new RuntimeException("创建Excel 文件出错");
        }
        String suffix = getSuffix(filePath);
        Workbook workbook;
        if ("xls".equals(suffix.toLowerCase())) {
            workbook = new HSSFWorkbook();
        } else {
            workbook = new XSSFWorkbook();
        }
        Sheet sheet;
        if (StringUtils.isBlank(sheetName)) {
            sheet = workbook.createSheet();
        } else {
            sheet = workbook.createSheet(sheetName);
        }
        sheet.setDefaultColumnWidth(15);
        Map<String, CellStyle> styles = createStyle(workbook);
        Row row = sheet.createRow(0);
        Map<String, Integer> titleOrder = new HashMap<>();
        for (int i = 0; i < titles.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(styles.get("header"));
            String title = titles.get(i);
            cell.setCellValue(title);
            titleOrder.put(title, i);
        }

        Iterator<Map<String, Object>> iterator = values.iterator();
        int index = 1;
        while (iterator.hasNext()) {
            row = sheet.createRow(index++);
            Map<String, Object> value = iterator.next();
            for (Map.Entry<String, Object> map : value.entrySet()) {
                String title = map.getKey();
                int i = titleOrder.get(title);
                Cell cell = row.createCell(i);
                if (index % 2 == 1) {
                    cell.setCellStyle(styles.get("CellA"));
                } else {
                    cell.setCellStyle(styles.get("cellB"));
                }
                Object o = map.getValue();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (o instanceof Double) {
                    cell.setCellValue((Double)o);
                } else if (o instanceof Date) {
                    String time = sdf.format((Date) o);
                    cell.setCellValue(time);
                } else if (o instanceof Calendar) {
                    Calendar calendar = (Calendar) o;
                    String time = sdf.format(calendar.getTime());
                    cell.setCellValue(time);
                } else if (o instanceof Boolean) {
                    cell.setCellValue((Boolean) o);
                } else {
                    if (o != null) {
                        cell.setCellValue(o.toString());
                    }
                }
            }
        }
        try {
            outputstream = new FileOutputStream(filePath);
            workbook.write(outputstream);
            success = true;
        } catch (IOException e) {
            throw new RuntimeException("写入Excel数据时报错");
        } finally {
            if (outputstream != null) {
                outputstream.close();
            }
            if (workbook != null) {
                workbook.close();
            }
        }
        return success;
    }

    //String filePath, String sheetName, List<String> titles,
    //                                List<Map<String, Object>> values
    public static void main(String[] args) throws IOException{
        ExcelHandler excelHandler = new ExcelHandler();
        Map<String, Object> map = new HashMap<>();
        map.put("header1", "c1");
        map.put("header2", "c2");
        map.put("header3", "c3");
        List<Map<String,Object>> list = new ArrayList<>();
        list.add(map);
        list.add(map);
        list.add(map);
        excelHandler.writeWholeExcel("C:/Users/liyuan/Desktop/bak/test.xlsx", "裂开",
                Arrays.asList("header1", "header2", "header3"), list);
    }
}
