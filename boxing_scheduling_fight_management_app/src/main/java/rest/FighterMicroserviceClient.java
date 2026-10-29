package rest;

import model.FighterDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Component
@FeignClient(name = "${spring.cloud.openfeign.client.config.fighter-microservice.name}",
             url = "${spring.cloud.openfeign.client.config.fighter-microservice.domain-url}")
public interface FighterMicroserviceClient {
    @GetMapping("/fighters/search")
    public List<FighterDTO> getFighterData(@RequestBody Map<String, Object> searchCriteria);
}
