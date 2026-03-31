package com.scrms.repository;

import com.scrms.entity.SlaPolicy;
import com.scrms.enums.Category;
import com.scrms.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SlaPolicyRepository extends JpaRepository<SlaPolicy, Long> {
    Optional<SlaPolicy> findByCategoryAndPriority(Category category, Priority priority);
}
