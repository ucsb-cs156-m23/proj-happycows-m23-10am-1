package edu.ucsb.cs156.happiercows.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "commons_stats")
public class CommonsStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "commons_id", referencedColumnName = "id", nullable = false)
    private Commons commons;

    @Column(name = "num_cows", nullable = false)
    private int numCows;

    @Column(name = "avg_health", nullable = false)
    private double avgHealth;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

}
