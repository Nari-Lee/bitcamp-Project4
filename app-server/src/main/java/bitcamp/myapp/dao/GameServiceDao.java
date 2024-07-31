package bitcamp.myapp.dao;

import java.util.Random;

public class GameServiceDao implements GameDao {
  private static final long serialVersionUID = 1L;
  private int currentNumber = 0;
  private Random random = new Random();

  @Override
  public String playerTurn(int currentNumber, int count) {
    for (int i = 1; i <= count; i++) {
      currentNumber++;
      this.currentNumber = currentNumber;
      if (currentNumber >= 31) {
        return "플레이어가 졌습니다. 컴퓨터가 이겼습니다!";
      }
    }
    return "플레이어가 숫자를 말했습니다. 현재 숫자: " + currentNumber;
  }

  @Override
  public String computerTurn(int currentNumber) {
    int count;
    if (31 - currentNumber <= 4) {
      count = 31 - currentNumber - 1;
      if (count < 1) {
        count = 1; // 최소 1개는 말하도록 보장
      }
    } else {
      count = random.nextInt(3) + 1;
    }

    for (int i = 1; i <= count; i++) {
      currentNumber++;
      this.currentNumber = currentNumber;
      if (currentNumber >= 31) {
        return "컴퓨터가 졌습니다. 플레이어가 이겼습니다!";
      }
    }
    return "컴퓨터가 " + count + "개의 숫자를 말합니다. 현재 숫자: " + currentNumber;
  }

  @Override
  public int getCurrentNumber() {
    return currentNumber;
  }
}
