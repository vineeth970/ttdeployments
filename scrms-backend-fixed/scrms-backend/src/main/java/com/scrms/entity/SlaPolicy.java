package com.scrms.entity;

import com.scrms.enums.Category;
import com.scrms.enums.Priority;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sla_policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlaPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private int resolutionHours;   // SLA window in hours
    private int responseHours;     // First response SLA in hours
}
