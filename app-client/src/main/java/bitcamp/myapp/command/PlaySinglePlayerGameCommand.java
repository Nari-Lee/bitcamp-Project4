package bitcamp.myapp.command;

import bitcamp.command.Command;
import bitcamp.context.ApplicationContext;
import bitcamp.util.Prompt;

import java.util.Random;
import java.util.Scanner;

public class PlaySinglePlayerGameCommand implements Command {
    ApplicationContext appCtx;

    public PlaySinglePlayerGameCommand(ApplicationContext appCtx) {
        this.appCtx = appCtx;
    }

    @Override
    public void execute(String title) {

        Random random = new Random();
        Scanner scanner = new Scanner(System.in);

        int currentNumber = 0;
        boolean isPlayerTurn = true;

        System.out.println("게임을 시작하려면 아무키나 누르세요 ! (0은 종료)");
        String abc = scanner.nextLine();
        if (abc.equalsIgnoreCase("0")) {
            return;
        }

        while (currentNumber < 31) {
            // 플레이어 턴
            isPlayerTurn = true;
            System.out.println("플레이어 턴 현재 숫자: " + currentNumber);
            int count = Prompt.inputInt("플레이어, 몇 개의 숫자를 말하시겠습니까? (1-3)>");
            while (count < 1 || count > 3) {
                count = Prompt.inputInt("잘못된 입력입니다. 다시 입력해주세요 (1-3)>");
            }
            for (int i = 0; i < count; i++) {
                currentNumber += 1;
                System.out.print(currentNumber + " ");
            }
            System.out.println();
            if (currentNumber >= 31) break;

            // 컴퓨터 턴
            isPlayerTurn = !isPlayerTurn;
            System.out.println("컴퓨터 턴 현재 숫자 : " + currentNumber);
            int randomNumber = random.nextInt(3) + 1;
            System.out.printf("컴퓨터가 %d개를 입력했습니다.\n", randomNumber);
            for (int i = 0; i < randomNumber; i++) {
                currentNumber += 1;
                System.out.print(currentNumber + " ");
            }
            System.out.println();
        }
        if (isPlayerTurn) {
            System.out.println("플레이어가 졌습니다 ... 컴퓨터가 이겼습니다.");
        } else {
            System.out.println("플레이어가 이겼습니다!!!!!");
        }
    }
}
