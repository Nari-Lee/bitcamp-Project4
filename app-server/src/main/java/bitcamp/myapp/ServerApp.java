package bitcamp.myapp;

import bitcamp.context.ApplicationContext;
import bitcamp.listener.ApplicationListener;
import bitcamp.myapp.dao.skel.Skel;
import bitcamp.myapp.listener.InitApplicationListener;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerApp {
  List<ApplicationListener> listeners = new ArrayList<>();
  ApplicationContext appCtx = new ApplicationContext();

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
        new Skel(clientSocket).start();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
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
}
