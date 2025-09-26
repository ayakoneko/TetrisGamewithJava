package tetris.controller.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import tetris.model.external.OpMove;
import tetris.model.external.PureGame;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * TetrisServerClient: Handles communication with the external TetrisServer.
 *
 * Features:
 * - Connects to localhost:3000
 * - Sends PureGame state and receives OpMove response
 * - Handles connection failures gracefully
 * - Creates new connection for each request (as per spec)
 */
public class TetrisServerClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3000;
    private static final int CONNECTION_TIMEOUT = 2000; // 2 seconds
    private static final int READ_TIMEOUT = 5000; // 5 seconds

    private final ObjectMapper mapper;
    private boolean serverAvailable = false;
    private long lastConnectionAttempt = 0;
    private static final long RECONNECTION_DELAY = 3000; // 3 seconds between reconnection attempts

    public TetrisServerClient() {
        this.mapper = new ObjectMapper();
    }

    /**
     * Sends game state to server and gets optimal move.
     * Returns null if server is unavailable or communication fails.
     */
    public OpMove getOptimalMove(PureGame gameState) {
        // Don't spam connection attempts
        long now = System.currentTimeMillis();
        if (!serverAvailable && (now - lastConnectionAttempt) < RECONNECTION_DELAY) {
            return null;
        }

        lastConnectionAttempt = now;

        try (Socket socket = new Socket()) {
            // Set connection timeout
            socket.setSoTimeout(READ_TIMEOUT);
            socket.connect(new java.net.InetSocketAddress(SERVER_HOST, SERVER_PORT), CONNECTION_TIMEOUT);

            try (PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Convert PureGame to JSON and send
                String jsonGameState = mapper.writeValueAsString(gameState);
                out.println(jsonGameState);

                System.out.println("[EXTERNAL] Sent game state to server");

                // Read response
                String response = in.readLine();
                if (response == null || response.trim().isEmpty()) {
                    System.err.println("[EXTERNAL] Server returned empty response");
                    serverAvailable = false;
                    return null;
                }

                // Parse OpMove response
                OpMove move = mapper.readValue(response, OpMove.class);

                // Server responded successfully
                if (!serverAvailable) {
                    System.out.println("[EXTERNAL] Server connection established/restored");
                }
                serverAvailable = true;

                System.out.println("[EXTERNAL] Received move: X=" + move.opX() + ", Rotate=" + move.opRotate());
                return move;

            } catch (SocketTimeoutException e) {
                System.err.println("[EXTERNAL] Server response timeout");
                serverAvailable = false;
                return null;
            }

        } catch (ConnectException e) {
            if (serverAvailable) {
                System.err.println("[EXTERNAL] Server connection lost - switching to warning mode");
            }
            serverAvailable = false;
            return null;

        } catch (IOException e) {
            System.err.println("[EXTERNAL] Communication error: " + e.getMessage());
            serverAvailable = false;
            return null;

        } catch (Exception e) {
            System.err.println("[EXTERNAL] Unexpected error: " + e.getMessage());
            serverAvailable = false;
            return null;
        }
    }

    /**
     * Checks if the server is currently available.
     */
    public boolean isServerAvailable() {
        return serverAvailable;
    }

    /**
     * Resets the server availability status to allow immediate reconnection attempt.
     */
    public void resetConnectionStatus() {
        lastConnectionAttempt = 0;
        serverAvailable = false;
    }
}