package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.model.City;
import com.mhealth.admin.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users,Integer>, JpaSpecificationExecutor<Users> {

    @Query("Select u from Users u where u.userId = ?1")
    Optional<Users> findById(Integer id);

    @Query("Select u from Users u where u.contactNumber like ?1")
    Optional<Users> findByContactNumber(String contactNumber);

    @Query("Select c from City c where c.id in (Select u.city from Users u where u.type = ?1 group by u.city)")
    List<City> getCitiesByUsertype(UserType userType);

    @Query("Select u from Users u where u.status like ?1 and u.type = ?2 and u.isVerified like ?3")
    List<Users> findByStatusAndTypeAndVerified(String a, UserType userType, String yes);


    List<Users> findByTypeAndStatus(UserType userType, StatusAI statusAI);

    List<Users> findByHospitalId(int hospitalId);

    @Query("Select u from Users u where u.status LIKE ?1 and u.type = ?2 order by u.sort asc")
    List<Users> findByStatusAndTypeOrderByAsc(String status,UserType type);

    @Query("SELECT u FROM Users u WHERE u.type = 'Doctor' AND u.status = 'A' AND u.hasDoctorVideo IN ('visit', 'both') AND u.hospitalId > 0")
    List<Users> findActiveDoctorsWithVideoAndHospital();
    
    @Query("SELECT o.doctorId.userId FROM Orders o GROUP BY o.doctorId.userId ORDER BY COUNT(o.doctorId.userId) DESC")
    List<Integer> findTopDoctors();

    @Query("SELECT u FROM Users u WHERE u.userId IN (?1)")
    List<Users> findDoctorsByIds(List<Integer> topDoctorIds);

    @Query("SELECT u FROM Users u WHERE u.type = 'Doctor' AND u.status = 'A' AND u.hospitalId IN ?1 AND u.hasDoctorVideo IN ('visit', 'both') AND u.hospitalId > 0")
    List<Users> findNearbyDoctors(List<Integer> hospitalIds);


    @Query("select u from Users u where u.contactNumber = ?1 and u.type = ?2")
    Users findByContactNumberAndType(String contactNumber, UserType userType);

    @Query("Select count(u) from Users u where u.type = ?1")
    Long countUsersByType(UserType userType);

    @Query("SELECT COUNT(DISTINCT u.doctorId) FROM DoctorAvailability u " +
            " WHERE u.slotId.slotDay = ?1")
    Long countAvailableDoctors(String day);

    @Query("Select count(u) from Users u where u.type = ?1 AND u.status = ?2")
    Long countUsersByTypeAndStatus(UserType userType, StatusAI statusAI);

    long countByEmail(String email);
  
    long countByContactNumberAndType(String contactNumber, UserType type);
  
    long countByEmailAndUserIdNot(String email, Integer userId);
  
    long countByContactNumberAndTypeAndUserIdNot(String contactNumber, UserType type, Integer userId);

    Optional<Users> findByUserIdAndType(Integer userId, UserType type);
  
    Users findByUserIdAndType(Users userId, String userType);

    @Query("""
    SELECT u
    FROM Users u
    WHERE u.type = 'Clinic'
    AND (:name IS NULL OR u.clinicName LIKE :name)
    AND (:email IS NULL OR u.email LIKE :email)
    AND (:status IS NULL OR u.status = :status)
    AND (:contactNumber IS NULL OR u.contactNumber LIKE :contactNumber)
""")
    Page<Users> findHospitalsWithFilters(
            @Param("name") String name,
            @Param("email") String email,
            @Param("status") StatusAI status,
            @Param("contactNumber") String contactNumber,
            Pageable pageable
    );

    Users findBySort(Integer priority);

    @Query("""
    SELECT u
    FROM Users u
    WHERE u.type = 'Agentuser'
    AND u.status is not null
    AND (:name IS NULL OR CONCAT(u.firstName, ' ', u.lastName) LIKE :name)
    AND (:email IS NULL OR u.email LIKE :email)
    AND (:status IS NULL OR u.status = :status)
    AND (:contactNumber IS NULL OR u.contactNumber LIKE :contactNumber)
""")
    Page<Users> findAgentWithFilters(
            @Param("name") String name,
            @Param("email") String email,
            @Param("status") StatusAI status,
            @Param("contactNumber") String contactNumber,
            Pageable pageable
    );

    @Query(value = "SELECT u.* FROM mh_users u WHERE u.status = 'A' AND u.type = 'Clinic' AND u.is_hpcz_verified = 'Yes'",nativeQuery = true)
    List<Users> getHospitalList();

    Users findByUserIdAndStatus(Integer hospitalId, StatusAI statusAI);
}
