package apap.ti._5.vehicle_rental_2306240124_be.restservice;

import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.RentalBookingCreateRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.RentalBookingSearchRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.RentalBookingResponseDTO;

import java.util.List;

public interface RentalBookingRestService {


    List<VehicleResponseDTO> searchAvailableVehicles(RentalBookingSearchRequestDTO request);

    RentalBookingResponseDTO createBooking(RentalBookingCreateRequestDTO request);

    List<RentalBookingResponseDTO> getAllBookings();

    RentalBookingResponseDTO getBookingById(String id);
}
