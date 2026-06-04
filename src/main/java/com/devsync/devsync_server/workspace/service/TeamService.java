package com.devsync.devsync_server.workspace.service;

import com.devsync.devsync_server.auth.entity.User;
import com.devsync.devsync_server.auth.repository.UserRepository;
import com.devsync.devsync_server.notification.dto.CreateNotificationRequest;
import com.devsync.devsync_server.notification.service.NotificationService;
import com.devsync.devsync_server.workspace.dto.TeamMemberDTO;
import com.devsync.devsync_server.workspace.model.Team;
import com.devsync.devsync_server.workspace.model.TeamMembership;
import com.devsync.devsync_server.workspace.repository.TeamMembershipRepository;
import com.devsync.devsync_server.workspace.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public Team createTeam(Long userId, String name, boolean isPrivate) {
        if (teamRepository.findByName(name).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Workspace name already claimed!");
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

    @Transactional(readOnly = true)
    public List<Team> getMyTeams(Long userId) {
        List<TeamMembership> memberships = membershipRepository.findByUserIdAndStatus(userId, "APPROVED");
        List<Long> teamIds = memberships.stream().map(TeamMembership::getTeamId).toList();
        return teamRepository.findAllById(teamIds);
    }

    @Transactional(readOnly = true)
    public Team getTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));
    }

    @Transactional(readOnly = true)
    public Team getTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found"));
    }

    @Transactional(readOnly = true)
    public boolean isUserMember(Long userId, Long teamId) {
        return membershipRepository.findByUserIdAndTeamId(userId, teamId)
                .map(member -> member.getStatus().equals("APPROVED"))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public List<TeamMemberDTO> getTeamMembersWithNames(Long teamId) {
        return membershipRepository.findByTeamId(teamId).stream().map(m -> {
            User user = userRepository.findById(m.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            return new TeamMemberDTO(m.getUserId(), user.getUsername(), m.getRole(), m.getStatus());
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<TeamMembership> getTeamMembers(Long teamId) {
        getTeamById(teamId);
        return membershipRepository.findByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public long getTotalMembers(Long teamId) {
        return membershipRepository.findByTeamId(teamId).stream()
                .filter(membership -> "APPROVED".equals(membership.getStatus()))
                .count();
    }

    @Transactional
    public TeamMembership joinTeam(Long userId, Long teamId) {
        Team team = getTeamById(teamId);
        if (membershipRepository.findByUserIdAndTeamId(userId, teamId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already joined");
        }

        String status = team.isPrivate() ? "PENDING" : "APPROVED";
        TeamMembership membership = TeamMembership.builder()
                .userId(userId)
                .teamId(teamId)
                .role("MEMBER")
                .status(status)
                .joinedAt(status.equals("APPROVED") ? LocalDateTime.now() : null)
                .build();

        TeamMembership savedMembership = membershipRepository.save(membership);

        if (team.isPrivate()) {
            notificationService.createNotification(CreateNotificationRequest.builder()
                    .userId(team.getOwnerId())
                    .title("Join Request Pending")
                    .message("A user has requested to join your private team: " + team.getName())
                    .build());
        }

        return savedMembership;
    }

    @Transactional
    public TeamMembership approveMember(Long userId, Long requestId) {
        TeamMembership membership = membershipRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid requestId"));

        Team team = getTeamById(membership.getTeamId());
        if (!team.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only team lead can approve");
        }

        membership.setStatus("APPROVED");
        membership.setJoinedAt(LocalDateTime.now());
        TeamMembership savedMembership = membershipRepository.save(membership);

        notificationService.createNotification(CreateNotificationRequest.builder()
                .userId(membership.getUserId())
                .title("Join Request Approved")
                .message("You have been approved to join the team: " + team.getName())
                .build());

        return savedMembership;
    }

    @Transactional
    public void removeMember(Long requesterId, Long teamId, Long memberUserIdToRemove) {
        Team team = getTeamById(teamId);

        if (!team.getOwnerId().equals(requesterId) && !requesterId.equals(memberUserIdToRemove)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to remove members");
        }

        TeamMembership membership = membershipRepository.findByUserIdAndTeamId(memberUserIdToRemove, teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership not found"));

        if (membership.getRole().equals("LEAD")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot remove the team owner");
        }

        membershipRepository.delete(membership);
    }

    @Transactional
    public TeamMembership addMember(Long requesterId, Long teamId, String email) {
        log.info("Add member request received. requesterId={}, teamId={}, email={}", requesterId, teamId, email);

        Team team = getTeamById(teamId);
        log.info("Team found. teamId={}, teamName={}, ownerId={}", team.getId(), team.getName(), team.getOwnerId());

        if (!team.getOwnerId().equals(requesterId)) {
            log.warn("Unauthorized add member attempt. requesterId={} is not owner of teamId={}", requesterId, teamId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only team owner can add members");
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("User not found for email={}", email);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        });
        log.info("User found. userId={}, username={}, email={}", user.getId(), user.getUsername(), user.getEmail());

        if (user.getId().equals(requesterId)) {
            log.warn("Owner attempted to add himself. userId={}, teamId={}", requesterId, teamId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are already a member of this team");
        }

        if (membershipRepository.existsByUserIdAndTeamId(user.getId(), teamId)) {
            log.warn("Duplicate membership prevented. userId={}, teamId={}", user.getId(), teamId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already a member of this team");
        }

        TeamMembership membership = TeamMembership.builder()
                .userId(user.getId())
                .teamId(teamId)
                .role("MEMBER")
                .status("APPROVED")
                .joinedAt(LocalDateTime.now())
                .build();

        TeamMembership savedMembership = membershipRepository.save(membership);
        log.info("Member added successfully. membershipId={}, userId={}, teamId={}",
                savedMembership.getId(), savedMembership.getUserId(), savedMembership.getTeamId());

        notificationService.createNotification(CreateNotificationRequest.builder()
                .userId(user.getId())
                .title("Added To Team")
                .message("You were added to " + team.getName())
                .build());

        return savedMembership;
    }
}