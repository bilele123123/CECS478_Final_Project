[![Java CI](https://github.com/bilele123123/CECS478_Final_Project/actions/workflows/ci.yml/badge.svg)](https://github.com/bilele123123/CECS478_Final_Project/actions/workflows/ci.yml)
# CECS 478 Final Project – Integrated Alpha-Beta Release

## Vertical Slice Demo
<img src="docs/vertical-slice-demo.gif" alt="Project Demo">

## 1. Updated Project Architecture
~~~
├───.github
│   └───workflows // CI Workflows
├───docs // Vertical Slices Demo
├───evidence
│   ├───logs
│   ├───metrics
│   └───pcaps
├───repository_assets
└───server
    ├───src/main/java/com/example/demo
    │   │   ├───config
    │   │   ├───controller
    │   │   ├───model
    │   │   |───service
    │   │   └───resources
    │   │       ├───static
    │   │       │   └───js
    │   │       └───templates
    │   └───test
~~~
### Key Vertical Slice
#### Flow: request → validate JWT → rate-limit → log → publish → capture → export metrics
1. Client (Node.js) requests a token from Spring Boot backend
2. Sends message via WSS to /chat endpoint
3. Backend validates token, applies per-user rate limit, logs message
4. PCAP capture container records network traffic
5. Metrics and logs exported to artifacts/release/

## 2. What Works

| Component                  | Status                                                        |
|----------------------------|---------------------------------------------------------------|
| Spring Boot chat server    | Running, builds successfully with Maven                       |
| WebSocket/STOMP messaging  | Happy-path messages transmitted correctly                     |
| JWT token validation       | Enforced, invalid tokens rejected                             |
| Rate limiting              | 200ms per-user rate limit working                             |
| Logging                    | Messages logged to `LoggingService` and exported to `artifacts/release/logs` |
| PCAP capture               | Captures WSS traffic on port 8443                             |
| Demo client                | Node.js script sends messages, retrieves metrics              |
| Docker setup               | `make up && make demo` works on fresh clone (Windows/Linux)   |
| Testing                    | 4 unit tests (2 happy-path, 2 negative) passing               |
| CI pipeline                | Builds, runs tests, coverage summary produced                 |

Overall the system delivers a secure and functional real time chat pipeline. Messages move correctly through token validation rate limiting logging and WebSocket broadcasting with consistent results. The supporting components including Docker setup PCAP capture the demo client and the CI pipeline all operate as expected. Unit tests confirm proper handling of both valid and invalid messages which shows that the core features work reliably.

## 3. What Doesn’t Work / Known Issues

| Issue                     | Notes / Workaround                                                      |
|---------------------------|------------------------------------------------------------------------|
| Self-signed TLS warnings  | Demo disables cert verification (`NODE_TLS_REJECT_UNAUTHORIZED=0`) – only safe for demo |
| Rate-limiting edge cases  | May not handle concurrent high-volume users perfectly yet              |
| Metrics visualization     | Charts/tables generated manually; no automated dashboard              |

## 4. Security Features Implemented

- **JWT-based authentication** with token cache to prevent reuse or expired tokens
- **Input validation**: reject empty/oversized messages
- **Rate limiting**: per-user 200ms delay between messages
- **Least-privilege execution**: demo-runner container only has access to `/work`
- **Tamper-evident logs**: `LoggingService` records all valid messages and suspicious events

## 5. Testing Coverage

**Alpha-level minimal tests:**

- 1 happy-path message
- 1 negative test (empty message)

**Beta-level robust tests:**

- 1 additional happy-path (short message)
- 1 additional negative test (oversized message)

**CI pipeline:**

- Builds server with Maven wrapper (`./server/mvnw clean test`)
- Runs all tests and reports coverage

## 6. Evidence Collected

- **PCAPs:** `/artifacts/release/pcaps/chat_capture.pcap`
- **Logs:** `/artifacts/release/logs/server.log`
- **Metrics JSON:** `/artifacts/release/metrics/metrics.json`
- Initial results show correct message delivery and timing statistics

## 7. What’s Next

- Complete evaluation analysis (Week 16)
- Implement automated metrics visualization (charts/tables)
- Add concurrent user stress tests to verify rate limiting under load
----

## Runbook - How to Build, Rebuild and Run the Project

### Local Run (No Docker)
- Build project  
  ./mvnw install
- Start server  
  ./mvnw spring-boot:run

### Docker Run
- Windows:  
  make up-windows
- Linux / Mac:  
  make up-linux

### Rebuild (Docker)
- Rebuild all containers and server  
  make rebuild

### Demo Client (Optional)
- Windows:  
  make demo-windows
- Linux / Mac:  
  make demo-linux

### Stop and Clean
- Remove containers and artifacts  
  make clean
  
----
## Final Project Proposal
### 1. Problem Statement:
Modern messaging applications rely heavily on persistent bidirectional communication which makes WebSockets a common choice for real-time exchange of information. However many small or student-built messaging tools implement WebSockets without adequate security controls which leaves them vulnerable to attackers. This project aims to design and implement a security-focused messaging service using WebSockets with the primary goal of demonstrating secure communication patterns in a simple reproducible environment. The system will highlight secure session safe message handling input validation and traffic inspection practices relevant to defensive security engineering.

### 2. Threat Model:
#### Assets
- User messages transmitted over WebSockets
- Session identifiers and authentication tokens
- WebSocket upgrade handshake and underlying TCP channel
- Server-side message routing logic and in-memory message buffer

#### Attacker Model
An attacker may be an individual capable of sniffing or tampering with unprotected traffic, a malicious client attempting to impersonate users. Attackers are assumed not to have root access to the server or Docker host.

#### Attack Surfaces
- WebSocket upgrade handshake
- Plaintext messages sent over an unprotected WS connection
- Message broadcasting pipeline in the Spring server
- Client-side input submission
- Session or token leakage through logs or misconfigured CORS

#### Assumptions
- All testing will occur within a controlled Docker environment
- No sensitive real data will be exchanged
- TLS termination may be simulated rather than fully production-grade
- The system will not perform persistent storage but may buffer messages in memory

#### Defensive Approach
The system will incorporate:
- Secure WebSocket configuration with validation of origins and allowed endpoints
- Token-based session initialization with per-connection validation
- Payload size limits and sanitation filters
- Server-side monitoring hooks to log abnormal behavior
- Optional lightweight detection for message flooding or malformed JSON

### 3. Success Metrics:
Success will be measured using several observable outcomes:
Messages exchanged within the system remain unreadable when intercepted in plaintext unless secure WS (wss) or encrypted channels are used.
Clients without valid tokens cannot join the messaging channel.
The server rejects malformed messages exceeding defined size or structure.
Average round-trip message delivery remains under 150ms in the Docker network.
Security logs: The system generates interpretable alerts for suspicious behavior such as rapid message bursts.

### 4. Dataset / PCAP Plan:
Traffic analysis will be performed using synthetic or self-generated message traffic only. PCAPs will be captured using tcpdump inside the Docker container after obtaining strict consent from all connected clients which in this case will be only the project operator. No external sources or real user data will be used. Captures will focus on observing differences between insecure WS connections and optional encrypted channels as well as reviewing how malformed payloads appear on the wire.

### 5. Risks and Ethics:
Primary risks involve handling raw network captures and logging user messages. To avoid ethical issues all data will be synthetic communication will be limited to test accounts and all logs will exclude sensitive identifiers. The system will run exclusively in the provided Docker environment preventing accidental interception of third-party traffic. Any attack simulation such as flooding malformed JSON or token tampering will be performed against the student-owned container to avoid legal and ethical concerns.

6. Architecture Diagram:
#### High Level Design
![High Level Architecture Diagram](/repository_assets/architecture_diagram.png)
