# DevSync

<p align="center">
  <img src="https://github.com/MGhiremath0281/devsync-server/blob/main/docs/Project-Banner.png" alt="DevSync Banner" width="100%">
</p>

<p align="center">
  <strong>Every Conversation. Every Commit. Every Ticket. Connected.</strong>
</p>

<p align="center">
  A unified developer collaboration platform that seamlessly combines team communication, issue tracking, GitHub workflows, and real-time meetings into a single high-performance workspace.
</p>
<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB" alt="React" />
  <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis" />
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white" alt="Apache Kafka" />
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
  <img src="https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=Prometheus&logoColor=white" alt="Prometheus" />
  <img src="https://img.shields.io/badge/Grafana-F2F4F9?style=for-the-badge&logo=grafana&logoColor=orange&labelColor=F2F4F9" alt="Grafana" />
  <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white" alt="Git" />
</p>

---

## Overview

DevSync is engineered to eliminate context switching, which serves as a major bottleneck in engineering workflows. Instead of scattering operations across separate applications for team communication, ticket tracking, repository notifications, and video calls, DevSync consolidates these systems into a single centralized hub.

Built specifically for agile engineering environments, the platform delivers instant, low-latency collaboration alongside comprehensive visibility into pull requests, system deployments, bug discussions, and live team standups.

---

## Platform Capabilities

| Capability | Specification / Stack |
| :--- | :--- |
| **Architecture** | Microservice-Ready Modular Architecture |
| **Real-Time Communication** | Low-Latency WebSockets (STOMP Protocol) |
| **Authentication** | Dual-Token JWT System (Access + Refresh Rotation) |
| **Security Layer** | BCrypt Password Hashing + Role-Based Access Control (RBAC) |
| **Presence Tracking** | High-Performance Redis Heartbeat Subsystem |
| **Video Infrastructure** | WebRTC-Powered Media Engine |
| **Source Control Synced** | Real-Time GitHub Webhooks |

---

## Core Features

### Team Communication
* **Channel-Based Messaging:** Create dedicated channels for specific projects, topics, or engineering teams.
* **Direct Connections:** Secure peer-to-peer 1:1 messaging workflows.
* **Rich UX Triggers:** Real-time typing indicators, delivery receipts, and live user presence tracking.
* **Developer First:** Built-in code block sharing with robust syntax highlighting.

### Deep GitHub Integration
* **Live Webhooks:** Instant operational notifications for push events and repository activity.
* **Code Review Sync:** Stay updated on pull request states and newly opened issues.
* **Smart Automation:** Automated creation of specialized bug discussion environments tied directly to incoming repository issues.

### Intelligent Bug Management
* **Ticket Lifecycle Tracking:** End-to-end tracking and status updates of system bugs and feature requests.
* **Contextual Communication:** Dedicated channels for open tickets to keep troubleshooting conversations isolated and focused.
* **Persistent Threads:** Ensure technical alignment with centralized, persistent debugging histories.

### DevMeet
* **Native Video Engine:** Secure, browser-native meetings powered by WebRTC.
* **Collaborative Control:** High-fidelity screen sharing alongside optimized audio and video streams.
* **UX Optimizations:** Intelligent active-speaker detection helps engineering teams stay focused during standups.

---

## System Architecture

<p align="center">
  <img src="https://github.com/MGhiremath0281/devsync-server/blob/main/docs/System-Design.png" alt="System Design" width="100%">
</p>

DevSync utilizes a reactive, event-driven communications backbone. WebSockets (STOMP) act as the central message bus across all platform features. GitHub webhooks, chat interactions, issue tracking updates, media signaling, and Redis heartbeats are tightly coordinated to ensure data states remain perfectly synchronized across client instances instantly.

---

## Technology Stack

### Backend & Data
* **Language & Runtime:** Java (OpenJDK)
* **Framework Layer:** Spring Boot, Spring Security, Spring Data JPA
* **Messaging & Live Flow:** WebSocket (STOMP), Redis Pub/Sub
* **Storage & Caching:** MySQL, Redis
* **Build Automation:** Maven

### Frontend Studio
* **Core Languages:** React, TypeScript
* **Styling Engine:** Tailwind CSS
* **Build Tooling:** Vite
* **HTTP Client:** Axios

### DevOps & Infrastructure
* **CI/CD Pipelines:** GitHub Actions
* **Containerization:** Docker
* **Webhook Ecosystem:** GitHub Webhooks

---

## Repository Links

To explore the frontend implementation of the DevSync ecosystem, check out the client repository below:

*   **Frontend Client:** [DevSync Client GitHub](https://github.com/Mahesh2611975/devsync-cliente) — Built with React, featuring real-time collaborative interfaces and telemetry dashboards.

---

## Engineering Roadmap

### Version 1.1 — AI-Augmented Workflows
- [ ] **AI Code Assistant:** Contextual code generation and PR reviews inside chat rooms.
- [ ] **Automated Meeting Summaries:** Post-meeting audio transcripts and key action item generation.
- [ ] **Smart Stack Trace Analysis:** Automated bug room data enrichment via log parsing.

### Version 1.2 — Ecosystem Expansion
- [ ] **Native Cross-Platform Apps:** Dedicated iOS and Android clients using React Native.
- [ ] **System Push Notifications:** OS-level workspace alerts.
- [ ] **Mobile DevMeet:** WebRTC optimization for cellular networks.

---

## Contributors

The engineering team driving the development and maintenance of the DevSync platform ecosystem.

<table align="center" width="100%">
  <thead>
    <tr>
      <th align="center" colspan="2" width="50%"><h3>Backend Engineering</h3></th>
      <th align="center" colspan="2" width="50%"><h3>Frontend Engineering</h3></th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <!-- Backend User 1 -->
      <td align="center" valign="top" width="25%">
        <a href="https://github.com/MGhiremath0281">
          <img src="https://github.com/MGhiremath0281.png" width="100px" alt="MGhiremath0281" style="border-radius: 50%; max-width: 100%;"/>
          <br />
          <sub><b>MGhiremath0281</b></sub>
        </a>
      </td>
      <!-- Backend User 2 -->
      <td align="center" valign="top" width="25%">
        <a href="https://github.com/NitishKumar200415">
          <img src="https://github.com/NitishKumar200415.png" width="100px" alt="NitishKumar200415" style="border-radius: 50%; max-width: 100%;"/>
          <br />
          <sub><b>NitishKumar200415</b></sub>
        </a>
      </td>
      <!-- Frontend User 1 -->
      <td align="center" valign="top" width="25%">
        <a href="https://github.com/Mahesh2611975">
          <img src="https://github.com/Mahesh2611975.png" width="100px" alt="Mahesh2611975" style="border-radius: 50%; max-width: 100%;"/>
          <br />
          <sub><b>Mahesh2611975</b></sub>
        </a>
      </td>
      <!-- Frontend User 2 -->
      <td align="center" valign="top" width="25%">
        <a href="https://github.com/mouneshnayak766-gif">
          <img src="https://github.com/mouneshnayak766-gif.png" width="100px" alt="mouneshnayak766-gif" style="border-radius: 50%; max-width: 100%;"/>
          <br />
          <sub><b>mouneshnayak766-gif</b></sub>
        </a>
      </td>
    </tr>
  </tbody>
</table>

---

<p align="center">
  Built by the DevSync Project Contributors.<br/>
  <strong>Unifying development workflows, communications, and live collaboration mechanisms into a single cohesive system.</strong>
</p>
