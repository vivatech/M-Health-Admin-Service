package com.mhealth.admin.repository;

import com.mhealth.admin.model.PartnerNurse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PartnerNurseRepository extends JpaRepository<PartnerNurse, Integer> {
    @Query("Select u.contactNumber from PartnerNurse u where u.contactNumber in ?1")
    List<String> findByContactNumberIn(List<String> contactNumber);

    @Query("Select u.contactNumber from PartnerNurse u where u.contactNumber LIKE ?1 order by u.id DESC")
    List<PartnerNurse> findByContactNumberIdDesc(String nurseMobile);

    @Query("Select COUNT(u) from PartnerNurse u")
    Long countNursePartners();
}