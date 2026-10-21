package com.fitness.monitor.controller;

import com.fitness.monitor.dto.ApiResponse;
import com.fitness.monitor.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/reports/weekly")
    public ResponseEntity<ApiResponse<Map<String, Object>>> weekly(@RequestAttribute("userId") Long userId,
                                                                    @RequestParam(defaultValue = "0") int weeksAgo) {
        return ResponseEntity.ok(ApiResponse.ok("Report ready", reportService.weekly(userId, weeksAgo)));
    }

    @GetMapping("/export/{type}")
    public ResponseEntity<byte[]> export(@RequestAttribute("userId") Long userId, @PathVariable String type) {
        byte[] body = reportService.csv(userId, type).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + type + ".csv\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(body);
    }
}
