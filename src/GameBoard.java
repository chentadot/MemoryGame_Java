import java.io.Serializable;
import java.util.*;

/* GameBoard is a generic class that aims to hold the game board data structure.
    each element is a sub-class of Card.
    responsibilities:
        Instantiate a board with the desired size
        Using a provided set of Cards:
            Populate the board with the cards
            Can be either shuffled or ordered according to the Set order
            Use each card the specified number of times (e.g., 1 is unique, 2 is for standard memory match, etc.)
        Hold the location of each card in the board
        Allow iterating through the board to get the location and Card object.


 */
public class GameBoard<T extends Card> implements Iterable<GameBoard<T>.BoardElement>, Serializable {
    private final int numOfRows;
    private final int numOfColumns;
    private final T[][] cardArray;

    @SuppressWarnings("unchecked")
    public GameBoard(int numOfRows, int numOfColumns, Set<T> setOfCards, boolean shuffled, int frequency) {
        this.numOfRows = numOfRows;
        this.numOfColumns = numOfColumns;
        List<T> cardList = generateListOfCards(setOfCards, frequency);
        if (shuffled) Collections.shuffle(cardList);
        cardArray = generateCardArray(numOfRows, numOfColumns, cardList);
    }

    private List<T> generateListOfCards(Set<T> setOfCards, int frequency) {
        List<T> cardList = new ArrayList<T>();
        int count = 0;
        for (T card : setOfCards) {
            if (count >= numOfRows*numOfColumns) break;
            for (int i=0; i<frequency; i++) {
                cardList.add(card);
                count++;
            }
        }
        return cardList;
    }

    private T[][] generateCardArray(int numOfRows, int numOfColumns,List<T> cardList) {
        T[][] temp = (T[][]) new Card[numOfRows][numOfColumns];
        int i=0;
        for (int row=0; row<numOfRows; row++) {
            for (int col=0; col<numOfColumns; col++) {
                temp[row][col] = cardList.get(i);
                i++;
                if (i>=cardList.size()) break;
            }
        }
        return temp;
    }

    public class BoardElement {
        private final int row;
        private final int column;
        private final T card;

        public BoardElement(int row, int column, T card) {
            this.row = row;
            this.column = column;
            this.card = card;
        }

        public int getRow() {return row;}
        public int getColumn() {return column;}
        public T getCard() {return card;}
    }

    private class BoardIterator implements Iterator<BoardElement> {
        private int currentRow = 0;
        private int currentColumn = 0;

        @Override
        public boolean hasNext() {
            return currentRow<numOfRows && currentColumn<numOfColumns;
        }

        @Override
        public BoardElement next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements in the board");
            }

            T card = cardArray[currentRow][currentColumn];
            BoardElement element = new BoardElement(currentRow,currentColumn,card);

            currentColumn++;
            if (currentColumn >= numOfColumns) {
                currentColumn = 0;
                currentRow++;
            }

            return element;
        }
    }

    // Implement Iterable to enable "foreach" looping
    @Override
    public Iterator<BoardElement> iterator() {
        return new BoardIterator();
    }

    // additional board functionality
    public T getCardAtLocation(int row, int column) {
        return cardArray[row][column];
    }
    public void setCardAtLocation(int row, int column, T card) {
        cardArray[row][column] = card;
    }

    public int getNumOfRows() {return numOfRows;}
    public int getNumOfColumns() {return numOfColumns;}
}
