package com.mhealth.admin.repository;

import com.mhealth.admin.model.SlotMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

public interface SlotMasterRepository extends JpaRepository<SlotMaster, Integer> {

    @Query("Select u from SlotMaster u where u.slotType.id= ?1 and u.slotDay = ?2 and u.slotStartTime in ?3")
    Collection<SlotMaster> findBySlotTypeIdAndSlotDayAndSlotStartTimeIn(Integer id, String slotDay, List<LocalTime> slotStartTime);

    @Query("Select u from SlotMaster u where u.slotType.id= ?1 and u.slotDay LIKE ?2 ORDER BY u.slotId ASC")
    List<SlotMaster> findBySlotTypeIdAndSlotDay(Integer slotTypeId, String date);
}