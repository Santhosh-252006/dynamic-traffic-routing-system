package com.traffic.controller;

import com.traffic.dto.DashboardMetricsDto;
import com.traffic.dto.ReportDto;
import com.traffic.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports & Analytics API", description = "Endpoints for retrieving system metrics, traffic distribution reports, and exporting route data")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get high-level summary metrics for dashboard cards")
    public ResponseEntity<DashboardMetricsDto> getDashboardMetrics() {
        return ResponseEntity.ok(reportService.getDashboardMetrics());
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get detailed traffic statistics and corridor analysis")
    public ResponseEntity<ReportDto> getStatistics() {
        return ResponseEntity.ok(reportService.getDetailedReport());
    }

    @GetMapping("/export/csv")
    @Operation(summary = "Export route search history as CSV file download")
    public ResponseEntity<byte[]> exportCsvHistory() {
        String csvData = reportService.generateCsvRouteHistory();
        byte[] bytes = csvData.getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "route_history_export.csv");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(bytes);
    }
}
