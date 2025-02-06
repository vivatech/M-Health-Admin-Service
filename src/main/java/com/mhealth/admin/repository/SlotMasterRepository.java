package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.RequestType;
import com.mhealth.admin.model.SlotMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SlotMasterRepository extends JpaRepository<SlotMaster, Integer> {

    @Query("Select distinct(u) from SlotMaster u where u.slotType.id= ?1 and u.slotDay = ?2 and u.slotTime in ?3")
    List<SlotMaster> findBySlotTypeIdAndSlotDayAndSlotTimeIn(Integer id, String slotDay, List<String> slotTime);

    @Query("Select u.slotId from SlotMaster u where u.slotDay IN ?1 AND u.slotType.id = 4 and u.slotStartTime > ?2 AND u.slotId NOT IN (SELECT c.slotId.slotId FROM Consultation c WHERE c.requestType IN ?4 AND c.consultationDate >= ?3) ORDER BY u.slotStartTime ASC")
    List<Integer> findBySlotDayAndSlotStartTime(String[] dayName, LocalTime time, LocalDate date, List<RequestType> type);
    @Query("Select u.slotId from SlotMaster u where u.slotDay IN ?1 AND u.slotType.id = 4 AND u.slotId NOT IN (SELECT c.slotId.slotId FROM Consultation c WHERE c.requestType IN ?4 AND c.consultationDate >= ?2 AND c.consultationDate <= ?3) ORDER BY u.slotStartTime ASC")
    List<Integer> findBySlotDay(String[] dayName, LocalDate startDate, LocalDate endDate, List<RequestType> type);

    SlotMaster findBySlotId(Integer slotId);
}