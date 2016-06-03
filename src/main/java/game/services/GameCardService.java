package game.services;

import db.models.game.cards.CardFactory;
import game.Card;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * created: 6/1/2016
 * package: game.services
 */
public class GameCardService {
    CardFactory factory=new CardFactory();
    Random random=new Random();
    @NotNull
    public Card[] makeHand(short handSize, boolean evil) {
        Card hand[]=new Card[handSize];
        for (int i=0; i<(int) handSize; i++) {
            random.nextInt();
            hand[i]=factory.createCard(random.nextInt(), evil);
        }
        return hand;
    }
}
