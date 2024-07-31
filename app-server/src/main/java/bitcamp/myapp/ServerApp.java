package bitcamp.myapp;

import bitcamp.context.ApplicationContext;
import bitcamp.listener.ApplicationListener;
import bitcamp.myapp.listener.InitApplicationListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerApp {
  List<ApplicationListener> listeners = new ArrayList<>();
  ApplicationContext appCtx = new ApplicationContext();
  List<ClientHandler> clients = new ArrayList<>();
  int currentPlayer = 0;
  int currentNumber = 0;

  public static void main(String[] args) {
    ServerApp app = new ServerApp();
    app.addApplicationListener(new InitApplicationListener());
    app.execute();
  }

  private void addApplicationListener(ApplicationListener listener) {
    listeners.add(listener);
  }

  private void removeApplicationListener(ApplicationListener listener) {
    listeners.remove(listener);
  }

  void execute() {
    try (ServerSocket serverSocket = new ServerSocket(8888)) {
      System.out.println("서버가 시작되었습니다.");

      for (ApplicationListener listener : listeners) {
        listener.onStart(appCtx);
      }

      while (clients.size() < 2) {
        Socket clientSocket = serverSocket.accept();
        ClientHandler clientHandler = new ClientHandler(clientSocket, this, clients.size() + 1);
        clients.add(clientHandler);
        new Thread(clientHandler).start();
      }

      // 모든 클라이언트가 접속한 후 게임을 시작
      startGame();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  synchronized void startGame() {
    clients.get(0).sendMessage("게임이 시작되었습니다. 당신은 플레이어 1입니다.");
    clients.get(1).sendMessage("게임이 시작되었습니다. 당신은 플레이어 2입니다.");
    sendNextTurnMessage();
  }

  synchronized void sendNextTurnMessage() {
    if (currentNumber >= 31) {
      clients.get(currentPlayer).sendMessage("당신이 졌습니다!");
      clients.get(1 - currentPlayer).sendMessage("당신이 이겼습니다!");
      return;
    }
    clients.get(currentPlayer).sendMessage("현재 숫자: " + currentNumber);
  }

  synchronized void handlePlayerInput(int playerNumber, int count) {
    if (playerNumber != currentPlayer + 1) {
      clients.get(playerNumber - 1).sendMessage("지금은 당신의 차례가 아닙니다!");
      return;
    }

    if (count < 1 || count > 3) {
      clients.get(currentPlayer).sendMessage("잘못된 입력입니다. 1-3 사이의 숫자를 입력하세요:");
      return;
    }

    StringBuilder numbers = new StringBuilder();
    for (int i = 1; i <= count; i++) {
      currentNumber++;
      numbers.append(currentNumber).append(" ");
    }

    clients.get(currentPlayer).sendMessage(numbers.toString().trim());
    clients.get(1 - currentPlayer).sendMessage(numbers.toString().trim());

    if (currentNumber >= 31) {
      clients.get(currentPlayer).sendMessage("당신이 졌습니다!");
      clients.get(1 - currentPlayer).sendMessage("당신이 이겼습니다!");
    } else {
      currentPlayer = 1 - currentPlayer;
      sendNextTurnMessage();
    }
  }

  class ClientHandler implements Runnable {
    private Socket socket;
    private ServerApp server;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int playerNumber;

    public ClientHandler(Socket socket, ServerApp server, int playerNumber) {
      this.socket = socket;
      this.server = server;
      this.playerNumber = playerNumber;
      try {
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void run() {
      try {
        while (true) {
          String message = in.readUTF();
          try {
            int count = Integer.parseInt(message);
            server.handlePlayerInput(playerNumber, count);
          } catch (NumberFormatException e) {
            sendMessage("잘못된 입력입니다. 숫자를 입력하세요:");
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          in.close();
          out.close();
          socket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    public void sendMessage(String message) {
      try {
        out.writeUTF(message);
        out.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    public String receiveMessage() {
      try {
        return in.readUTF();
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      }
    }
  }
}
