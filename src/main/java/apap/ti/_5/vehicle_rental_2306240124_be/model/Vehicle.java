package apap.ti._5.vehicle_rental_2306240124_be.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import apap.ti._5.vehicle_rental_2306240124_be.enums.FuelType;
import apap.ti._5.vehicle_rental_2306240124_be.enums.TransmissionType;
import apap.ti._5.vehicle_rental_2306240124_be.enums.VehicleStatus;
import apap.ti._5.vehicle_rental_2306240124_be.enums.VehicleType;

@Data
@Entity
@Table(name = "vehicle")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Vehicle {
    @Id
    private String id; // format: VEHxxxx

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_vendor_id", nullable = false)
    private RentalVendor rentalVendor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransmissionType transmission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuelType;

    private Double price;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @Builder.Default
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RentalBooking> bookings = new ArrayList<>();


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
