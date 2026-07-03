package com.example.govservice.application;

import com.example.govservice.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(@RequestParam(required = false) String status,
                                                       @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(applicationService.list(status, keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> get(@PathVariable String id) {
        return ApiResponse.ok(applicationService.get(id));
    }

    @GetMapping("/{id}/status")
    public ApiResponse<Map<String, Object>> status(@PathVariable String id) {
        return ApiResponse.ok(applicationService.status(id));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        return ApiResponse.ok(applicationService.create(body));
    }

    @PostMapping("/{id}/supplements")
    public ApiResponse<Map<String, Object>> supplement(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ApiResponse.ok(applicationService.supplement(id, body));
    }

    @PostMapping("/{id}/materials/reject")
    public ApiResponse<Map<String, Object>> rejectMaterials(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ApiResponse.ok(applicationService.rejectMaterials(id, body));
    }

    @PostMapping("/{id}/accept")
    public ApiResponse<Map<String, Object>> accept(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ApiResponse.ok(applicationService.accept(id, body));
    }
}
