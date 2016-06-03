package game.services;

import db.models.game.cards.CardFactory;
import db.models.game.cards.CardModel;
import db.models.game.cards.CardType;
import db.services.CardService;
import game.Card;
import game.ICardEffect;
import game.PlayingUser;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * created: 6/1/2016
 * package: game.services
 */
public class GameCardService {
    CardFactory factory=new CardFactory();
    final Map<CardType, CardModel> typesToCards = new ConcurrentHashMap<>();
    final Map<String, CardModel> namesToCards = new ConcurrentHashMap<>();
    final CardService cardService;
    final AtomicLong cardIdIncrement = new AtomicLong(0L);

    public GameCardService(@NotNull CardService service) {
        this.cardService = service;
    }


    public void configure() {
        // загрузим доступные карты и проассоциируем их эффекты


    }

    @NotNull
    public Card[] makeHand(short handSize, @NotNull PlayingUser owner) {
        final Card[] cards = new Card[handSize];
        for (int i = 0; i<handSize; ++i) {
            cards[i] = makeRandomCard(owner);
        }
        return cards;
    }


    protected Card makeCard(@NotNull CardModel model, @NotNull PlayingUser owner) {
        // TODO: make card
    }

    public Card makeRandomCard(@NotNull PlayingUser owner) {
        final CardModel[] cards = (CardModel[]) typesToCards.values().toArray();
        final int randomId = (int) Math.round(Math.random()*cards.length);
        return this.makeCard(cards[randomId], owner);
    }

    public Card makeCard(@NotNull CardType type, @NotNull PlayingUser owner) {
        final CardModel model =  typesToCards.get(type);
        return this.makeCard(model, owner);
    }

    public Card makeCard(@NotNull String name, @NotNull PlayingUser owner) {
        final CardModel model = namesToCards.get(name);
        return this.makeCard(model, owner);
    }

}
