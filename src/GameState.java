import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class GameState<T extends Card> implements Serializable {
    private GameBoard<T> gameBoard;
    private boolean[][] cardStatus;
    private boolean playerTurn = false;     // false -> player1, true -> player2
    private transient IntegerProperty player1Score = new SimpleIntegerProperty(0);
    private transient IntegerProperty player2Score = new SimpleIntegerProperty(0);

    public GameState() {
    }

    public GameState(GameBoard<T> gameBoard, boolean player) {
        setGameBoard(gameBoard);
        setPlayerTurn(player);
    }

    public GameState(GameBoard<T> gameBoard) {
        this(gameBoard,false);
    }

    public void setGameBoard(GameBoard<T> gameBoard) {
        this.gameBoard = gameBoard;
        cardStatus = new boolean[gameBoard.getNumOfRows()][gameBoard.getNumOfColumns()];        // default value: false
    }
    public void setPlayerTurn(boolean player){this.playerTurn = player;}
    public void setPlayer1Score(int player1Score) {this.player1Score.set(player1Score);}
    public void setPlayer2Score(int player2Score) {this.player2Score.set(player2Score);}
    public void setCardAsPicked(int row, int column) {cardStatus[row][column] = true;}

    public void incrementCurrentPlayerScore() {
        if (playerTurn) {
            player1Score.set(player1Score.get()+1);
        } else {
            player2Score.set(player2Score.get()+1);
        }
        System.out.println("Player1: " + player1Score.get());
        System.out.println("Player2: " + player2Score.get());
    }

    public void resetPlayerScore() {
        player1Score.set(0);
        player2Score.set(0);
    }

    public GameBoard<T> getGameBoard() {return gameBoard;}
    public boolean[][] getCardStatus() {return cardStatus;}
    public boolean getCardStatusAtLocation(int row, int column) {return cardStatus[row][column];}
    public boolean getPlayerTurn() {return playerTurn;}
    public int getPlayer1Score() {return player1Score.getValue();}
    public int getPlayer2Score() {return player2Score.getValue();}
    public IntegerProperty player1ScoreProperty() {return player1Score;}
    public IntegerProperty player2ScoreProperty() {return player2Score;}
    public void markCardAsPicked(int row, int column){
        cardStatus[row][column] = true;
    }

    public void loadGameState(GameState<T> gameState) {
        this.setGameBoard(gameState.gameBoard);
        this.setPlayer1Score(gameState.getPlayer1Score());
        this.setPlayer2Score(gameState.getPlayer2Score());
        this.setPlayerTurn(gameState.getPlayerTurn());
        cardStatus = gameState.cardStatus;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject(); // Now safe, `IntegerProperty` fields are `transient`
        out.writeInt(player1Score.get()); // Serialize the int value manually
        out.writeInt(player2Score.get());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // Now safe, `IntegerProperty` fields are `transient`
        player1Score = new SimpleIntegerProperty(in.readInt()); // Reinitialize from int value
        player2Score = new SimpleIntegerProperty(in.readInt());
    }
}
