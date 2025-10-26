package apap.ti._5.vehicle_rental_2306240124_be.repository;

import apap.ti._5.vehicle_rental_2306240124_be.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    boolean existsByLicensePlate(String licensePlate);

    @Query(value = """
        SELECT v.*
        FROM vehicle v
        JOIN rental_vendor rv ON rv.id = v.rental_vendor_id
        WHERE v.status <> 'Unavailable'
          AND (:type IS NULL OR v.type ILIKE :type)
          AND (
              :keyword IS NULL
              OR v.brand ILIKE CONCAT('%', :keyword, '%')
              OR v.model ILIKE CONCAT('%', :keyword, '%')
              OR v.location ILIKE CONCAT('%', :keyword, '%')
              OR rv.name ILIKE CONCAT('%', :keyword, '%')
          )
        ORDER BY v.created_at DESC
    """, nativeQuery = true)
    List<Vehicle> findFilteredVehicles(
            @Param("type") String type,
            @Param("keyword") String keyword
    );
}
