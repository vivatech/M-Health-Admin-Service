package com.mhealth.admin.repository;

import com.mhealth.admin.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LanguageRepository extends JpaRepository<Language, Integer> {
    @Query("Select u from Language u where u.status LIKE ?1")
    List<Language> findAllByStatus(String a);

    @Query("Select u from Language u where u.id in ?1")
    List<Language> findByIds(List<Integer> langIds);

    List<Language> findByStatus(String status);

    @Query("Select u.name from Language u where u.id in ?1")
    List<String> findLanguages(List<Integer> lang);
}