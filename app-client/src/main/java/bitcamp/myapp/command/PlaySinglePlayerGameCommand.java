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
        Scanner scanner = new Scanner(System.in);
        System.out.println("모드를 선택하세요! ");
        System.out.println("1. 중복된 숫자 허용");
        System.out.println("2. 중복된 숫자 금지");
        int mode = Prompt.inputInt("모드 선택 (1 또는 2): ");
        System.out.println();

        if (mode == 1) {
            System.out.println("중복된 숫자는 입력할 수 없는 배스킨라빈스 게임입니다!\n직전 컴퓨터 턴에 컴퓨터가 입력한 개수는 입력할 수 없습니다.\n 예) 플레이어 : 2\n     컴퓨터   : 2\n");
            playAllowDuplicateMode();
        } else if (mode == 2) {
            playNoDuplicateMode();
        } else {
            System.out.println("잘못된 선택입니다. 게임을 종료합니다.");
        }
    }

    private void playAllowDuplicateMode() {
        Random random = new Random();
        Scanner scanner = new Scanner(System.in);

        int currentNumber = 0;
        boolean isPlayerTurn = true;

        System.out.println("게임을 시작하려면 아무키나 누르세요! (0은 종료)");
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

    private void playNoDuplicateMode() {
        Random random = new Random();
        Scanner scanner = new Scanner(System.in);

        int currentNumber = 0;
        int lastPlayerCount = 0;
        boolean isPlayerTurn = true;

        System.out.println("게임을 시작하려면 아무키나 누르세요! (0은 종료)");
        String abc = scanner.nextLine();
        if (abc.equalsIgnoreCase("0")) {
            return;
        }
        System.out.println("중복된 숫자는 입력할 수 없는 베스킨라빈스31입니다!");

        while (currentNumber < 31) {
            // 플레이어 턴
            isPlayerTurn = true;
            System.out.println("플레이어 턴 현재 숫자: " + currentNumber);
            int playerCount;
            while (true) {
                playerCount = Prompt.inputInt("플레이어, 몇 개의 숫자를 말하시겠습니까? (1-3)>");
                while (playerCount < 1 || playerCount > 3) {
                    playerCount = Prompt.inputInt("잘못된 입력입니다. 다시 입력해주세요 (1-3)>");
                }
                if (playerCount != lastPlayerCount) {
                    break;
                }
                System.out.println("중복된 숫자입니다. 다시 입력해주세요.");
            }
            for (int i = 0; i < playerCount; i++) {
                currentNumber += 1;
                System.out.print(currentNumber + " ");
            }
            System.out.println();
            lastPlayerCount = playerCount;
            if (currentNumber >= 31) break;

            // 컴퓨터 턴
            isPlayerTurn = !isPlayerTurn;
            System.out.println("컴퓨터 턴 현재 숫자 : " + currentNumber);
            int computerCount;
            while (true) {
                computerCount = random.nextInt(3) + 1;
                if (computerCount != lastPlayerCount) {
                    break;
                }
            }
            System.out.printf("컴퓨터가 %d개를 입력했습니다.\n", computerCount);
            for (int i = 0; i < computerCount; i++) {
                currentNumber += 1;
                System.out.print(currentNumber + " ");
            }
            System.out.println();
            lastPlayerCount = computerCount;
        }
        if (isPlayerTurn) {
            System.out.println("플레이어가 졌습니다... 컴퓨터가 이겼습니다.");
        } else {
            System.out.println("플레이어가 이겼습니다!!!!!");
        }
    }
}
