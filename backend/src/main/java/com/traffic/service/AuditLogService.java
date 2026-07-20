package com.traffic.service;

import com.traffic.dto.AuditLogDto;
import com.traffic.model.AuditLog;
import com.traffic.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void logAction(String userEmail, String action, String details) {
        AuditLog auditLog = new AuditLog(userEmail != null ? userEmail : "SYSTEM", action, details);
        auditLogRepository.save(auditLog);
    }

    public List<AuditLogDto> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc().stream()
                .map(log -> new AuditLogDto(log.getId(), log.getUserEmail(), log.getAction(), log.getDetails(), log.getTimestamp()))
                .collect(Collectors.toList());
    }
}
