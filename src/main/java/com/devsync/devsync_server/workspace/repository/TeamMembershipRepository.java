package com.devsync.devsync_server.workspace.repository;

import com.devsync.devsync_server.workspace.model.TeamMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {
    Optional<TeamMembership> findByUserIdAndTeamId(Long userId, Long teamId);
    List<TeamMembership> findByTeamIdAndStatus(Long teamId, String status);
    boolean existsByUserIdAndTeamIdAndStatus(Long userId, Long teamId, String status);
}