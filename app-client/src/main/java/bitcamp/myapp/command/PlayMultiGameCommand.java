package bitcamp.myapp.command;

import bitcamp.context.ApplicationContext;
import bitcamp.myapp.ClientApp;
import bitcamp.util.Prompt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class PlayMultiGameCommand {
	ApplicationContext appCtx;

	public PlayMultiGameCommand(ApplicationContext appCtx) {
		this.appCtx = appCtx;
	}

	public static void playMultiplayer() {
		ClientApp.SERVER_ADDRESS = Prompt.input("접속할 게임서버의 IP를 입력 해주세요: ");
		ClientApp.SERVER_PORT = Prompt.inputInt("접속할 게임서버의 Port 번호를 입력 해주세요: ");

		try (Socket socket = new Socket(ClientApp.SERVER_ADDRESS, ClientApp.SERVER_PORT);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

			System.out.println("멀티게임을 위해 게임서버에 접속합니다.");

			Thread serverThread = new Thread(() -> {
				try {
					String serverMessage;
					while ((serverMessage = in.readLine()) != null) {
						System.out.println("Server Message: " + serverMessage);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			serverThread.start();

			boolean running = true;
			while (running) {
				System.out.println("1. 게임방 만들기");
				System.out.println("2. 게임방 찾아보기");
				System.out.println("3. Exit");

				String option = Prompt.input("");
				switch (option) {
					case "1":
						createRoom(out);
						break;
					case "2":
						searchRooms(out);
						break;
					case "3":
						out.println("exit");
						running = false;
						break;
					default:
						System.out.println("유효하지 않은 메뉴입니다. 입력하신 숫자를 확인 후 다시 입력해주세요");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createRoom(PrintWriter out) throws IOException {
		String roomName = Prompt.input("게임방 이름을 입력해 주세요: ");

		int maxPlayers = Prompt.inputInt("최대 접속가능 인원수를 지정해주세요 (2명~5명): ");
		if (maxPlayers < 2 || maxPlayers > 5) {
			System.out.println("유효하지 않은 인원수 입니다. 2명~5명 사이의 인원수를 지정해주세요.");
			return;
		}

		String endNumberInput = Prompt.input("종료할 숫자를 입력해주세요 ('random' 을 입력시 게임 종료숫자가 무작위으로 지정됩니다.): ");
		int endNumber = 31; // default end number

		if (endNumberInput.equalsIgnoreCase("random")) {
			Random random = new Random();
			endNumber = random.nextInt(21) + 30; // Random end number between 31 and 50
		} else if (endNumberInput.equalsIgnoreCase("")) {
			endNumber = 31;
			System.out.println("기본값인 '31' 로 게임 종료 숫자가 지정되었습니다.");
		} else if (!endNumberInput.trim().isEmpty()) {
			endNumber = Integer.parseInt(endNumberInput);
			if (endNumber < 10 || endNumber > 100) {
				System.out.println("종료할 숫자는 반드시 10~100 사이의 숫자값 이어야 됩니다.");
				return;
			}
		}

		out.println("create " + roomName + " " + maxPlayers + " " + endNumber);
	}

	private static void searchRooms(PrintWriter out) throws IOException {
		out.println("- 게임방 목록 -");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String response = in.readLine();
		System.out.println("접속 가능한 게임방 이름:");
		System.out.println(response);

		if (response.contains("접속 가능한 게임방이 없습니다.")) {
			String createRoomResponse = Prompt.input("게임방을 새로 만드시겠습니까? (y/n): ").trim().toLowerCase();
			if (createRoomResponse.equals("y")) {
				createRoom(out);
			}
		} else {
			String roomName = Prompt.input("접속할 게임방 번호를 입력 해주세요: ");
			out.println("게임방 이름: " + roomName + "에 접속 하였습니다.");
		}
	}
}
