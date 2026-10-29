package model;

import ch.qos.logback.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import rest.ArenaMicroserviceClient;
import rest.FighterMicroserviceClient;

import java.time.LocalDate;
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
    public ScheduledFight saveScheduledFights(ScheduledFight fight) {return repo.save(fight);}

    //this function retrieves fight records based on search criteria
    public List<SFFrontendDTO> getFights(Map<String, Object> searchCriteria){
        Object value;
        Specification<ScheduledFight> scheduledFightSpec;
        List<ArenaDTO> arenaDTOS;
        List<ScheduledFight> scheduledFightList;
        LocalDate date;

        int arenaIDCount = 0;
        scheduledFightSpec = Specification.unrestricted();
        List<Long> arenaIdList = new ArrayList<>();
        Map<String, Object> arenaSearchCriteria = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");



        /*Getting the search criteria values for retrieving the area data*/
        value = searchCriteria.get("address");
        if (value instanceof String providedAddress && StringUtils.hasLength(providedAddress)) {
            arenaSearchCriteria.put("address", value);
        }

        value = searchCriteria.get("city");
        if (value instanceof String providedCity && StringUtils.hasLength(providedCity)) {
            arenaSearchCriteria.put("city", value);
        }

        value = searchCriteria.get("state");
        if (value instanceof String providedState && StringUtils.hasLength(providedState)) {
            arenaSearchCriteria.put("state", value);
        }

        value = searchCriteria.get("country");
        if (value instanceof String providedCountry && StringUtils.hasLength(providedCountry)) {
            arenaSearchCriteria.put("country", value);
        }

        arenaDTOS = getArenaDTOList(arenaSearchCriteria);

        /*This creates a list of arena_ids and passes them as a parameter to a method from
        the specification class to create a specification object that will be used by hibernate
        to create a series of equal conditions on the arena_id field concatenated by or statements
        in the sql query on the database.*/
        for (ArenaDTO arenaDTO: arenaDTOS) {
            arenaIdList.add(arenaDTO.arena_id());
        }

        scheduledFightSpec = scheduledFightSpec.and(ScheduledFightSpecs.orConcatenatedArenaIdStatements(arenaIdList));

        //This part here makes sure an equals condition on the date will be added to the sql query.
        value = searchCriteria.get("date");
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
        Map<String, Object> fighterSearchCriteria;

        frontendDTOS = new LinkedList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        //This for loop handles the creation of the fight records that will be sent back to the front end
        for (ScheduledFight fightRecord : fightRecords) {
            frontendDTO = new SFFrontendDTO();
            fighterSearchCriteria = new HashMap<>();

            //Setting of fighter 1 and fighter 2 names
            fighterSearchCriteria.put("fighterID",fightRecord.getFighter_id_1());
            fighterDTO = fighterMicroserviceClient.getFighterData(fighterSearchCriteria).remove(0);
            fighterName = fighterDTO.first_name() + fighterDTO.last_name();
            frontendDTO.setFighter_1(fighterName);

            fighterSearchCriteria.put("fighterID",fightRecord.getFighter_id_2());
            fighterDTO = fighterMicroserviceClient.getFighterData(fighterSearchCriteria).remove(0);
            fighterName = fighterDTO.first_name() + fighterDTO.last_name();
            frontendDTO.setFighter_2(fighterName);

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
