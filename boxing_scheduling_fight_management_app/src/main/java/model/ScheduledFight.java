package model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.sql.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ScheduledFight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long event_id;

    @Column(name = "arena_id")
    private Long arena_id;

    @Column(name = "fighter_id_1")
    private Long fighter_id_1;

    @Column(name = "fighter_id_2")
    private Long fighter_id_2;

    @Column(name = "winner_id")
    private Long winner_id;

    @Column(name = "event_status")
    private String event_status;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "time")
    private LocalTime time;

}
