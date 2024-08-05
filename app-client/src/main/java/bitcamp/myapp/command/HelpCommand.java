package bitcamp.myapp.command;

import bitcamp.command.AnsiColors;
import bitcamp.command.Command;

public class HelpCommand implements Command {

  public void execute(String menuName) {
    System.out.println(AnsiColors.PURPLE + "베스킨라빈스31 게임 도움말" + AnsiColors.RESET);
    System.out.println("1부터 3까지의 숫자를 번갈아 가며 말하다가 31을 말하는 사람이 지는 게임입니다.");
    System.out.println("싱글 플레이에 중복된 숫자 허용 모드는 중복 숫자를 입력할 수 없는 배스킨라빈스 게임입니다!\n직전 컴퓨터 턴에 컴퓨터가 입력한 개수는 입력할 수 없습니다.\n예) 플레이어 : 2\n    컴퓨터   : 2\n");
  }
}
