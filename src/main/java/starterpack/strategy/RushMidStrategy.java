package starterpack.strategy;

import starterpack.game.*;
import starterpack.util.Logger;
import starterpack.util.Utility;

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

        PlayerState myPlayerState = gameState.getPlayerStateByIndex(myPlayerIndex);

        Position playerPos = myPlayerState.getPosition();
        int playerX = playerPos.getX();
        int playerY = playerPos.getY();
        boolean notInMid = !((playerX == 4 || playerX == 5) && (playerY == 4 || playerY == 5));

        // tp home if low
        if (myPlayerState.getHealth() <=3) {
            return Utility.spawnPoints.get(myPlayerIndex);
        }

        // stay put if can buy prost whatever
        if (myPlayerState.getGold() >= 8 && !(myPlayerState.getItem() == Item.PROCRUSTEAN_IRON)) {
            return Utility.spawnPoints.get(myPlayerIndex);
        }

        // move towards mid if not already in mid
        if (myPlayerIndex == 0) {
            if (notInMid) {
                return new Position(playerX + 1, playerY + 1);
            }
        } else if (myPlayerIndex == 1) {
            if (notInMid) {
                return new Position(playerX + -1, playerY + 1);
            }
        } else if (myPlayerIndex == 2) {
            if (notInMid) {
                return new Position(playerX - 1, playerY - 1);
            }
        } else if (myPlayerIndex == 3) {
            if (notInMid) {
                return new Position(playerX + 1, playerY - 1);
            }
        }
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
