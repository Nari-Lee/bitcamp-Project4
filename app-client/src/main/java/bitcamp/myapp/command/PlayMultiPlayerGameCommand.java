package bitcamp.myapp.command;

import bitcamp.command.Command;
import bitcamp.context.ApplicationContext;
import bitcamp.util.Prompt;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PlayMultiPlayerGameCommand implements Command {
  ApplicationContext appCtx;

  public PlayMultiPlayerGameCommand(ApplicationContext appCtx) {
    this.appCtx = appCtx;
  }

  @Override
  public void execute(String title) {
    try {
      ObjectInputStream in = (ObjectInputStream) appCtx.getAttribute("inputStream");
      ObjectOutputStream out = (ObjectOutputStream) appCtx.getAttribute("outputStream");

      if (in == null || out == null) {
        throw new IllegalStateException("입출력 스트림이 초기화되지 않았습니다.");
      }

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
      e.printStackTrace();
    }
  }
}
