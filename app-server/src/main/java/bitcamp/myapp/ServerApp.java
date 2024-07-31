package bitcamp.myapp;

import bitcamp.context.ApplicationContext;
import bitcamp.listener.ApplicationListener;
import bitcamp.myapp.listener.InitApplicationListener;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {
  List<ApplicationListener> listeners = new ArrayList<>();
  ApplicationContext appCtx = new ApplicationContext();
  ExecutorService threadPool = Executors.newFixedThreadPool(2);
  List<ClientHandler> clients = new ArrayList<>();

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

      // 애플리케이션이 시작될 때 리스너에게 알린다.
      for (ApplicationListener listener : listeners) {
        try {
          listener.onStart(appCtx);
        } catch (Exception e) {
          System.out.println("리스너 실행 중 오류 발생!");
        }
      }

      while (true) {
        Socket clientSocket = serverSocket.accept();
        ClientHandler clientHandler = new ClientHandler(clientSocket, this);
        clients.add(clientHandler);
        threadPool.execute(clientHandler);
        if (clients.size() == 2) {
          startMultiplayerGame();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      threadPool.shutdown();
      // 애플리케이션이 종료될 때 리스너에게 알린다.
      for (ApplicationListener listener : listeners) {
        try {
          listener.onShutdown(appCtx);
        } catch (Exception e) {
          System.out.println("리스너 실행 중 오류 발생!");
        }
      }
    }
  }

  synchronized void startMultiplayerGame() {
    clients.get(0).sendMessage("멀티플레이어 게임이 시작되었습니다. 당신은 플레이어 1입니다.");
    clients.get(1).sendMessage("멀티플레이어 게임이 시작되었습니다. 당신은 플레이어 2입니다.");

    int currentNumber = 0;
    int currentPlayer = 0;

    while (currentNumber < 31) {
      ClientHandler currentClient = clients.get(currentPlayer);
      currentClient.sendMessage("현재 숫자: " + currentNumber);
      int count = Integer.parseInt(currentClient.receiveMessage());
      currentNumber += count;

      if (currentNumber >= 31) {
        currentClient.sendMessage("당신이 졌습니다.");
        clients.get(1 - currentPlayer).sendMessage("당신이 이겼습니다.");
        break;
      }

      currentPlayer = 1 - currentPlayer;
    }
  }

  public void broadcast(String message, ClientHandler sender) {
    for (ClientHandler client : clients) {
      if (client != sender) {
        client.sendMessage(message);
      }
    }
  }
}

class ClientHandler implements Runnable {
  private Socket socket;
  private ServerApp serverApp;
  private ObjectInputStream in;
  private ObjectOutputStream out;

  public ClientHandler(Socket socket, ServerApp serverApp) {
    this.socket = socket;
    this.serverApp = serverApp;
  }

  @Override
  public void run() {
    try {
      in = new ObjectInputStream(socket.getInputStream());
      out = new ObjectOutputStream(socket.getOutputStream());

      while (true) {
        String message = in.readUTF();
        if (message.equals("quit")) {
          break;
        }
        serverApp.broadcast(message, this);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        in.close();
        out.close();
        socket.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void sendMessage(String message) {
    try {
      out.writeUTF(message);
      out.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String receiveMessage() {
    try {
      return in.readUTF();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
