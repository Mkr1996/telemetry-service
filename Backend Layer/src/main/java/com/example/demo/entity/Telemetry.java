package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Telemetry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "telemetry_id_seq")
    @SequenceGenerator(name = "telemetry_id_seq", sequenceName = "telemetry_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    @JsonBackReference
    private Machine machine;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Double vibration;

    private Double pressure;

    @Column(nullable = false)
    private Instant ts = Instant.now();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
