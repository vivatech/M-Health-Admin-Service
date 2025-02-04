package com.mhealth.admin.repository;

import com.mhealth.admin.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Integer> {
    @Query("Select u from City u order by u.name asc")
    List<City> findAllByNameAsc();

    List<City> findByState_IdIn(List<Integer> stateIds);
}