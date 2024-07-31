package bitcamp.myapp;

import bitcamp.context.ApplicationContext;
import bitcamp.listener.ApplicationListener;
import bitcamp.menu.MenuGroup;
import bitcamp.myapp.command.PlayMultiPlayerGameCommand;
import bitcamp.myapp.command.PlaySinglePlayerGameCommand;
import bitcamp.myapp.listener.InitApplicationListener;
import bitcamp.util.Prompt;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientApp {

  List<ApplicationListener> listeners = new ArrayList<>();
  ApplicationContext appCtx = new ApplicationContext();

  public static void main(String[] args) {
    ClientApp app = new ClientApp();

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
    try {
      String host = Prompt.input("서버 주소? ");
      int port = Prompt.inputInt("포트 번호? ");

      Socket socket = new Socket(host, port);

      ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
      appCtx.setAttribute("inputStream", in);
      appCtx.setAttribute("outputStream", out);

      // 애플리케이션이 시작될 때 리스너에게 알린다.
      for (ApplicationListener listener : listeners) {
        try {
          listener.onStart(appCtx);
        } catch (Exception e) {
          System.out.println("리스너 실행 중 오류 발생!");
        }
      }

      System.out.println("[베스킨라빈스31 게임]");

      MenuGroup mainMenu = (MenuGroup) appCtx.getAttribute("mainMenu");
      if (mainMenu != null) {
        mainMenu.execute();
      } else {
        System.out.println("메인 메뉴를 초기화하지 못했습니다.");
      }

      out.writeUTF("quit");
      out.flush();

    } catch (Exception ex) {
      System.out.println("실행 오류!");
      ex.printStackTrace();
    }

    System.out.println("종료합니다.");

    Prompt.close();

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
