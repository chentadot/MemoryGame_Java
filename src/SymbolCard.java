import javafx.scene.image.Image;

public class SymbolCard extends Card {
    private final String symbolUrl;

    public SymbolCard(int cardID, String symbolUrl) {
        super(cardID);
        this.symbolUrl = symbolUrl;
    }

    public String getSymbolUrl() {return this.symbolUrl;}
    public Image getSymbolImage() {return new Image(symbolUrl);}
}
