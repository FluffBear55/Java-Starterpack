package starterpack.strategy;

import starterpack.game.*;
import starterpack.util.Utility;

public class RushMidStrategy implements Strategy {

    final private Item primaryItem = Item.SHIELD;
    private int shieldUseTurn;

    public CharacterClass strategyInitialize(int myPlayerIndex) {
        return CharacterClass.KNIGHT;
    }

    private boolean isInDanger(GameState gameState, int myPlayerIndex) {
        PlayerState myPlayerState = gameState.getPlayerStateByIndex(myPlayerIndex);
        for (int i = 0; i < 4; i++) {
            // if i is not me
            if (i != myPlayerIndex) {

                PlayerState targetPlayerState = gameState.getPlayerStateByIndex(i);
                int distToTarget = Utility.chebyshevDistance(gameState.getPlayerStateByIndex(myPlayerIndex).getPosition(), targetPlayerState.getPosition());
                int targetPlayerRange = targetPlayerState.getStatSet().getRange();
                int targetPlayerDamage;

                // i have proc then targets damage is 4
                if (myPlayerState.getItem() == Item.PROCRUSTEAN_IRON) {
                    targetPlayerDamage = 4;
                } else {
                    targetPlayerDamage = targetPlayerState.getStatSet().getDamage();
                }

                // if i am within targets range and they can kill me then
                if (distToTarget <= targetPlayerRange && targetPlayerDamage >= myPlayerState.getHealth()) {
                    // i am in danger
                    return true;
                }
            }
        }
        return false;
    }
    public boolean useActionDecision(GameState gameState, int myPlayerIndex) {
        if ((gameState.getPlayerStateByIndex(myPlayerIndex).getItem() == Item.SHIELD) && isInDanger(gameState, myPlayerIndex)) {
            shieldUseTurn = gameState.getTurn();
            return true;
        }
        return false;
    }
    public Position moveActionDecision(GameState gameState, int myPlayerIndex) {

        PlayerState myPlayerState = gameState.getPlayerStateByIndex(myPlayerIndex);
        Position playerPos = myPlayerState.getPosition();

        int playerX = playerPos.getX();
        int playerY = playerPos.getY();
        boolean notInMid = !((playerX == 4 || playerX == 5) && (playerY == 4 || playerY == 5));
        boolean inDanger = isInDanger(gameState, myPlayerIndex);

        // tp home if in danger and shield is done
        if (inDanger && (gameState.getTurn() == shieldUseTurn+2)) {
            return Utility.spawnPoints.get(myPlayerIndex);
        }

        // stay put if in spawn and can buy item
        if ((myPlayerState.getGold() >= primaryItem.getCost()) && (playerPos == Utility.spawnPoints.get(myPlayerIndex))) {
            return Utility.spawnPoints.get(myPlayerIndex);
        } else {
            // move towards mid if not already in mid
            if (notInMid) {
                if (myPlayerIndex == 0) {
                    return new Position(playerX + 1, playerY + 1);
                } else if (myPlayerIndex == 1) {
                    return new Position(playerX - 1, playerY + 1);
                } else if (myPlayerIndex == 2) {
                    return new Position(playerX - 1, playerY - 1);
                } else if (myPlayerIndex == 3) {
                    return new Position(playerX + 1, playerY - 1);
                }
            }
        }
        return playerPos;
    }

    public int attackActionDecision(GameState gameState, int myPlayerIndex) {
        // weakest player in range = nobody
        int weakestInRangeHealth = 99;
        int weakestInRangeIndex = 0;
        // for i in range 4
        for (int i = 0; i < 4; i++) {
            // if i is not me
            if (i != myPlayerIndex) {
                int distToTarget = Utility.chebyshevDistance(gameState.getPlayerStateByIndex(myPlayerIndex).getPosition(), gameState.getPlayerStateByIndex(i).getPosition());
                int myPlayerRange = gameState.getPlayerStateByIndex(myPlayerIndex).getStatSet().getRange();
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

    public Item buyActionDecision(GameState gameState, int myPlayerIndex) {
        return primaryItem;
    }

}
