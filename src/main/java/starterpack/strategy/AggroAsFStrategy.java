package starterpack.strategy;

import starterpack.game.*;
import starterpack.util.Utility;

public class AggroAsFStrategy implements Strategy {

    /**
     * When the game initializes, you need to decide your starting class!
     * Also, feel free to initialize some variables you need here!
     * @return The CharacterClass you decided when the game starts.
     */
    public CharacterClass strategyInitialize(int myPlayerIndex) {
        return CharacterClass.WIZARD;
    }

    /**
     *
     * @param gameState
     * @param myPlayerIndex
     * @return
     */
    public Position moveActionDecision(GameState gameState, int myPlayerIndex) {

        PlayerState myPlayerState = gameState.getPlayerStateByIndex(myPlayerIndex);

        Position playerPos = myPlayerState.getPosition();
        int playerX = playerPos.getX();
        int playerY = playerPos.getY();

        // tp home if low
        if (myPlayerState.getHealth() <=4) {
            return Utility.spawnPoints.get(myPlayerIndex);
        }



        /* int res = 0;
        for (int i = 0; i < 4; i++) {

            if (i != myPlayerIndex) {
                int distToTarget = Utility.chebyshevDistance(myPlayerState.getPosition(), gameState.getPlayerStateByIndex(i).getPosition());
                if (distToTarget >= myPlayerState.getCharacterClass().getStatSet().getRange()) {
                    return i;
                }
                res = i;
            }
        }
        return res;
        */
        return playerPos;
    }

    /**
     *
     * @param gameState
     * @param myPlayerIndex
     * @return
     */
    public int attackActionDecision(GameState gameState, int myPlayerIndex) {
        int res = 0;
        for (int i = 0; i < 4; i++) {

            if (i != myPlayerIndex) {
                if (Utility.chebyshevDistance(
                        gameState.getPlayerStateByIndex(myPlayerIndex).getPosition(),
                        gameState.getPlayerStateByIndex(i).getPosition()) <=
                        gameState.getPlayerStateByIndex(myPlayerIndex).getCharacterClass().getStatSet().getRange()) {
                    return i;
                }
                res = i;
            }
        }
        return res;
    }

    /**
     *
     * @param gameState
     * @param myPlayerIndex
     * @return
     */
    public Item buyActionDecision(GameState gameState, int myPlayerIndex) {
        return Item.PROCRUSTEAN_IRON;
    }

    /**
     *
     * @param gameState
     * @param myPlayerIndex
     * @return
     */
    public boolean useActionDecision(GameState gameState, int myPlayerIndex) {
        return false;
    }
}
