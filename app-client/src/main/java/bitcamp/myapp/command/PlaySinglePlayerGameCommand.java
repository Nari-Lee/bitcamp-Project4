package bitcamp.myapp.command;

import bitcamp.util.Prompt;

import java.util.Random;

public class PlaySinglePlayerGameCommand implements Command {

  @Override
  public void execute(String title) {
    Random random = new Random();
    int currentNumber = 0;

    System.out.println("혼자하기 게임을 시작합니다.");
    System.out.println("플레이어와 컴퓨터가 번갈아 가며 숫자를 말합니다.");
    System.out.println("마지막으로 31을 말하는 사람이 집니다.");

    while (currentNumber < 31) {
      currentNumber = playerTurn(currentNumber);
      if (currentNumber >= 31) {
        System.out.println("플레이어가 졌습니다. 컴퓨터가 이겼습니다!");
        break;
      }

      currentNumber = computerTurn(random, currentNumber);
      if (currentNumber >= 31) {
        System.out.println("컴퓨터가 졌습니다. 플레이어가 이겼습니다!");
        break;
      }
    }
  }

  private int playerTurn(int currentNumber) {
    System.out.println("현재 숫자: " + currentNumber);

    int count = Prompt.inputInt("플레이어, 몇 개의 숫자를 말하시겠습니까? (1-3)>");
    while (count < 1 || count > 3) {
      count = Prompt.inputInt("잘못된 입력입니다. 다시 입력해주세요 (1-3)>");
    }

    for (int i = 1; i <= count; i++) {
      currentNumber++;
      System.out.println("플레이어: " + currentNumber);
      if (currentNumber >= 31) {
        break;
      }
    }
    return currentNumber;
  }

  private int computerTurn(Random random, int currentNumber) {
    int count = random.nextInt(3) + 1;
    System.out.println("컴퓨터가 " + count + "개의 숫자를 말합니다.");

    for (int i = 1; i <= count; i++) {
      currentNumber++;
      System.out.println("컴퓨터: " + currentNumber);
      if (currentNumber >= 31) {
        break;
      }
    }
    return currentNumber;
  }
}
