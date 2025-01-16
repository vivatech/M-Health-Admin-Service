package com.mhealth.admin.repository;

import com.mhealth.admin.model.SlotMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Time;
import java.util.List;

public interface SlotMasterRepository extends JpaRepository<SlotMaster, Integer> {

    @Query("Select u from SlotMaster u where u.slotType.id= ?1 and u.slotDay = ?2 and u.slotTime in ?3")
    List<SlotMaster> findBySlotTypeIdAndSlotDayAndSlotTimeIn(Integer id, String slotDay, List<String> slotTime);

    @Query("Select u from SlotMaster u where u.slotType.id= ?1 and u.slotDay LIKE ?2 ORDER BY u.slotId ASC")
    List<SlotMaster> findBySlotTypeIdAndSlotDay(Integer slotTypeId, String date);
}