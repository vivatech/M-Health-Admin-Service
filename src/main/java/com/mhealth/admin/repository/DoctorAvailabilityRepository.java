package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.model.DoctorAvailability;
import com.mhealth.admin.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Integer> {
    List<DoctorAvailability> findByDoctorId(Users doctor);

    @Query("Select u from DoctorAvailability u where u.slotId.slotId = ?1 and u.doctorId.type = ?2")
    List<DoctorAvailability> findBySlotIdAndUserType(Integer slotId, UserType userType);

    @Query("Select count(u.doctorSlotId) from DoctorAvailability u where u.slotId.slotId = ?1 and u.doctorId.userId = ?2")
    Long countBySlotIdAndDoctorId(Integer slotId, Integer userId);

    @Query("Select count(u.doctorSlotId) from DoctorAvailability u where u.slotTypeId = ?1 and u.slotId.slotId in ?2 AND u.doctorId.userId = ?3")
    Long countBySlotTypeIdAndSlotIdAndDoctorId(Integer slotId, List<Integer> allocatedSlots, Integer doctorId);
}