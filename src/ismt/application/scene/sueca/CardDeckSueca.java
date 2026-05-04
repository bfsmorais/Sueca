package ismt.application.scene.sueca;

import ismt.application.engine.*;

public class CardDeckSueca extends CardDeck{

    // na sueca, o baralho nao pode ter 8, 9 e 10
    public CardDeckSueca() {
        
        super();    // inicializa o ArrayList de cartas
        
        for (int suit_index = 0; suit_index < 4; suit_index++) {
            
            add_card(new Card(1, 11, Suit.values()[suit_index]));   // Ás
            add_card(new Card(7, 10, Suit.values()[suit_index]));   // 7 (bisca)
            
            add_card(new Card(11, 2, Suit.values()[suit_index]));   //Dama
            add_card(new Card(12, 3, Suit.values()[suit_index]));   // Valete
            add_card(new Card(13, 4, Suit.values()[suit_index]));   // Rei
            
            for (int card_rank = 2; card_rank < 7; card_rank++) {

                add_card(new Card(card_rank, 0, Suit.values()[suit_index]));
            }
        }
    }
}
