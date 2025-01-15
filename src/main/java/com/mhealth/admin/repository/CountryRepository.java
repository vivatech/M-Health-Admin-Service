package com.mhealth.admin.repository;

import com.mhealth.admin.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country,Integer> {
    List<Country> findByIdIn(List<Integer> ids);

    List<Country> findByPhonecodeIn(List<Integer> phonecodeList);

    List<Country> findByIdInAndPhonecodeIn(List<Integer> ids, List<Integer> phonecodeList);
}
