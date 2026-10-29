package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SFFrontendDTO {
    private String fighter_1;

    private String fighter_2;

    private String arena;

    private String address;

    private String date;

    private String time;

    private String event_status;
}
