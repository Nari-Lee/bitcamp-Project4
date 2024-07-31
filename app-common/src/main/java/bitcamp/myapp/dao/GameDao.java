package bitcamp.myapp.dao;

import java.io.Serializable;

public interface GameDao extends Serializable {

    String playerTurn(int currentNumber, int count);
    String computerTurn(int currentNumber);
    int getCurrentNumber();
  }
