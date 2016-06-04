package game.services;

import db.models.game.cards.CardType;
import game.Card;
import game.GameField;
import game.GameFieldRow;
import game.PlayingUser;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * created: 6/1/2016
 * package: game
 */
public class GameFieldFactoryService {
    private final GameCardService gameCardService;

    synchronized GameFieldRow[] buildRows() {
        final GameFieldRow[] rows = new GameFieldRow[2];
        // implementation of buildRows
        rows[0] = new GameFieldRow(GameField.FieldType.RANGED);
        rows[1] = new GameFieldRow(GameField.FieldType.MELEE);
        return rows;
    }

    public GameFieldFactoryService(@NotNull GameCardService service) {
        this.gameCardService = service;
    }

    public GameField makeField(PlayingUser[] users) {
        final Map<PlayingUser, GameFieldRow[]> userToRows = new ConcurrentHashMap<>();
        final Map<PlayingUser, Card> bosses = new ConcurrentHashMap<>();
        for (PlayingUser user : users) {
            userToRows.put(user, buildRows());
            bosses.put(user, this.gameCardService.makeCard(CardType.BOSS_CARD, user));
        }
        return new GameField(userToRows, bosses);
    }
}

