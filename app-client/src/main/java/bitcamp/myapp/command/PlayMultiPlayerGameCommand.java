package bitcamp.myapp.command;

import bitcamp.command.Command;
import bitcamp.context.ApplicationContext;
import bitcamp.util.Prompt;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PlayMultiPlayerGameCommand implements Command {
  ApplicationContext appCtx;

  public PlayMultiPlayerGameCommand(ApplicationContext appCtx) {
    this.appCtx = appCtx;
  }

  @Override
  public void execute(String title) {
    ObjectInputStream in = null;
    ObjectOutputStream out = null;
    Socket socket = null;

    try {
      String host = Prompt.input("서버 주소? ");
      int port = Prompt.inputInt("포트 번호? ");

      socket = new Socket(host, port);

      out = new ObjectOutputStream(socket.getOutputStream());
      in = new ObjectInputStream(socket.getInputStream());

      System.out.println("멀티플레이어 게임을 시작합니다. 다른 플레이어를 기다리고 있습니다...");

      while (true) {
        String message = in.readUTF();
        System.out.println(message);
        if (message.contains("현재 숫자: ")) {
          int count = Prompt.inputInt("몇 개의 숫자를 말하시겠습니까? (1-3)>");
          while (count < 1 || count > 3) {
            count = Prompt.inputInt("잘못된 입력입니다. 다시 입력해주세요 (1-3)>");
          }
          out.writeUTF(String.valueOf(count));
          out.flush();
        }
        if (message.contains("졌습니다") || message.contains("이겼습니다")) {
          break;
        }
      }

    } catch (Exception e) {
      System.out.println("오류 발생: " + e.getMessage());
      e.printStackTrace();
    } finally {
      try {
        if (out != null) {
          out.writeUTF("quit");
          out.flush();
        }
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
      } catch (Exception e) {
        System.out.println("연결 종료 중 오류 발생: " + e.getMessage());
      }
    }
  }
}
