package ismt.application.engine;

import java.util.ArrayList;
import java.util.Collections;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;

public class CardDeck {

    public ArrayList<Card> cards_in_this_deck;

    public CardDeck() {
        cards_in_this_deck = new ArrayList<Card>();
    }

    public ObservableList<Node> get_images() {
        ObservableList<Node> cards = FXCollections.observableArrayList();
        cards.clear();

        for (Card myCard : cards_in_this_deck) {
            cards.add(myCard.generateCardImage());
        }

        return cards;
    }

    public void shuffle() {
		// The cards in an ArrayList-based dynamic array can be
        // ordered randomly by using the static method shuffle() of
        // the Collections class.

        Collections.shuffle(cards_in_this_deck);
    }

    public void add_card(Card given_card) {
        if (cards_in_this_deck.size() < 52) {
            cards_in_this_deck.add(given_card);
        }
    }

    public Card draw_card() {
		// Value null is returned if there are no available cards
        // in the deck.

		// If cards are left in the deck, the last card in the array
        // is returned, and the returned card is removed from the deck.
        Card card_to_return = null;

        if (cards_in_this_deck.size() > 0) {
			// ArrayList method remove() returns a reference to the
            // object that it removes from the array.

            card_to_return = cards_in_this_deck.remove(cards_in_this_deck.size() - 1);
        }

        return card_to_return;
    }
    
    public void empty(){
        
        this.cards_in_this_deck = new ArrayList<>();
    }
    
    public boolean isEmpty(){
        return this.cards_in_this_deck.isEmpty();
    }
    
    // devolve uma carta do mesmo naipe
    public Card draw_card_suit(Suit card_suit) {

        for(int i = 0 ; i < cards_in_this_deck.size() ; i++){
            
            // encontrou uma carta do baralho do mesmo naipe
            if(cards_in_this_deck.get(i).getSuit().equals(card_suit)){
                return cards_in_this_deck.remove(i);
            }
        }
        
        // nao tem cartas do mesmo naipe, logo joga uma qualquer
        return this.draw_card();
    }
    
    public JsonArrayBuilder getCardDeckJSON(JsonBuilderFactory jsonBuilderFactory){
        
        if(jsonBuilderFactory == null){
            jsonBuilderFactory = Json.createBuilderFactory(null);
        }
        
        JsonArrayBuilder jsonArrayBuilder = jsonBuilderFactory.createArrayBuilder();
        
        for (Card cards_in_this_deck1 : this.cards_in_this_deck) {
            jsonArrayBuilder.add( cards_in_this_deck1.getCardJSON(jsonBuilderFactory) );
        } 
         
        return jsonArrayBuilder;
    }
}
