package apap.ti._5.vehicle_rental_2306240124_be.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "rental_booking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalBooking {
    @Id
    private String id; // format: VRxxxxxx

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    private Date pickUpTime;
    private Date dropOffTime;

    private String pickUpLocation;
    private String dropOffLocation;

    private Integer capacityNeeded;
    private String transmissionNeeded;
    private Boolean includeDriver;
    private Double totalPrice;
    private String status; // Upcoming / Ongoing / Done

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "booking_add_ons",
        joinColumns = @JoinColumn(name = "booking_id"),
        inverseJoinColumns = @JoinColumn(name = "addon_id")
    )
    private List<RentalAddOn> addOns = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
}
