import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import javafx.beans.binding.Bindings;
import javafx.scene.shape.Rectangle;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MemoryGameController {
    @FXML private GridPane gameBoardGrid;
    @FXML private Circle player1Token;
    @FXML private Label player1ScoreLabel;
    @FXML private Circle player2Token;
    @FXML private Label player2ScoreLabel;
    @FXML private ComboBox<String> boardSizeBox;
    @FXML private Label player1Name;
    @FXML private Label player2Name;
    @FXML private Rectangle cheatBox;


    private boolean loadedGame = false;
    private int numOfRows = 5;
    private int numOfColumns = 6;
    private double cellWidth;
    private double cellHeight;
    private GameBoard<SymbolCard> gameBoard;
    private Set<SymbolCard> cardSet;
    private GameState<SymbolCard> gameState;
    private final Color noTurn = Color.GRAY;
    private final Color yesTurn = Color.BLUE;
    private final Path symbolsFolder = Paths.get(".\\src\\symbols\\");
    private final String backsideImagePath = Paths.get(".\\src\\backPattern\\pattern.jpg").toUri().toString();
    private final String saveFile = "MG_save.txt";
    private final Image backsideImage = new Image(backsideImagePath);
    private final ImagePattern backsidePattern = new ImagePattern(backsideImage);
    private Rectangle[][] cardsArray;
    private int cardsPicked = 0;
    private int card1row;
    private int card1col;
    private int card2row;
    private int card2col;
    private boolean lastTurnResult = false;
    private boolean firstTurn = true;

    private String player1 = "נועם";
    private String player2 = "אבא";


    @FXML
    void loagGamePressed(ActionEvent event) {
        try {
            FileInputStream fi = new FileInputStream(saveFile);
            ObjectInputStream oi = new ObjectInputStream((fi));
            gameState.loadGameState((GameState)oi.readObject());
            oi.close();
            fi.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        gameBoard = gameState.getGameBoard();
        loadedGame = true;
        resetGame();
    }

    @FXML
    void newGamePressed(ActionEvent event) {
        loadedGame = false;
        resetGame();
    }

    @FXML
    void saveGamePressed(ActionEvent event) {
        try {
            FileOutputStream fo = new FileOutputStream(saveFile);
            ObjectOutputStream oo = new ObjectOutputStream(fo);
            oo.writeObject(gameState);
            oo.close();
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML void cheatBoxPressed(MouseEvent event) {
        for (int row=0; row<numOfRows; row++) {
            for (int col=0; col<numOfColumns; col++) {
                ImagePattern cardSymbol = new ImagePattern(gameBoard.getCardAtLocation(row,col).getSymbolImage());
                cardsArray[row][col].setFill(cardSymbol);
            }
        }
    }
    @FXML
    void cheatBoxReleased(MouseEvent event) {
        for (int row=0; row<numOfRows; row++) {
            for (int col=0; col<numOfColumns; col++) {
                cardsArray[row][col].setFill(backsidePattern);
            }
        }
    }


    @FXML
    void initialize() {
        populateBoardSizes();
        gameState = new GameState<SymbolCard>();
        player1ScoreLabel.textProperty().bind(Bindings.createStringBinding(() -> gameState.player1ScoreProperty().get()+"",gameState.player1ScoreProperty()));
        player2ScoreLabel.textProperty().bind(Bindings.createStringBinding(() -> gameState.player2ScoreProperty().get()+"",gameState.player2ScoreProperty()));
        player1Token.setFill(noTurn);
        player2Token.setFill(noTurn);
        getSymbolSet();

        player1Name.setText(player1);
        player2Name.setText(player2);
    }

    private void populateBoardSizes() {
        boardSizeBox.getItems().add("Large");
        boardSizeBox.getItems().add("Medium");
        boardSizeBox.getItems().add("Small");

        boardSizeBox.setValue("Medium");
    }

    private void getBoardSize() {
        if (!loadedGame) {
            switch (boardSizeBox.getValue()) {
                case "Large":
                    numOfRows = 6;
                    numOfColumns = 7;
                    break;
                case "Medium":
                    numOfRows = 5;
                    numOfColumns = 6;
                    break;
                case "Small":
                    numOfRows = 4;
                    numOfColumns = 4;
                    break;
            }
            cellWidth = gameBoardGrid.getWidth() / numOfColumns;
            cellHeight = gameBoardGrid.getHeight() / numOfRows;
        } else {
            numOfRows = gameBoard.getNumOfRows();
            numOfColumns = gameBoard.getNumOfColumns();
            cellWidth = gameBoardGrid.getWidth()/numOfColumns;
            cellHeight = gameBoardGrid.getHeight()/numOfRows;
        }
    }

    private void generateBoard() {
        getBoardSize();
        if (!loadedGame) {
            gameBoard = new GameBoard<SymbolCard>(numOfRows, numOfColumns, cardSet, true, 2);
            gameState.setGameBoard(gameBoard);
        }

        cardsArray = new Rectangle[numOfRows][numOfColumns];

        gameBoardGrid.getColumnConstraints().clear();
        gameBoardGrid.getRowConstraints().clear();
        gameBoardGrid.getChildren().clear();
        for (int col = 0; col < numOfColumns; col++) {
            ColumnConstraints colSize = new ColumnConstraints();
            colSize.setPercentWidth(100.0 / numOfColumns);
            colSize.setHgrow(Priority.ALWAYS);
            gameBoardGrid.getColumnConstraints().add(colSize);
        }
        for (int row = 0; row < numOfRows; row++) {
            RowConstraints rowSize = new RowConstraints();
            rowSize.setPercentHeight(100.0 / numOfRows);
            rowSize.setVgrow(Priority.ALWAYS);
            gameBoardGrid.getRowConstraints().add(rowSize);
        }

        for (int row = 0; row < numOfRows; row++) {
            for (int col = 0; col < numOfColumns; col++) {
                cardsArray[row][col] = new Rectangle(cellWidth*0.9, cellHeight*0.9); // board grid is made of empty rectangles
                cardsArray[row][col].setStroke(Color.BLACK);
                cardsArray[row][col].setFill(backsidePattern);

                int finalRow = row;
                int finalCol = col;
                cardsArray[row][col].setOnMousePressed(event -> handleCardPressed(finalRow,finalCol));
                gameBoardGrid.add(cardsArray[row][col], col, row);
                GridPane.setHalignment(cardsArray[row][col], javafx.geometry.HPos.CENTER);
                if (loadedGame && gameState.getCardStatusAtLocation(row,col)) cardsArray[row][col].setVisible(false);
            }
        }

    }

    private void resetGame() {
        cardsPicked=0;
        generateBoard();
        if (!loadedGame) gameState.resetPlayerScore();
        switchTurn();
        if (loadedGame) switchTurn();       // do it again for loaded game to preserve the turn state.
        firstTurn=true;
    }

    private void switchTurn() {
        boolean turn = gameState.getPlayerTurn();

        if (!turn) {
            player1Token.setFill(yesTurn);
            player2Token.setFill(noTurn);
        } else {
            player1Token.setFill(noTurn);
            player2Token.setFill(yesTurn);
        }

        gameState.setPlayerTurn(!turn);
    }

    private void getSymbolSet() {
        cardSet = new HashSet<SymbolCard>();
        if (Files.exists(symbolsFolder) && Files.isDirectory(symbolsFolder)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(symbolsFolder);) {
                int i=0;
                for (Path p : directoryStream) {
                    String symbolImageFile = p.toUri().toString();
                    if (symbolImageFile.toLowerCase().endsWith(".png")) {
                        SymbolCard newCard = new SymbolCard(i,symbolImageFile);
                        cardSet.add(newCard);
                        i++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void handleCardPressed(int row, int col) {
        System.out.println("Row:" + row + " Col:" + col);
        if (firstTurn) {
            firstTurn = false;
        } else if (cardsPicked == 0) {
            if (lastTurnResult) {
                removeMatchedCards();
            } else {
                flipUnmatchedCards();
            }
        }
        cardsPicked++;
        ImagePattern cardSymbol = new ImagePattern(gameBoard.getCardAtLocation(row,col).getSymbolImage());
        cardsArray[row][col].setFill(cardSymbol);
        if (cardsPicked == 1) {
            card1row=row;
            card1col=col;
        } else if (cardsPicked == 2) {
            card2row = row;
            card2col = col;
            if (card1row==card2row && card1col==card2col) {
                cardsPicked--;
            } else {
                handlePickedCards();
            }
        }
    }

    private void handlePickedCards() {
        cardsPicked=0;
        if (checkIfCardsMatch()) {
            lastTurnResult=true;
            gameState.incrementCurrentPlayerScore();
        } else {
            lastTurnResult=false;
            switchTurn();
        }
    }

    private boolean checkIfCardsMatch() {
        SymbolCard card1 = gameBoard.getCardAtLocation(card1row,card1col);
        SymbolCard card2 = gameBoard.getCardAtLocation(card2row,card2col);
        return card1.matches(card2);
    }

    private void removeMatchedCards() {
        cardsArray[card1row][card1col].setVisible(false);
        cardsArray[card2row][card2col].setVisible(false);
        gameState.markCardAsPicked(card1row,card1col);
        gameState.markCardAsPicked(card2row,card2col);
    }

    private void flipUnmatchedCards() {
        cardsArray[card1row][card1col].setFill(backsidePattern);
        cardsArray[card2row][card2col].setFill(backsidePattern);
    }
}
