package starterpack.strategy;

import starterpack.game.GameState;
import starterpack.game.CharacterClass;
import starterpack.game.Item;
import starterpack.game.Position;
import starterpack.util.Logger;

public class RushMidStrategy implements Strategy {

    /**
     * When the game initializes, you need to decide your starting class!
     * Also, feel free to initialize some variables you need here!
     * @return The CharacterClass you decided when the game starts.
     */
    public CharacterClass strategyInitialize(int myPlayerIndex) {
        return CharacterClass.KNIGHT;
    }

    /**
     *
     * @param gameState
     * @param myPlayerIndex
     * @return
     */
    public Position moveActionDecision(GameState gameState, int myPlayerIndex) {
        Position playerPos = gameState.getPlayerStateByIndex(myPlayerIndex).getPosition();
        int playerX = playerPos.getX();
        int playerY = playerPos.getY();

        if (myPlayerIndex == 0) {
            if (!((playerX == 5 || playerX == 6) && (playerY == 5 || playerY == 6))) {
                return new Position(playerPos.getX()+1, playerPos.getY()+1);
            }
        }
        return gameState.getPlayerStateByIndex(myPlayerIndex).getPosition();
    }

    /**
     *
     * @param gameState
     * @param myPlayerIndex
     * @return
     */
    public int attackActionDecision(GameState gameState, int myPlayerIndex) {
        return 0;
    }

    /**
     *
     * @param gameState
     * @param myPlayerIndex
     * @return
     */
    public Item buyActionDecision(GameState gameState, int myPlayerIndex) {
        return Item.NONE;
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
