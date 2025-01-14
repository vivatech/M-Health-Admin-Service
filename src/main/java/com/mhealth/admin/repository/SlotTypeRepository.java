package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.SlotStatus;
import com.mhealth.admin.model.SlotType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SlotTypeRepository extends JpaRepository<SlotType, Integer> {
    @Query("Select u from SlotType u where u.status = :active")
    List<SlotType> findByStatus(@Param("active") SlotStatus active);

    @Query("Select u from SlotType u where u.type = :type")
    Optional<SlotType> findByType(@Param("type") String type);

    @Query(value = "SELECT id FROM mh_slot_type WHERE status = :status LIMIT 1", nativeQuery = true)
    Optional<Integer> findDefaultSlot(@Param("status") String status);
}