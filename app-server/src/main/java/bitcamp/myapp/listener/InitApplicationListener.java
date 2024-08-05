package bitcamp.myapp.listener;

import bitcamp.myapp.skel.GamePlayProcess;
import bitcamp.myapp.skel.GameRoomProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

public class InitApplicationListener implements Runnable {
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private String roomName;
	private String clientName;

	public InitApplicationListener(Socket socket) {
		this.socket = socket;
		try {
			this.out = new PrintWriter(socket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			// 클라이언트 이름 설정
			out.println("Enter your name:");
			clientName = in.readLine();

			while (true) {
				out.println("Enter a command (list, join <room>, create <room> <maxPlayers> <endNumber>, play <1-3>, exit):");
				String command = in.readLine();
				if (command == null) {
					break;
				}

				String[] tokens = command.split(" ", 4);
				String action = tokens[0].toLowerCase();

				switch (action) {
					case "list":
						listRooms();
						break;
					case "join":
						if (tokens.length > 1) {
							joinRoom(tokens[1]);
						} else {
							out.println("Room name required to join.");
						}
						break;
					case "create":
						if (tokens.length > 3) {
							try {
								int maxPlayers = Integer.parseInt(tokens[2]);
								int endNumber = Integer.parseInt(tokens[3]);
								createRoom(tokens[1], maxPlayers, endNumber);
							} catch (NumberFormatException e) {
								out.println("Invalid number. Enter a valid number for max players and end number.");
							}
						} else {
							out.println("Room name, max players, and end number required to create.");
						}
						break;
					case "play":
						if (tokens.length > 1) {
							try {
								int count = Integer.parseInt(tokens[1]);
								playGame(count);
							} catch (NumberFormatException e) {
								out.println("Invalid number. Enter a number between 1 and 3.");
							}
						} else {
							out.println("You need to specify how many numbers to call (1-3).");
						}
						break;
					case "exit":
						out.println("Goodbye!");
						if (roomName != null) {
							GamePlayProcess.removeClientFromRoom(roomName, this);
						}
						socket.close();
						return;
					default:
						out.println("Invalid command.");
						break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void listRooms() {
		Set<String> rooms = GamePlayProcess.getRoomList();
		if (rooms.isEmpty()) {
			out.println("No rooms available.");
		} else {
			out.println("Available rooms: " + rooms);
		}
	}

	private void joinRoom(String roomName) {
		if (this.roomName != null) {
			GamePlayProcess.removeClientFromRoom(this.roomName, this);
		}
		this.roomName = roomName;
		GamePlayProcess.addClientToRoom(roomName, this);
		out.println("Joined room: " + roomName);
		GamePlayProcess.startGameInRoom(roomName);
	}

	private void createRoom(String roomName, int maxPlayers, int endNumber) {
		if (maxPlayers < 2 || maxPlayers > 5) {
			out.println("Max players must be between 2 and 5.");
			return;
		}

		if (endNumber < 10 || endNumber > 100) {
			out.println("End number must be between 10 and 100.");
			return;
		}

		if (GamePlayProcess.getRoomList().contains(roomName)) {
			out.println("Room already exists.");
		} else {
			GamePlayProcess.createRoom(roomName, maxPlayers, endNumber);
			joinRoom(roomName);
			out.println("Room created and joined: " + roomName + " with max players: " + maxPlayers + " and end number: " + endNumber);
		}
	}

	private void playGame(int count) {
		if (roomName == null) {
			out.println("You must join a room to play.");
			return;
		}

		GameRoomProcess room = GamePlayProcess.getGameRoom(roomName);
		if (room != null) {
			room.playTurn(this, count);
		}
	}

	public void sendMessage(String message) {
		out.println(message);
	}

	public String getClientName() {
		return clientName;
	}
}
