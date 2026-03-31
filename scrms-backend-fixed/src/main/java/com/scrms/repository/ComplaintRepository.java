package com.scrms.repository;

import com.scrms.entity.Complaint;
import com.scrms.entity.User;
import com.scrms.enums.ComplaintStatus;
import com.scrms.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    Optional<Complaint> findByReferenceNumber(String referenceNumber);

    List<Complaint> findByCustomer(User customer);

    List<Complaint> findByAssignedAgent(User agent);

    List<Complaint> findByStatus(ComplaintStatus status);

    List<Complaint> findByPriority(Priority priority);

    List<Complaint> findByStatusNot(ComplaintStatus status);

    @Query("SELECT c FROM Complaint c WHERE c.slaDeadline < :now " +
           "AND c.status NOT IN " +
           "(com.scrms.enums.ComplaintStatus.RESOLVED, " +
           "com.scrms.enums.ComplaintStatus.CLOSED, " +
           "com.scrms.enums.ComplaintStatus.REJECTED)")
    List<Complaint> findBreachedComplaints(@Param("now") LocalDateTime now);

    @Query("SELECT c FROM Complaint c WHERE c.slaDeadline BETWEEN :now AND :deadline " +
           "AND c.status NOT IN " +
           "(com.scrms.enums.ComplaintStatus.RESOLVED, " +
           "com.scrms.enums.ComplaintStatus.CLOSED, " +
           "com.scrms.enums.ComplaintStatus.REJECTED)")
    List<Complaint> findUpcomingBreaches(@Param("now") LocalDateTime now,
                                         @Param("deadline") LocalDateTime deadline);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.status = :status")
    long countByStatus(@Param("status") ComplaintStatus status);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.assignedAgent = :agent " +
           "AND c.status NOT IN " +
           "(com.scrms.enums.ComplaintStatus.RESOLVED, " +
           "com.scrms.enums.ComplaintStatus.CLOSED)")
    long countActiveByAgent(@Param("agent") User agent);

    @Query("SELECT c FROM Complaint c WHERE c.createdAt BETWEEN :from AND :to")
    List<Complaint> findByDateRange(@Param("from") LocalDateTime from,
                                    @Param("to") LocalDateTime to);
}