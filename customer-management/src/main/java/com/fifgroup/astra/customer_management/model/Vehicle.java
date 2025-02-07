package com.fifgroup.astra.customer_management.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private String registrationId;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties("vehicles")
    private User owner;

    @Setter
    @Getter
    @Column(name = "vehicle_image")
    private String vehicleImage;

}
