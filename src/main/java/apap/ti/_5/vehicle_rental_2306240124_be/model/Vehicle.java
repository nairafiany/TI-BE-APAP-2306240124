package apap.ti._5.vehicle_rental_2306240124_be.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Data
@Entity
@Table(name = "vehicle")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id"
)
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

    @Column(name = "production_year", nullable = false)
    private Integer productionYear;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false, unique = true)
    private String licensePlate;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private String transmission; // Manual, Automatic

    @Column(nullable = false)
    private String fuelType; // Bensin, Diesel, Hybrid, Listrik

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String status; // Available, In Use, Unavailable

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
