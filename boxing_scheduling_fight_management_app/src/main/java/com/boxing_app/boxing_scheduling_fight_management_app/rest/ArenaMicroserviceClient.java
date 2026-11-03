package com.boxing_app.boxing_scheduling_fight_management_app.rest;

import com.boxing_app.boxing_scheduling_fight_management_app.model.ArenaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Component
@FeignClient(name="${spring.cloud.openfeign.client.config.arena-microservice.name}")
public interface ArenaMicroserviceClient {
    @PostMapping("/arenas/search")
    List<ArenaDTO> getArenaData(@RequestBody Map<String, Object> searchCriteria);

    @GetMapping("/arenas")
    List<ArenaDTO> getAllArenaData();
}
