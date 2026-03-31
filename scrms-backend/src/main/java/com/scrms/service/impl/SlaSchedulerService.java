package com.scrms.service.impl;

import com.scrms.entity.Complaint;
import com.scrms.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlaSchedulerService {

    private final ComplaintRepository complaintRepository;

    // Runs every 15 minutes
    @Scheduled(fixedRate = 900000)
    @Transactional
    public void checkSlaBreaches() {
        List<Complaint> breached = complaintRepository.findBreachedComplaints(LocalDateTime.now());
        breached.forEach(complaint -> {
            if (!complaint.isSlaBreached()) {
                complaint.setSlaBreached(true);
                complaintRepository.save(complaint);
                log.warn("SLA breached for complaint: {}", complaint.getReferenceNumber());
            }
        });

        if (!breached.isEmpty()) {
            log.info("SLA check complete. {} complaints marked as breached.", breached.size());
        }
    }
}
