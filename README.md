[![Java CI](https://github.com/bilele123123/CECS478_Final_Project/actions/workflows/ci.yml/badge.svg)](https://github.com/bilele123123/CECS478_Final_Project/actions/workflows/ci.yml)
# Final Project Proposal
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