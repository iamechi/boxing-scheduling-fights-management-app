package com.boxing_app.boxing_scheduling_fight_management_app.rest;

import com.boxing_app.boxing_scheduling_fight_management_app.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
@RestController
public class SchedulingFightManagementAppController {
    @Autowired
    private SchedulingFightManagementAppDao service;

    @PostMapping("/schedulefight/save")
    public ResponseEntity<?> saveScheduledFightRecord(@RequestBody ScheduledFightDTO scheduledFightRecord) {
        ScheduledFight savedScheduledFight;
        savedScheduledFight = service.saveScheduledFights(scheduledFightRecord);

        return ResponseEntity.ok(savedScheduledFight);
    }

    @GetMapping("/schedulefight")
    public List<SFFrontendDTO> getAllScheduledFightRecords() {
        SFSearchFormDTO emptySearch = new SFSearchFormDTO(null, null, null, null, null);
        return service.getFights(emptySearch);
    }

    @PostMapping("/schedulefight/search")
    public List<SFFrontendDTO> getScheduledFightRecords(@RequestBody SFSearchFormDTO searchCriteria) {
        System.out.println("entered getScheduledFightRecords");
        return service.getFights(searchCriteria);
    }
}
