package com.mhealth.admin.repository;

import com.mhealth.admin.model.GlobalConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GlobalConfigurationRepository extends JpaRepository<GlobalConfiguration,Integer> {

    @Query("SELECT m FROM GlobalConfiguration m WHERE m.key LIKE :value")
    Optional<GlobalConfiguration> findByKey(@Param("value") String value);

    List<GlobalConfiguration> findByKeyIn(List<String> turnPassword);

    @Query("Select u from GlobalConfiguration u order by u.displayOrder")
    List<GlobalConfiguration> findAllByDisplayOrder();

    @Query("Select u from GlobalConfiguration u where u.key like %:key% AND u.value LIKE %:value% order by u.displayOrder")
    List<GlobalConfiguration> findAllByDisplayOrderKeyAndValue(
            @Param("key") String key,@Param("value")  String value);
}
