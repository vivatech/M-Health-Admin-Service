package com.mhealth.admin.repository;

import com.mhealth.admin.model.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate,Integer> {
    @Query("Select u from EmailTemplate u where u.key = :key")
    Optional<EmailTemplate> findByKey(@Param("key") String key);

    @Query("SELECT t FROM EmailTemplate t WHERE (:key IS NULL OR t.key LIKE %:key%) AND (:value IS NULL OR t.value LIKE %:value%) ORDER BY t.createdAt")
    List<EmailTemplate> searchByKeyAndValue(@Param("key") String key, @Param("value") String value);
}
