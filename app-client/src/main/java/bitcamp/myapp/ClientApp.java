package bitcamp.myapp;

import bitcamp.command.AnsiColors;
import bitcamp.context.ApplicationContext;
import bitcamp.listener.ApplicationListener;
import bitcamp.menu.MenuGroup;
import bitcamp.myapp.listener.InitApplicationListener;
import bitcamp.util.Prompt;

import java.util.ArrayList;
import java.util.List;

// 클라이언트 애플리케이션을 실행하는 클래스
public class ClientApp {
  // 애플리케이션의 상태 변화를 알리기 위한 리스너 객체들을 저장하는 리스트
  List<ApplicationListener> listeners = new ArrayList<>();
  // 애플리케이션의 상태 전체를 관리하는 애플리케이션 컨텍스트 객체를 생성
  ApplicationContext appCtx = new ApplicationContext();

  public static void main(String[] args) {
    ClientApp app = new ClientApp();

    app.addApplicationListener(new InitApplicationListener());

    app.execute();
  }

  // 애플리케이션 리스너를 리스트에 추가하는 메서드
  private void addApplicationListener(ApplicationListener listener) {
    listeners.add(listener);
  }
  // 애플리케이션 리스너를 리스트에서 제거하는 메서드
  private void removeApplicationListener(ApplicationListener listener) {
    listeners.remove(listener);
  }

  void execute() {
    try {
      // 애플리케이션이 시작될 때 리스너에게 알린다.
      for (ApplicationListener listener : listeners) {
        try {
          listener.onStart(appCtx);
        } catch (Exception e) {
          System.out.println(AnsiColors.RED + "리스너 실행 중 오류 발생!" + AnsiColors.RESET);
        }
      }

     printBanner();

      // 메인 메뉴를 가져와 실행
      MenuGroup mainMenu = (MenuGroup) appCtx.getAttribute("mainMenu");
      if (mainMenu != null) {
        mainMenu.execute();
      } else {
        System.out.println(AnsiColors.RED + "메인 메뉴를 초기화하지 못했습니다." + AnsiColors.RESET);
      }

    } catch (Exception ex) {
      System.out.println(AnsiColors.RED + "실행 오류!" + AnsiColors.RESET);
      ex.printStackTrace();
    }

    System.out.println(AnsiColors.GREEN + "종료합니다." + AnsiColors.RESET);

    // 프롬프트 종료
    Prompt.close();

    // 애플리케이션이 종료될 때 리스너에게 알린다.
    for (ApplicationListener listener : listeners) {
      try {
        listener.onShutdown(appCtx);
      } catch (Exception e) {
        System.out.println(AnsiColors.RED + "리스너 실행 중 오류 발생!" + AnsiColors.RESET);
      }
    }
  }
  private void printBanner() {
    String banner = AnsiColors.CYAN +
        "==========================================\n" +
        "              베스킨라빈스31 게임          \n" +
        "==========================================\n" + AnsiColors.RESET;
    System.out.print(banner);
  }
}
