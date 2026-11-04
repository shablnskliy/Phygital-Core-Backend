package com.example.Phygital_Core.aspect;

import com.example.Phygital_Core.entity.AdminLog;
import com.example.Phygital_Core.repository.AdminLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AdminLogRepository adminLogRepository;

    // Перехватываем ВСЕ методы во ВСЕХ контроллерах
    @AfterReturning("execution(* com.example.Phygital_Core.controller.*.*(..))")
    public void logAdminAction(JoinPoint joinPoint) {
        try {
            // Достаем HTTP-запрос, чтобы понять, какой это был метод
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) return;

            HttpServletRequest request = attributes.getRequest();
            String method = request.getMethod();

            // Логируем только изменения (POST, PUT, PATCH, DELETE)
            if (!method.equals("POST") && !method.equals("PUT") && !method.equals("PATCH") && !method.equals("DELETE")) {
                return;
            }

            // Получаем ID админа из JWT (он лежит в контексте)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getName() == null || auth.getName().equals("anonymousUser")) {
                return;
            }

            String adminId = auth.getName();
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();

            // Создаем и сохраняем лог
            AdminLog log = AdminLog.builder()
                    .date(LocalDateTime.now())
                    .admin("ADMIN (ID: " + adminId + ")")
                    .action(method)
                    .entityType(className)
                    .entityId(null)
                    .details("Вызван метод: " + methodName)
                    .build();

            adminLogRepository.save(log);
            System.out.println("✅ ЛОГ ЗАПИСАН В БД: " + method + " " + className);

        } catch (Exception e) {
            System.out.println("❌ Ошибка записи лога: " + e.getMessage());
        }
    }
}
