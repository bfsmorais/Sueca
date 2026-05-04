package ismt.application.engine;

import javafx.beans.property.SimpleIntegerProperty;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;

public class Player {

    private String name;
    private String password;
    private Stats stats;
    private int points;
    private int money;
    private CardDeck deck;

    public Player() {
        points = 0;
        deck = new CardDeck();
    }

    public Player(String given_name, int given_points, CardDeck given_deck) {
        // TODO Criar jogador com par�metros e baralho aleat�rio
        name = given_name;
        points = given_points;
        deck = given_deck;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public CardDeck getDeck() {
        return deck;
    }

    public void setDeck(CardDeck deck) {
        this.deck = deck;
    }

    public void addToDeck(Card card) {
        this.deck.add_card(card);
    }

    private SimpleIntegerProperty value = new SimpleIntegerProperty(0);

    public SimpleIntegerProperty valueProperty() {
        return value;
    }
    
    public JsonObjectBuilder getCardJSON(JsonBuilderFactory jsonBuilderFactory){
        
        if(jsonBuilderFactory == null){
            jsonBuilderFactory = Json.createBuilderFactory(null);
        }
        
        JsonObjectBuilder jsonObject = jsonBuilderFactory.createObjectBuilder();
        jsonObject.add("name", this.name);
        jsonObject.add("cardDeck", this.deck.getCardDeckJSON(jsonBuilderFactory) );
        
        return jsonObject;
    } 
}
