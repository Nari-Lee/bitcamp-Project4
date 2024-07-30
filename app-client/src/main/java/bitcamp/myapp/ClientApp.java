package bitcamp.myapp;

import bitcamp.context.ApplicationContext;
import bitcamp.menu.MenuGroup;
import bitcamp.menu.MenuItem;
import bitcamp.myapp.command.PlaySinglePlayerGameCommand;

public class ClientApp {
  public static void main(String[] args) {
    ApplicationContext ctx = new ApplicationContext();

    MenuGroup mainMenu = ctx.getMainMenu();
    mainMenu.add(new MenuItem("혼자하기", new PlaySinglePlayerGameCommand()));
    mainMenu.add(new MenuItem("멀티게임")); // 멀티게임은 구현되지 않았음
    mainMenu.add(new MenuItem("도움말", title -> {
      System.out.println("베스킨라빈스31 게임 도움말");
      System.out.println("1부터 3까지의 숫자를 번갈아 가며 말하다가 31을 말하는 사람이 지는 게임입니다.");
    }));

    mainMenu.execute();
  }
}
