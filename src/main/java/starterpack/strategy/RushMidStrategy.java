package starterpack.strategy;

import starterpack.game.*;
import starterpack.util.Logger;
import starterpack.util.Utility;

import static starterpack.util.Logger.LOGGER;

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
        if (myPlayerState.getHealth() <= 3) {
            return Utility.spawnPoints.get(myPlayerIndex);
        }

        // stay put if in spawn and can buy proc
        if ((myPlayerState.getGold() >= 8) && !(myPlayerState.getItem() == Item.PROCRUSTEAN_IRON) && (playerPos == Utility.spawnPoints.get(myPlayerIndex))) {
            return playerPos;
        }

        // move towards mid if not already in mid
        if (myPlayerIndex == 0) {
            if (notInMid) {
                return new Position(playerX + 1, playerY + 1);
            }
        } else if (myPlayerIndex == 1) {
            if (notInMid) {
                return new Position(playerX - 1, playerY + 1);
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
        // weakest player in range = nobody
        int weakestInRangeHealth = 99;
        int weakestInRangeIndex = 0;
        // for i in range 4
        for (int i = 0; i < 4; i++) {
            // if i is not me
            if (i != myPlayerIndex) {
                int distToTarget = Utility.chebyshevDistance(gameState.getPlayerStateByIndex(myPlayerIndex).getPosition(), gameState.getPlayerStateByIndex(i).getPosition());
                int myPlayerRange = gameState.getPlayerStateByIndex(myPlayerIndex).getCharacterClass().getStatSet().getRange();
                if (distToTarget <= myPlayerRange) {
                    if (gameState.getPlayerStateByIndex(i).getHealth() < weakestInRangeHealth) {
                        weakestInRangeIndex = i;
                        weakestInRangeHealth = gameState.getPlayerStateByIndex(i).getHealth();
                    }
                }
            }
        }
        return weakestInRangeIndex;
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
