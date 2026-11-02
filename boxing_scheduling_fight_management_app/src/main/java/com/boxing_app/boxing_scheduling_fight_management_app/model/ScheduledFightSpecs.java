package com.boxing_app.boxing_scheduling_fight_management_app.model;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ScheduledFightSpecs {
    public static Specification<ScheduledFight> findByArenaId(Long arena_id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("arena_id"), arena_id);
    }

    public static Specification<ScheduledFight> findByFighterId(Long fighter_id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("fighter_id"), fighter_id);
    }

    public static Specification<ScheduledFight> findByDate(LocalDate date) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("date"), date);
    }

    public static Specification<ScheduledFight> orConcatenatedArenaIdStatements(List<Long> arena_ids) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> arenaIdConditions = new ArrayList<>();

            for (Long arena_id : arena_ids) {
                arenaIdConditions.add(criteriaBuilder.equal(root.get("arena_id"), arena_id));
            }

            Predicate[] arenaIdConditionsArray = arenaIdConditions.toArray(new Predicate[0]);

            return criteriaBuilder.or(arenaIdConditionsArray);
        };
    }
}
