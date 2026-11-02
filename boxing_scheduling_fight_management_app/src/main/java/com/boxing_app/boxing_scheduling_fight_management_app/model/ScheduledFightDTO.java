package com.boxing_app.boxing_scheduling_fight_management_app.model;

public record ScheduledFightDTO(
        Long event_id,
        Long arena_id,
        Long fighter_id_1,
        Long fighter_id_2,
        Long winner_id,
        String event_status,
        String date,
        String time

) {}
