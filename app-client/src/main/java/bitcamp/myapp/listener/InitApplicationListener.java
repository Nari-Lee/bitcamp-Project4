package bitcamp.myapp.listener;

import bitcamp.command.Command;
import bitcamp.context.ApplicationContext;
import bitcamp.listener.ApplicationListener;
import bitcamp.menu.MenuGroup;
import bitcamp.menu.MenuItem;
import bitcamp.myapp.command.HelpCommand;
import bitcamp.myapp.command.PlayMultiGameCommand;
import bitcamp.myapp.command.PlaySingleGameCommand;

public class InitApplicationListener implements ApplicationListener {

  @Override
  public void onStart(ApplicationContext ctx) throws Exception {
    MenuGroup mainMenu = new MenuGroup("메인 메뉴");

    mainMenu.add(new MenuItem("혼자하기", (Command) new PlaySingleGameCommand(ctx)));
    mainMenu.add(new MenuItem("멀티게임", (Command) new PlayMultiGameCommand(ctx)));
    mainMenu.add(new MenuItem("도움말", new HelpCommand()));

    mainMenu.setExitMenuTitle("종료");
    ctx.setAttribute("mainMenu", mainMenu);
  }

  @Override
  public void onShutdown(ApplicationContext ctx) throws Exception {
    System.out.println("클라이언트 애플리케이션을 종료합니다.");
    // 종료 작업 수행
  }
}
