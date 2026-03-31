package com.scrms.repository;

import com.scrms.entity.User;
import com.scrms.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByRoleAndActiveTrue(Role role);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.active = true " +
           "ORDER BY (SELECT COUNT(c) FROM Complaint c WHERE c.assignedAgent = u " +
           "AND c.status NOT IN " +
           "(com.scrms.enums.ComplaintStatus.RESOLVED, " +
           "com.scrms.enums.ComplaintStatus.CLOSED)) ASC")
    List<User> findAvailableAgentsSortedByLoad(@Param("role") Role role);
}