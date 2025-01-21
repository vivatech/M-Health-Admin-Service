package com.mhealth.admin.repository;

import com.mhealth.admin.model.NurseServiceState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NurseServiceStateRepository extends JpaRepository<NurseServiceState, Integer> {

    @Query("Select u from NurseServiceState u where u.orderId = ?1")
    List<NurseServiceState> findByOrderId(Integer id);

    @Query(value = "SELECT state FROM mh_dservice_state WHERE search_id = ?1", nativeQuery = true)
    String findBySearchId(String tripId);

}