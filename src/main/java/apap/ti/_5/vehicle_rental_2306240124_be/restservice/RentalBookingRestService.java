package apap.ti._5.vehicle_rental_2306240124_be.restservice;

import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.RentalBookingCreateRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.RentalBookingSearchRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.RentalBookingUpdateAddOnsRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.RentalBookingUpdateDetailsRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.request.rentalbooking.RentalBookingUpdateStatusRequestDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306240124_be.restdto.response.RentalBookingResponseDTO;

import java.util.List;
import java.util.Map;

public interface RentalBookingRestService {


    List<VehicleResponseDTO> searchAvailableVehicles(RentalBookingSearchRequestDTO request);

    RentalBookingResponseDTO createBooking(RentalBookingCreateRequestDTO request);

    List<RentalBookingResponseDTO> getAllBookings();

    RentalBookingResponseDTO getBookingById(String id);
    RentalBookingResponseDTO updateBookingDetails(String id, RentalBookingUpdateDetailsRequestDTO request);

    RentalBookingResponseDTO updateBookingStatus(String id, RentalBookingUpdateStatusRequestDTO request);
    RentalBookingResponseDTO updateBookingAddOns(String id, RentalBookingUpdateAddOnsRequestDTO request);
    RentalBookingResponseDTO cancelBooking(String id);
    Map<String, Object> getBookingChartData(String period, int year); 

}
