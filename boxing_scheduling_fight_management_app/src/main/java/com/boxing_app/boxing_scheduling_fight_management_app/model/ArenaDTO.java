package com.boxing_app.boxing_scheduling_fight_management_app.model;

public record ArenaDTO (
    Long arena_id,
    String name,
    String address,
    String city,
    String state,
    String country
){}