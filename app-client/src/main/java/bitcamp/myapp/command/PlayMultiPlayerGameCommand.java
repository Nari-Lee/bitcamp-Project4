package bitcamp.myapp.command;

import bitcamp.command.Command;

public class PlayMultiPlayerGameCommand implements Command {

  @Override
  public void execute(String menuName) {
    System.out.println("준비중입니다.");
  }
}
