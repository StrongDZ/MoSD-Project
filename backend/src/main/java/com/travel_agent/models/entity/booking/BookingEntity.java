package com.travel_agent.models.entity.booking;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@MappedSuperclass
public abstract class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer bookingId;
    
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "adults")
    private Integer adults;
    
    @Column(name = "children")
    private Integer children;
    
    @Column(name = "special_request")
    private String specialRequest;
    
    @Column(name = "state")
    private String state;

    @Column(name = "total_amount")
    private Integer totalAmount;
} 