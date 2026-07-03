package com.example.govservice.statistics;

import com.example.govservice.common.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;
    private final ExcelExportService excelExportService;

    public StatisticsController(StatisticsService statisticsService, ExcelExportService excelExportService) {
        this.statisticsService = statisticsService;
        this.excelExportService = excelExportService;
    }

    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> overview() {
        return ApiResponse.ok(statisticsService.overview());
    }

    @GetMapping("/summary")
    public ApiResponse<List<Map<String, Object>>> summary(@RequestParam(required = false) String startDate,
                                                          @RequestParam(required = false) String endDate,
                                                          @RequestParam(required = false) String category,
                                                          @RequestParam(required = false) String department) {
        return ApiResponse.ok(statisticsService.summary(startDate, endDate, category, department));
    }

    @GetMapping("/efficiency")
    public ApiResponse<List<Map<String, Object>>> efficiency(@RequestParam(required = false) String startDate,
                                                             @RequestParam(required = false) String endDate,
                                                             @RequestParam(required = false) String category,
                                                             @RequestParam(required = false) String department) {
        return ApiResponse.ok(statisticsService.efficiency(startDate, endDate, category, department));
    }

    @GetMapping("/export")
    public void export(@RequestParam(defaultValue = "summary") String type,
                       @RequestParam(required = false) String startDate,
                       @RequestParam(required = false) String endDate,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) String department,
                       HttpServletResponse response) throws Exception {
        byte[] bytes = excelExportService.export(type, startDate, endDate, category, department);
        String title = "efficiency".equalsIgnoreCase(type) ? "办事效率分析" : "事项办理统计";
        String filename = URLEncoder.encode(title + "_" + LocalDate.now() + ".xlsx", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.getOutputStream().write(bytes);
    }
}
