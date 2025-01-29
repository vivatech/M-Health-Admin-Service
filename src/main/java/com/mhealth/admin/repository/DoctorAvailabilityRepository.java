package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.model.DoctorAvailability;
import com.mhealth.admin.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Integer> {
    List<DoctorAvailability> findByDoctorId(Users doctor);

    @Query(value = """
            SELECT s.slot_id, s.slot_day, s.slot_time, s.slot_type_id, s.slot_start_time FROM mh_doctor_availability u 
            LEFT JOIN mh_slot_master s ON s.slot_id = u.slot_id WHERE u.doctor_id = ?1 and s.slot_type_id = ?2 
            """, nativeQuery = true)
    List<Object[]> findByAvailabilityByDoctorId(Integer userId, Integer slotTypeId);
}