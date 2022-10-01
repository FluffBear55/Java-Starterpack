package starterpack.strategy;

import starterpack.Config;
import starterpack.game.*;
import starterpack.util.Logger;
import starterpack.util.Utility;

public class NewMetaStrategy implements Strategy {

    private Item primaryItem = Item.HUNTER_SCOPE;
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

    private Position fixBadDestination(Position destination, PlayerState playerState) {
        int speed = playerState.getStatSet().getSpeed();
        int minDistance = 100;
        Position fixedDestination = new Position(0, 0);
        for (int x = 0; x < Config.BOARD_SIZE; x++) {
            for (int y = 0; y < Config.BOARD_SIZE; y++) {
                Position currentPos = new Position(x, y);
                int dist = Utility.manhattanDistance(currentPos, destination);
                if (speed >= Utility.manhattanDistance(currentPos, playerState.getPosition()) && dist < minDistance) {
                    minDistance = dist;
                    fixedDestination = new Position(x, y);
                }
            }
        }
        return fixedDestination;
    }

    public CharacterClass strategyInitialize(int myPlayerIndex) {
        return CharacterClass.WIZARD;
    }
    public boolean useActionDecision(GameState gameState, int myPlayerIndex) {
        return false;
    }

    public Position moveActionDecision(GameState gameState, int myPlayerIndex) {

        PlayerState myPlayerState = gameState.getPlayerStateByIndex(myPlayerIndex);
        Position playerPos = myPlayerState.getPosition();
        Position spawnPos = Utility.spawnPoints.get(myPlayerIndex);

        int playerX = playerPos.getX();
        int playerY = playerPos.getY();
        boolean notInMid = !((playerX == 4 || playerX == 5) && (playerY == 4 || playerY == 5));
        boolean inDanger = isInDanger(gameState, myPlayerIndex);
        boolean inSpawn = (playerPos.getX() == spawnPos.getX() && playerPos.getY() == spawnPos.getY());

        /*
        // tp home if in danger
        if (inDanger) {
            return Utility.spawnPoints.get(myPlayerIndex);
        }
         */

        // stay in spawn if can buy item
        if ((myPlayerState.getGold() >= 8) && !(myPlayerState.getItem() == Item.HUNTER_SCOPE) && (inSpawn)) {
            return playerPos;
        }
        Logger.LOGGER.info("enough gold?: " + (myPlayerState.getGold() >= 8));
        Logger.LOGGER.info("has scope?: " + (myPlayerState.getItem() == Item.HUNTER_SCOPE));
        Logger.LOGGER.info("player index: " + myPlayerIndex);
        Logger.LOGGER.info("pos: " + playerPos);
        Logger.LOGGER.info("spawn pos: " + Utility.spawnPoints.get(myPlayerIndex));
        Logger.LOGGER.info("in spawn?: " + inSpawn);
        // otherwise go to mid
        Position optimalMidPoint = new Position();
        switch (myPlayerIndex) {
            case 0:
                optimalMidPoint = new Position(4, 4);
                break;
            case 1:
                optimalMidPoint = new Position(5, 4);
                break;
            case 2:
                optimalMidPoint = new Position(5, 5);
                break;
            case 3:
                optimalMidPoint = new Position(4, 5);
                break;
            }
            return fixBadDestination(optimalMidPoint, myPlayerState);
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

    public Item buyActionDecision(GameState gameState, int myPlayerIndex) {
        return primaryItem;
    }


}
