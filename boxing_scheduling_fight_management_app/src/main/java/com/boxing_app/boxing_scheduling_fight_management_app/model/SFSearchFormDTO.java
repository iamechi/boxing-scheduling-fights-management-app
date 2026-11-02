package com.boxing_app.boxing_scheduling_fight_management_app.model;

public record SFSearchFormDTO (
    String address,
    String city,
    String state,
    String country,
    String date
) {}
