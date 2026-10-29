package rest;

import model.ArenaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Component
@FeignClient(name="${spring.cloud.openfeign.client.config.arena-microservice.name}",
             url="${spring.cloud.openfeign.client.config.arena-microservice.domain-url}")
public interface ArenaMicroserviceClient {
    @PostMapping
    List<ArenaDTO> getArenaData(@RequestBody Map<String, Object> searchCriteria);
}
