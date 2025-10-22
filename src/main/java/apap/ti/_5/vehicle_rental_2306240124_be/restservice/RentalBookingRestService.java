package apap.ti._5.vehicle_rental_2306240124_be.restservice;

import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.*;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.RentalBookingResponseDTO;

import java.util.List;

public interface RentalBookingRestService {

    List<RentalBookingResponseDTO> getAllBookings();

    RentalBookingResponseDTO getBookingById(String id);

    RentalBookingResponseDTO createBooking(RentalBookingCreateRequestDTO request);

    RentalBookingResponseDTO updateBookingDetails(String id, RentalBookingUpdateDetailsRequestDTO request);

    RentalBookingResponseDTO updateBookingStatus(String id, RentalBookingUpdateStatusRequestDTO request);

    RentalBookingResponseDTO updateBookingAddOns(String id, RentalBookingUpdateAddOnsRequestDTO request);

    void deleteBooking(String id);

    List<BookingChartPointDTO> getBookingStatistics(String period, Integer year);
}
