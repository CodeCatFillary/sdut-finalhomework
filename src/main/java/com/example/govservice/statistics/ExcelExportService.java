package com.example.govservice.statistics;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class ExcelExportService {
    private final StatisticsService statisticsService;

    public ExcelExportService(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    public byte[] export(String type, String startDate, String endDate, String category, String department) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if ("efficiency".equalsIgnoreCase(type)) {
                writeSheet(workbook, "办事效率分析", List.of("事项名称", "分类", "样本数量", "平均小时", "最快小时", "最慢小时", "效率状态"),
                        List.of("matterName", "category", "sampleCount", "averageHours", "fastestHours", "slowestHours", "efficiencyStatus"),
                        statisticsService.efficiency(startDate, endDate, category, department));
            } else {
                writeSheet(workbook, "事项办理统计", List.of("事项名称", "分类", "申请数量", "受理数量", "办结数量", "补正数量", "办结率"),
                        List.of("matterName", "category", "applyCount", "acceptedCount", "completedCount", "supplementCount", "completionRate"),
                        statisticsService.summary(startDate, endDate, category, department));
            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception exception) {
            throw new IllegalStateException("Excel 导出失败 " + exception.getMessage(), exception);
        }
    }

    private void writeSheet(XSSFWorkbook workbook, String sheetName, List<String> headers, List<String> keys, List<Map<String, Object>> rows) {
        var sheet = workbook.createSheet(sheetName);
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 22 * 256);
        }

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            Row row = sheet.createRow(rowIndex + 1);
            Map<String, Object> item = rows.get(rowIndex);
            for (int col = 0; col < keys.size(); col++) {
                Object value = item.get(keys.get(col));
                row.createCell(col).setCellValue(value == null ? "" : String.valueOf(value));
            }
        }
    }
}
