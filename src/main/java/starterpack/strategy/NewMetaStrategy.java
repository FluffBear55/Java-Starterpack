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

    private boolean isInMid(GameState gameState, int playerIndex) {

        PlayerState playerState = gameState.getPlayerStateByIndex(playerIndex);
        int playerX = playerState.getPosition().getX();
        int playerY = playerState.getPosition().getY();

        if ((playerX == 4 || playerX == 5) && (playerY == 4 || playerY == 5)) {
            return true;
        } else {
            return false;
        }
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

    private boolean isMidOccupied(GameState gameState, int myPlayerIndex) {
        PlayerState myPlayerState = gameState.getPlayerStateByIndex(myPlayerIndex);

        for (int i = 0; i < 4; i++) {
            // if i is not me
            if (i != myPlayerIndex && isInMid(gameState, i)) {
                return true;
            }
        }
        return false;
    }

    public CharacterClass strategyInitialize(int myPlayerIndex) {
        return CharacterClass.KNIGHT;
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
        /*
        // if someone in mid then move such that x = 3 or 6 and y = 3 or 6
        else if (isMidOccupied(gameState, myPlayerIndex) && notInMid) {
            Logger.LOGGER.info("dist to x=3: " + Math.abs(3 - playerX));
            Logger.LOGGER.info("dist to x=6: " + Math.abs(6 - playerX));
            int diffToX3 = 3 - playerX;
            int diffToX6 = 6 - playerY;
            int toMoveX = 0;
            if (Math.abs(diffToX3) < Math.abs(diffToX6)) {
                toMoveX = diffToX3;
            }
            else {
                toMoveX = diffToX6;
            }

            Logger.LOGGER.info("dist to y=3: " + Math.abs(3 - playerY));
            Logger.LOGGER.info("dist to y=6: " + Math.abs(6 - playerY));
            int diffToY3 = 3 - playerX;
            int diffToY6 = 6 - playerY;
            int toMoveY = Math.min(diffToY3, diffToY6);

            if (Math.abs(diffToY3) < Math.abs(diffToY6)) {
                toMoveY = diffToY3;
            }
            else {
                toMoveY = diffToY6;
            }


            Logger.LOGGER.info("to move X: " + toMoveX);
            Logger.LOGGER.info("to move Y: " + toMoveY);

            return fixBadDestination(new Position(playerX + toMoveX, playerY + toMoveY), myPlayerState);
        }
        */
        // otherwise go to mid
        else if (notInMid) {
            Position thePos = new Position(0, 0);
            if (myPlayerIndex == 0) {
                thePos =  new Position(playerX + 1, playerY + 1);
            } else if (myPlayerIndex == 1) {
                thePos =  new Position(playerX - 1, playerY + 1);
            } else if (myPlayerIndex == 2) {
                thePos =  new Position(playerX - 1, playerY - 1);
            } else if (myPlayerIndex == 3) {
                thePos =  new Position(playerX + 1, playerY - 1);
            }
            return thePos;
            /*
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

             */
        }
        else {
            return playerPos;
        }

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
