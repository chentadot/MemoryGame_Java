import java.io.Serializable;

public class Card implements Matchable<Card>, Serializable {
    private final int cardID;

    public Card(int cardID) {
        this.cardID =cardID;
    }

    public int getCardID() {
        return cardID;
    }

    @Override
    public boolean matches(Card other) {
        return this.getCardID() == other.getCardID();
    }
}
