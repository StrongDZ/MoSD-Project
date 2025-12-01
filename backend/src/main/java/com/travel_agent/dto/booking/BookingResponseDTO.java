package com.travel_agent.dto.booking;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Data
@Getter
@Setter

public class BookingResponseDTO {
    private Integer bookingId;
    private String customerName;
    private String phone;
    private String email;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer adults;
    private Integer children;
    private String specialRequest;
    private String state;
    private Integer totalAmount;
} 