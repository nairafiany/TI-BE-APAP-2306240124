package apap.ti._5.vehicle_rental_2306240124_be.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
    @Id
    private String id; // format: VEHxxxx

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_vendor_id", nullable = false)
    private RentalVendor rentalVendor;

    @Column(nullable = false)
    private String type; // Sedan, SUV, MPV, Luxury

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false, unique = true)
    private String licensePlate;

    private Integer capacity;

    private String transmission; // Manual / Automatic

    private String fuelType; // Bensin / Diesel / Hybrid / Listrik

    private Double price;

    private String status; // Available / In Use / Unavailable

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RentalBooking> bookings = new ArrayList<>();
}
