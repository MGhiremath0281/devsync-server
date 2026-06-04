package com.devsync.devsync_server.workspace.repository;

import com.devsync.devsync_server.workspace.model.TeamMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {

    Optional<TeamMembership> findByUserIdAndTeamId(Long userId, Long teamId);
    List<TeamMembership> findByTeamIdAndStatus(Long teamId, String status);
    boolean existsByUserIdAndTeamIdAndStatus(Long userId, Long teamId, String status);
    List<TeamMembership> findByUserIdAndStatus(Long userId, String status);
    List<TeamMembership> findByTeamId(Long teamId);
    @Query("SELECT tm FROM TeamMembership tm JOIN User u ON tm.userId = u.id WHERE tm.teamId = :teamId")
    List<TeamMembership> findMembersWithUserInfo(@Param("teamId") Long teamId);

    boolean existsByUserIdAndTeamId(Long userId, Long teamId);
}