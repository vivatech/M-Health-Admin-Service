package com.mhealth.admin.repository;

import com.mhealth.admin.model.StaticPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaticPageRepository extends JpaRepository<StaticPage, Integer> {
    StaticPage findByName(String name);
}
