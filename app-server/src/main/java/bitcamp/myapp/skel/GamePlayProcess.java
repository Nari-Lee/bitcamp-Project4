package bitcamp.myapp.skel;

import bitcamp.myapp.ServerApp;
import bitcamp.myapp.listener.InitApplicationListener;

import java.util.Set;

public class GamePlayProcess {
	public static synchronized void createRoom(String roomName, int maxPlayers, int endNumber) {
		if (!ServerApp.gameRooms.containsKey(roomName)) {
			GameRoomProcess room = new GameRoomProcess(roomName, maxPlayers, endNumber, ServerApp.ServerApp);
			ServerApp.gameRooms.put(roomName, room);
			System.out.println("게임방 이름: " + roomName + " 최대 접속가능 인원수: " + maxPlayers + " 게임종료숫자: " + endNumber + "가 생성되었습니다.");
		}
	}

	public static synchronized void addClientToRoom(String roomName, InitApplicationListener initApplicationListener) {
		GameRoomProcess room = ServerApp.gameRooms.get(roomName);
		if (room != null && room.getNumberOfPlayers() < room.getMaxPlayers()) {
			room.addClient(initApplicationListener);
			System.out.println(initApplicationListener.getClientName() + " 사용자가 게임방: " + roomName + "에 접속 했습니다.");
		} else {
			initApplicationListener.sendMessage("해당 게임방이 꽉 찼습니다. 다른방을 선택해주세요");
		}
	}

	public static synchronized Set<String> getRoomList() {
		return ServerApp.gameRooms.keySet();
	}

	public static synchronized void removeClientFromRoom(String roomName, InitApplicationListener initApplicationListener) {
		GameRoomProcess room = ServerApp.gameRooms.get(roomName);
		if (room != null) {
			room.removeClient(initApplicationListener);
			System.out.println(initApplicationListener.getClientName() + " 님이 방을 떠났습니다: " + roomName);
			if (room.isEmpty()) {
				ServerApp.gameRooms.remove(roomName);
				System.out.println("게임방 : " + roomName + " 이 삭제 되었습니다.");
			}
		}
	}

	public static synchronized GameRoomProcess getGameRoom(String roomName) {
		return ServerApp.gameRooms.get(roomName);
	}

	public static synchronized void startGameInRoom(String roomName) {
		GameRoomProcess room = ServerApp.gameRooms.get(roomName);
		if (room != null) {
			room.startGame();
		}
	}
}
