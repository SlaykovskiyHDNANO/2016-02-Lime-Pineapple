package game;

/**
 * Created by igor on 03.06.16.
 */
public class UserHand {
    Card[] cards;
    BossCard boss;
    public UserHand(Card[] hand, BossCard bosscard) {
        cards=hand;
        boss=bosscard;
    }
}
