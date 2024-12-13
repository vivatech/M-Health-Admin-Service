package com.mhealth.admin.repository;

import com.mhealth.admin.model.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StateRepository extends JpaRepository<State, Integer> {

    @Query("SELECT s FROM State s JOIN FETCH s.country c WHERE c.id IS NOT NULL")
    List<State> findStatesWithExistingCountry();
}