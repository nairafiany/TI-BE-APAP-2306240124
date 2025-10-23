package apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingChartPointDTO {
    private String label; // e.g. "January" or "Q1"
    private Long total;   // total bookings in that period
}