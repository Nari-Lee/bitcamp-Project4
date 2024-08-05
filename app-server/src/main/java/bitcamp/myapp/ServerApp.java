package bitcamp.myapp;

import bitcamp.myapp.listener.InitApplicationListener;
import bitcamp.myapp.skel.GameRoomProcess;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerApp {
    private static final int PORT = 8888;
    public static Map<String, GameRoomProcess> gameRooms = new HashMap<>();
    public static bitcamp.myapp.ServerApp ServerApp;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("게임서버가 포트번호:" + PORT + "에서 시작되었습니다.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("새로운 클라이언트가 접속하였습니다.: " + clientSocket.getInetAddress());

                // 클라이언트 핸들러 생성 및 시작
                InitApplicationListener initApplicationListener = new InitApplicationListener(clientSocket);
                new Thread(initApplicationListener).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
