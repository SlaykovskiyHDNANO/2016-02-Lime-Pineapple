package game.services;

import db.models.game.cards.CardFactory;
import db.models.game.cards.CardModel;
import db.models.game.cards.CardType;
import game.Card;
import game.PlayingUser;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * created: 6/1/2016
 * package: game.services
 */
public class GameCardService {
    final Map<CardType, CardModel> typesToCards = new ConcurrentHashMap<>();
    final Map<String, CardModel> namesToCards = new ConcurrentHashMap<>();
    final CardFactory cardFactory;
    final AtomicInteger cardIdIncrement = new AtomicInteger(0);

    public GameCardService(@NotNull CardFactory service) {
        this.cardFactory = service;
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
        final int id = cardIdIncrement.incrementAndGet();
        return new Card(model, id, owner);
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

    public CardModel[] getModels() {
        return this.cardFactory.getModels();
    }


}
