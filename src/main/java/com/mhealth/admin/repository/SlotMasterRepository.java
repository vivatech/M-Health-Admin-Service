package com.mhealth.admin.repository;

import com.mhealth.admin.model.SlotMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Time;
import java.util.List;

public interface SlotMasterRepository extends JpaRepository<SlotMaster, Integer> {

    @Query("Select distinct(u) from SlotMaster u where u.slotType.id= ?1 and u.slotDay = ?2 and u.slotTime in ?3")
    List<SlotMaster> findBySlotTypeIdAndSlotDayAndSlotTimeIn(Integer id, String slotDay, List<String> slotTime);
}