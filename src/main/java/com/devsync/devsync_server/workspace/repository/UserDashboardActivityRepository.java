package com.devsync.devsync_server.workspace.repository;

import com.devsync.devsync_server.workspace.model.UserDashboardActivity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDashboardActivityRepository extends JpaRepository<UserDashboardActivity, Long> {

    Optional<UserDashboardActivity> findByUserIdAndEntityIdAndEntityType(Long userId, Long entityId, String entityType);

    List<UserDashboardActivity> findByUserIdOrderByLastAccessedAtDesc(Long userId, Pageable pageable);
}