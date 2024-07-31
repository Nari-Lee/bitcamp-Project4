package bitcamp.myapp.command;

import bitcamp.command.Command;

public class HelpCommand implements Command {

  public void execute(String menuName) {
    System.out.println("베스킨라빈스31 게임 도움말");
    System.out.println("1부터 3까지의 숫자를 번갈아 가며 말하다가 31을 말하는 사람이 지는 게임입니다.");
  }
}
