package model;

public record ArenaDTO (
    Long arena_id,
    String name,
    String address,
    String city,
    String state,
    String country
){}