package com.scrms.service.impl;

import com.scrms.entity.Complaint;
import com.scrms.repository.ComplaintRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SlaSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(SlaSchedulerService.class);

    private final ComplaintRepository complaintRepository;

    public SlaSchedulerService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

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