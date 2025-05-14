package com.example.YummyDaily.repository;

import com.example.YummyDaily.entity.PendingNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PendingNotificationRepository extends JpaRepository<PendingNotification, Long> {
    List<PendingNotification> findByStatusAndRetryCountLessThan(String status, int maxRetries);
}