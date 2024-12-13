package com.mhealth.admin.repository;

import com.mhealth.admin.model.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserLocationRepository extends JpaRepository<UserLocation, Integer> {
    @Query("Select u from UserLocation u where u.user.userId = ?1")
    Optional<UserLocation> findByUserId(Integer userId);

    @Query("SELECT ua FROM UserLocation ua WHERE ua.latitude BETWEEN :minLat AND :maxLat AND ua.longitude BETWEEN :minLng AND :maxLng AND ROUND((6371 * acos(cos(radians(:lat)) * cos(radians(ua.latitude)) * cos(radians(ua.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(ua.latitude)))), 2) <= :range")
    List<UserLocation> findWithinRadius(@Param("minLat") double minLat,@Param("maxLat") double maxLat,
                                        @Param("minLng") double minLng,@Param("maxLng") double maxLng,
                                        @Param("lat") double lat,@Param("lng") double lng,@Param("range") double range);
}