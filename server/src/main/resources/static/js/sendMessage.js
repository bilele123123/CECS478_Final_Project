// Demo client: fetch JWT, connect via SockJS/STOMP, send a message, then exit.
// WARNING: disables cert verification for self-signed certs (demo-only).

process.env.NODE_TLS_REJECT_UNAUTHORIZED = '0'; // demo-only

const fetch = require('node-fetch'); // v2 syntax works with Node 18+
const SockJS = require('sockjs-client');
const { Client } = require('@stomp/stompjs');

const SERVER_BASE = process.env.SERVER_BASE || 'https://chat-server:8443';
const TOKEN_PATH = process.env.TOKEN_PATH || '/token';
const USERNAME = process.env.DEMO_USERNAME || 'demoUser';
const MESSAGE = process.env.DEMO_MESSAGE || 'Hello from automated demo!';

// Fetch JWT token
async function fetchToken() {
    const url = `${SERVER_BASE}${TOKEN_PATH}?username=${encodeURIComponent(USERNAME)}`;
    console.log('Fetching token from:', url);

    const res = await fetch(url);
    if (!res.ok) {
        throw new Error(`Token request failed: ${res.status} ${res.statusText}`);
    }

    const token = await res.text();
    return token.trim();
}

async function run() {
    try {
        const token = await fetchToken();
        console.log('Token received (first 20 chars):', token.slice(0, 20) + '...');

        const client = new Client({
            // SockJS factory to handle Spring Boot handshake
            webSocketFactory: () => {
                // Append token as query param for handshake
                return new SockJS(`${SERVER_BASE}/chat?X-Auth-Token=${encodeURIComponent(token)}`);
            },
            connectHeaders: { 'X-Auth-Token': token },
            debug: () => {}, // suppress verbose logs
            onConnect: () => {
                console.log('STOMP connected.');

                // Publish a message
                client.publish({
                    destination: '/app/send-message',
                    headers: { 'X-Auth-Token': token },
                    body: JSON.stringify({
                        content: MESSAGE,
                        timestamp: new Date().toISOString()
                    })
                });
                console.log('Message published.');

                // disconnect after short delay
                setTimeout(async () => {
                    await client.deactivate();
                    console.log('Client disconnected. Exiting.');
                    process.exit(0);
                }, 500);
            },
            onStompError: (frame) => {
                console.error('Broker reported error:', frame && frame.body);
                process.exit(2);
            },
            onWebSocketError: (evt) => {
                console.error('WebSocket error', evt);
            }
        });

        client.activate();

    } catch (err) {
        console.error('Demo failed:', err.message || err);
        process.exit(1);
    }
}

run();
