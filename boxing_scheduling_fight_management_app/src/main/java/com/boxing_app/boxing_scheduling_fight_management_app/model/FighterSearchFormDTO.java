package com.boxing_app.boxing_scheduling_fight_management_app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FighterSearchFormDTO {
    private Long fighterID;

    private String first_name;

    private String last_name;

    private double height;

    private double weight;

    private int fights;

    private int wins;

    private int losses;

    private int draws;

    private int KOS;

    private String current_state_residence;

    private String current_city_residence;

    private int years_of_boxing;

    private double knockout_percentage;

    private String photo_attachment;

}
