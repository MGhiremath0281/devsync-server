package com.devsync.devsync_server.workspace.service;

import com.devsync.devsync_server.workspace.model.Team;
import com.devsync.devsync_server.workspace.model.TeamMembership;
import com.devsync.devsync_server.workspace.repository.TeamMembershipRepository;
import com.devsync.devsync_server.workspace.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMembershipRepository membershipRepository;

    public Team createTeam(Long userId, String name, boolean isPrivate) {

        if (teamRepository.findByName(name).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Workspace name already claimed!"
            );
        }

        Team team = Team.builder()
                .name(name)
                .ownerId(userId)
                .isPrivate(isPrivate)
                .build();

        Team savedTeam = teamRepository.save(team);

        TeamMembership membership = TeamMembership.builder()
                .userId(userId)
                .teamId(savedTeam.getId())
                .role("LEAD")
                .status("APPROVED")
                .joinedAt(LocalDateTime.now())
                .build();

        membershipRepository.save(membership);

        return savedTeam;
    }

    public List<Team> getMyTeams(Long userId) {

        List<TeamMembership> memberships =
                membershipRepository.findByUserIdAndStatus(
                        userId,
                        "APPROVED"
                );

        List<Long> teamIds = memberships.stream()
                .map(TeamMembership::getTeamId)
                .toList();

        return teamRepository.findAllById(teamIds);
    }

    public Team getTeam(Long teamId) {

        return teamRepository.findById(teamId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Team not found"
                        )
                );
    }

    public TeamMembership joinTeam(Long userId, Long teamId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Workspace doesn't exist"
                        )
                );

        if (membershipRepository.findByUserIdAndTeamId(userId, teamId).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Already joined"
            );
        }

        String status = team.isPrivate()
                ? "PENDING"
                : "APPROVED";

        TeamMembership membership = TeamMembership.builder()
                .userId(userId)
                .teamId(teamId)
                .role("DEVELOPER")
                .status(status)
                .joinedAt(
                        status.equals("APPROVED")
                                ? LocalDateTime.now()
                                : null
                )
                .build();

        return membershipRepository.save(membership);
    }

    public TeamMembership approveMember(Long userId, Long requestId) {

        TeamMembership membership = membershipRepository.findById(requestId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Invalid requestId"
                        )
                );

        Team team = teamRepository.findById(membership.getTeamId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Team not found"
                        )
                );

        if (!team.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only team lead can approve"
            );
        }

        membership.setStatus("APPROVED");
        membership.setJoinedAt(LocalDateTime.now());

        return membershipRepository.save(membership);
    }
}