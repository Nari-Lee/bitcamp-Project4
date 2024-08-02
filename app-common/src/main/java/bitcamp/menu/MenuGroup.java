package bitcamp.menu;

import bitcamp.command.AnsiColors;
import bitcamp.util.Prompt;

import java.util.ArrayList;
import java.util.Stack;

public class MenuGroup extends AbstractMenu {

  private MenuGroup parent;
  private ArrayList<Menu> children = new ArrayList<>();
  private String exitMenuTitle = "이전";

  public MenuGroup(String title) {
    super(title);
  }

  @Override
  public void execute() {
    String menuPath = getMenuPath();

    while (true) {
      printMenus();
      String command = Prompt.input("%s> ", menuPath);
      if (command.equals("menu")) {
        printMenus();
        continue;
      } else if (command.equals("0")) { // 이전 메뉴 선택
        return;
      }

      try {
        int menuNo = Integer.parseInt(command);
        Menu menu = getMenu(menuNo - 1);
        if (menu == null) {
          System.out.println(AnsiColors.RED + "유효한 메뉴 번호가 아닙니다." + AnsiColors.RESET);
          continue;
        }

        menu.execute();

      } catch (NumberFormatException ex) {
        System.out.println(AnsiColors.RED + "숫자로 메뉴 번호를 입력하세요." + AnsiColors.RESET);
      }
    }
  }

  public void setExitMenuTitle(String title) {
    exitMenuTitle = title;
  }

  private void printMenus() {
    StringBuilder menuBuilder = new StringBuilder();
    menuBuilder.append(AnsiColors.YELLOW)
        .append("==========================================\n")
        .append(String.format("            [%s]\n", title))
        .append("==========================================\n");
    int i = 1;
    for (Menu menu : children) {
      menuBuilder.append(String.format("  %d. %s\n", i++, menu.getTitle()));
    }
    menuBuilder.append(String.format("  0. %s\n", exitMenuTitle))
        .append("==========================================\n")
        .append(AnsiColors.RESET);
    System.out.print(menuBuilder.toString());
  }

  private String getMenuPath() {
    Stack<String> menuPathStack = new Stack<>();
    MenuGroup menuGroup = this;
    while (menuGroup != null) {
      menuPathStack.push(menuGroup.title);
      menuGroup = menuGroup.parent;
    }

    StringBuilder strBuilder = new StringBuilder();
    while (!menuPathStack.isEmpty()) {
      if (strBuilder.length() > 0) {
        strBuilder.append("/");
      }
      strBuilder.append(menuPathStack.pop());
    }

    return strBuilder.toString();
  }

  private void setParent(MenuGroup parent) {
    this.parent = parent;
  }

  public void add(Menu child) {
    if (child instanceof MenuGroup) {
      ((MenuGroup) child).setParent(this);
    }
    children.add(child);
  }

  public void remove(Menu child) {
    children.remove(child);
  }

  public Menu getMenu(int index) {
    if (index < 0 || index >= children.size()) {
      return null;
    }
    return children.get(index);
  }

  public int countMenus() {
    return children.size();
  }
}
