package com.mhealth.admin.repository;

import com.mhealth.admin.model.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Integer> {

    @Query("Select u from Zone u where u.name LIKE %:name% and (:status IS NULL or u.status = :status)")
    Page<Zone> findByNameContainingAndStatus(
            @Param("name") String name,@Param("status") String status, Pageable pageable);

    @Query("select u from Zone u where u.name = :name")
    Optional<Zone> findByName(@Param("name") String name);
}
