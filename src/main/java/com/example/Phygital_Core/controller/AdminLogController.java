package com.example.Phygital_Core.controller;

import com.example.Phygital_Core.dto.AdminLogRequest;
import com.example.Phygital_Core.entity.AdminLog;
import com.example.Phygital_Core.repository.AdminLogRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin-logs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminLogController {

    private final AdminLogRepository adminLogRepository;

    // Получить все логи (для таблицы на фронте)
    @GetMapping
    public ResponseEntity<List<AdminLog>> getAllLogs() {
        // Сортируем по дате (новые сверху)
        return ResponseEntity.ok(adminLogRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "date")));
    }

    // Создать лог (будет вызываться с фронтенда)
    @PostMapping
    public ResponseEntity<AdminLog> createLog(@RequestBody AdminLogRequest request) {
        AdminLog log = AdminLog.builder()
                .date(request.getDate() != null ? request.getDate() : LocalDateTime.now())
                .admin(request.getAdmin())
                .action(request.getAction())
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .details(request.getDetails())
                .build();
        return ResponseEntity.ok(adminLogRepository.save(log));
    }
}
