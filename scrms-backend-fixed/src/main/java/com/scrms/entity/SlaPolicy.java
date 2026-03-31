package com.scrms.entity;

import com.scrms.enums.Category;
import com.scrms.enums.Priority;
import jakarta.persistence.*;

@Entity
@Table(name = "sla_policies")
public class SlaPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private int resolutionHours;
    private int responseHours;

    // ✅ Default Constructor (REQUIRED by JPA)
    public SlaPolicy() {}

    // ❌ REMOVE old constructor OR keep it (optional)
    public SlaPolicy(Long id, Category category, Priority priority,
                     int resolutionHours, int responseHours) {
        this.id = id;
        this.category = category;
        this.priority = priority;
        this.resolutionHours = resolutionHours;
        this.responseHours = responseHours;
    }

    // ✅ ADD THIS CONSTRUCTOR (THIS FIXES YOUR ERROR)
    public SlaPolicy(Category category, Priority priority,
                     int responseHours, int resolutionHours) {
        this.category = category;
        this.priority = priority;
        this.responseHours = responseHours;
        this.resolutionHours = resolutionHours;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public int getResolutionHours() { return resolutionHours; }
    public void setResolutionHours(int resolutionHours) { this.resolutionHours = resolutionHours; }

    public int getResponseHours() { return responseHours; }
    public void setResponseHours(int responseHours) { this.responseHours = responseHours; }
}