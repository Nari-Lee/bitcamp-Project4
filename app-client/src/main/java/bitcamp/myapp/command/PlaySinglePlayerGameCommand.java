package bitcamp.myapp.command;

import bitcamp.command.Command;
import bitcamp.context.ApplicationContext;
import bitcamp.myapp.dao.stub.Stub;
import bitcamp.util.Prompt;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PlaySinglePlayerGameCommand implements Command {
  ApplicationContext appCtx;

  public PlaySinglePlayerGameCommand(ApplicationContext appCtx) {
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

      Stub stub = new Stub(out, in);
      int currentNumber = 0;
      boolean isPlayerTurn = true;

      while (currentNumber < 31) {
        if (isPlayerTurn) {
          System.out.println("현재 숫자: " + currentNumber);
          int count = Prompt.inputInt("플레이어, 몇 개의 숫자를 말하시겠습니까? (1-3)>");
          while (count < 1 || count > 3) {
            count = Prompt.inputInt("잘못된 입력입니다. 다시 입력해주세요 (1-3)>");
          }
          printNumbers(currentNumber + 1, currentNumber + count);
          String result = stub.playerTurn(currentNumber, count);
          String[] parts = result.split(" 현재 숫자: ");
          currentNumber = Integer.parseInt(parts[1].trim());
          if (parts[0].contains("플레이어가 졌습니다.")) {
            System.out.println(parts[0].trim());
            break;
          }
        } else {
          String result = stub.computerTurn(currentNumber);
          String[] parts = result.split(" 현재 숫자: ");
          int oldCurrentNumber = currentNumber;
          currentNumber = Integer.parseInt(parts[1].trim());
          System.out.println(parts[0].trim());
          printNumbers(oldCurrentNumber + 1, currentNumber);
          if (parts[0].contains("컴퓨터가 졌습니다.")) {
            break;
          }
        }
        isPlayerTurn = !isPlayerTurn;
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void printNumbers(int start, int end) {
    for (int i = start; i <= end; i++) {
      System.out.println(i);
    }
  }
}
