package bitcamp.myapp.skel;

import bitcamp.myapp.ServerApp;
import bitcamp.myapp.listener.InitApplicationListener;

import java.util.ArrayList;
import java.util.List;

public class GameRoomProcess {
    private final String roomName;
    private final int maxPlayers;
    private final int endNumber;
    private final List<InitApplicationListener> clients;
    private boolean gameStarted;
    private int currentNumber;
    private ServerApp server;

    public GameRoomProcess(String roomName, int maxPlayers, int endNumber, ServerApp server) {
        this.roomName = roomName;
        this.maxPlayers = maxPlayers;
        this.endNumber = endNumber;
		this.clients = new ArrayList<>();
        this.gameStarted = false;
        this.currentNumber = 0;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getNumberOfPlayers() {
        return clients.size();
    }

    public boolean isEmpty() {
        return clients.isEmpty();
    }

    public void addClient(InitApplicationListener clientHandler) {
        if (clients.size() < maxPlayers) {
            clients.add(clientHandler);
            if (clients.size() == maxPlayers) {
                startGame();
            }
        } else {
            clientHandler.sendMessage("Room is full. Cannot add more players.");
        }
    }

    public void removeClient(InitApplicationListener clientHandler) {
        clients.remove(clientHandler);
        if (clients.isEmpty()) {
            GamePlayProcess.removeClientFromRoom(roomName, clientHandler);
        }
    }

    public void startGame() {
        if (!gameStarted) {
            gameStarted = true;
            notifyAllClients("Game started! Current number is: " + currentNumber);
        }
    }

    public synchronized void playTurn(InitApplicationListener player, int count) {
        if (!gameStarted) {
            player.sendMessage("Game has not started yet.");
            return;
        }

        if (count < 1 || count > 3) {
            player.sendMessage("Invalid number of counts. Choose between 1 and 3.");
            return;
        }

        for (int i = 0; i < count; i++) {
            currentNumber++;
            if (currentNumber >= endNumber) {
                notifyAllClients("Game over! Number " + currentNumber + " reached the end. " + player.getClientName() + " lost!");
                resetGame();
                return;
            }
        }

        notifyAllClients(player.getClientName() + " called " + count + " numbers. Current number is: " + currentNumber);
    }

    private void notifyAllClients(String message) {
        for (InitApplicationListener client : clients) {
            client.sendMessage(message);
        }
    }

    private void resetGame() {
        currentNumber = 0;
        gameStarted = false;
        notifyAllClients("Game has been reset.");
    }
}
