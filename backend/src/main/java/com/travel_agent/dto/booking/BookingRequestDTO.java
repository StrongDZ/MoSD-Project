package com.travel_agent.dto.booking;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Data
@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BookingHotelRequestDTO.class, name = "hotel"),
        @JsonSubTypes.Type(value = BookingShipRequestDTO.class, name = "ship")
})
public abstract class BookingRequestDTO {
    private List<RoomBooking> roomList;
    private String customerName;
    private String phone;
    private String email;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer adults;
    private Integer children;
    private String specialRequest;
    private Integer totalAmount;

    @Data
    public static class RoomBooking {
        private Integer roomId;
        private int quantity;
    }
}
