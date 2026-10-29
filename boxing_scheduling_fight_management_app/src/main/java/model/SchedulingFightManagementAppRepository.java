package model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SchedulingFightManagementAppRepository extends JpaRepository<ScheduledFight, Long>, JpaSpecificationExecutor<ScheduledFight> {
}
