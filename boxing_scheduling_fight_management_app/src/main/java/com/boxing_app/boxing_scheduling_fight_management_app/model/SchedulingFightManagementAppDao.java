package com.boxing_app.boxing_scheduling_fight_management_app.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.boxing_app.boxing_scheduling_fight_management_app.rest.ArenaMicroserviceClient;
import com.boxing_app.boxing_scheduling_fight_management_app.rest.FighterMicroserviceClient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class SchedulingFightManagementAppDao {
    @Autowired
    private SchedulingFightManagementAppRepository repo;

    @Autowired
    private ArenaMicroserviceClient arenaMicroserviceClient;

    @Autowired
    private FighterMicroserviceClient fighterMicroserviceClient;

    //handles retrieving all fights that have been scheduled
    public List<SFFrontendDTO> getAllFights() {
        List<ScheduledFight> fights = repo.findAll();

        return null;
    }

    //handles saving of scheduled fights
    public ScheduledFight saveScheduledFights(ScheduledFightDTO fight) {
        ScheduledFight fightSaveData;

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

        fightSaveData = new ScheduledFight(
                fight.event_id(),
                fight.arena_id(),
                fight.fighter_id_1(),
                fight.fighter_id_2(),
                fight.winner_id(),
                fight.event_status(),
                LocalDate.parse(fight.date(), dateFormat),
                LocalTime.parse(fight.time(), timeFormat)
        );



        return repo.save(fightSaveData);
    }

    //this function retrieves fight records based on search criteria
    public List<SFFrontendDTO> getFights(SFSearchFormDTO searchCriteria){
        Object value;
        Specification<ScheduledFight> scheduledFightSpec;
        List<ArenaDTO> arenaDTOS;
        List<ScheduledFight> scheduledFightList;
        LocalDate date;

        int arenaIDCount = 0;
        scheduledFightSpec = Specification.unrestricted();
        List<Long> arenaIdList = new ArrayList<>();
        Map<String, Object> arenaSearchCriteria = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");



        /*Getting the search criteria values for retrieving the area data*/
        value = searchCriteria.city();
        if (value instanceof String providedCity && StringUtils.hasLength(providedCity)) {
            arenaSearchCriteria.put("city", value);
        }

        value = searchCriteria.state();
        if (value instanceof String providedState && StringUtils.hasLength(providedState)) {
            arenaSearchCriteria.put("state", value);
        }

        value = searchCriteria.country();
        if (value instanceof String providedCountry && StringUtils.hasLength(providedCountry)) {
            arenaSearchCriteria.put("country", value);
        }

        System.out.println("Before call to arena microservice");
        arenaDTOS = getArenaDTOList(arenaSearchCriteria);
        System.out.println("After call to arena microservice");

        /*This creates a list of arena_ids and passes them as a parameter to a method from
        the specification class to create a specification object that will be used by hibernate
        to create a series of equal conditions on the arena_id field concatenated by or statements
        in the sql query on the database.*/
        for (ArenaDTO arenaDTO: arenaDTOS) {
            arenaIdList.add(arenaDTO.arena_id());
        }

        scheduledFightSpec = scheduledFightSpec.and(ScheduledFightSpecs.orConcatenatedArenaIdStatements(arenaIdList));

        //This part here makes sure an equals condition on the date will be added to the sql query.
        value = searchCriteria.date();
        if (value instanceof String providedDate && StringUtils.hasLength(providedDate)) {
            date = LocalDate.parse(providedDate, formatter);
            scheduledFightSpec = scheduledFightSpec.and(ScheduledFightSpecs.findByDate(date));

        }

        scheduledFightList = repo.findAll(scheduledFightSpec);

        return getRecordsForFrontEnd(scheduledFightList, arenaDTOS);
    }

    public List<ArenaDTO> getArenaDTOList(Map<String, Object> arenaSearchCriteria) {
        return arenaMicroserviceClient.getArenaData(arenaSearchCriteria);
    }

    //This method creates the records for the scheduled fights that will be sent back to the front end.
    public List<SFFrontendDTO> getRecordsForFrontEnd(List<ScheduledFight> fightRecords, List<ArenaDTO> arenaData) {
        String address;
        String fighterName;
        List<SFFrontendDTO> frontendDTOS;
        SFFrontendDTO frontendDTO;
        FighterDTO fighterDTO;
        Optional<ArenaDTO> matchingArena;
        ArenaDTO arenaDTO;
        FighterSearchFormDTO fighterSearchCriteria;

        frontendDTOS = new LinkedList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        //This for loop handles the creation of the fight records that will be sent back to the front end
        for (ScheduledFight fightRecord : fightRecords) {
            frontendDTO = new SFFrontendDTO();
            fighterSearchCriteria = new FighterSearchFormDTO();

            //Setting of fighter 1 and fighter 2 names
            fighterSearchCriteria.setFighterID(fightRecord.getFighter_id_1());
            fighterDTO = fighterMicroserviceClient.getFighterData(fighterSearchCriteria).remove(0);
            fighterName = fighterDTO.first_name() + " " + fighterDTO.last_name();
            frontendDTO.setFighter_1(fighterName);

            fighterSearchCriteria.setFighterID(fightRecord.getFighter_id_2());
            fighterDTO = fighterMicroserviceClient.getFighterData(fighterSearchCriteria).remove(0);
            fighterName = fighterDTO.first_name() + " " + fighterDTO.last_name();
            frontendDTO.setFighter_2(fighterName);

            if (fightRecord.getWinner_id() != null) {
                fighterSearchCriteria.setFighterID(fightRecord.getWinner_id());
                fighterDTO = fighterMicroserviceClient.getFighterData(fighterSearchCriteria).remove(0);
                fighterName = fighterDTO.first_name() + " " + fighterDTO.last_name();
                frontendDTO.setWinner(fighterName);
            }

            //This sets the information about the arena
            matchingArena = arenaData.stream()
                                    .filter(arenaDTO1 -> arenaDTO1.arena_id().equals(fightRecord.getArena_id()))
                                    .findFirst();

            arenaDTO = matchingArena.get();

            frontendDTO.setArena(arenaDTO.name());

            address = arenaDTO.address() + " " + arenaDTO.city() + ", " + arenaDTO.state() + "-" + arenaDTO.country();

            frontendDTO.setAddress(address);

            //Set the date, time, and event status of the fight
            frontendDTO.setDate(fightRecord.getDate().format(dateFormatter));
            frontendDTO.setTime(fightRecord.getTime().format(timeFormatter));
            frontendDTO.setEvent_status(fightRecord.getEvent_status());

            frontendDTOS.add(frontendDTO);
        }

        return frontendDTOS;
    }
}
