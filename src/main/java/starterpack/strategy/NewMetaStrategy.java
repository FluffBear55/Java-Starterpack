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

        return (playerX == 4 || playerX == 5) && (playerY == 4 || playerY == 5);
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
        return CharacterClass.KNIGHT;
    }

    public boolean useActionDecision(GameState gameState, int myPlayerIndex) {
        if (gameState.getPlayerStateByIndex(myPlayerIndex).getCharacterClass() != CharacterClass.ARCHER) {
            return true;
        }
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

        if (myPlayerState.getCharacterClass() == CharacterClass.KNIGHT) {
            // stay in spawn if can buy item
            if ((myPlayerState.getGold() >= 8) && !(myPlayerState.getItem() == Item.HUNTER_SCOPE) && (inSpawn)) {
                return playerPos;
            }
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
            }
            else {
                return playerPos;
            }
        }
        else if (myPlayerState.getCharacterClass() == CharacterClass.ARCHER) {
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
        return new Position(0, 0);
    }

    public int attackActionDecision(GameState gameState, int myPlayerIndex) {
        if (gameState.getPlayerStateByIndex(myPlayerIndex).getCharacterClass() == CharacterClass.ARCHER) {
            int currentBestPlayerIndex = 0;
            for (int i = 0; i < 4; i++) {
                // if i is not me
                if (i != myPlayerIndex) {
                    int distToTarget = Utility.chebyshevDistance(gameState.getPlayerStateByIndex(myPlayerIndex).getPosition(), gameState.getPlayerStateByIndex(i).getPosition());
                    int myPlayerRange = gameState.getPlayerStateByIndex(myPlayerIndex).getStatSet().getRange();
                    if ((distToTarget <= myPlayerRange) && gameState.getPlayerStateByIndex(i).getScore() >= gameState.getPlayerStateByIndex(currentBestPlayerIndex).getScore()) {
                        currentBestPlayerIndex = i;
                    }
                }
            }
            return currentBestPlayerIndex;
        }
        else if (gameState.getPlayerStateByIndex(myPlayerIndex).getCharacterClass() == CharacterClass.KNIGHT) {
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
        return 0;
    }

    public Item buyActionDecision(GameState gameState, int myPlayerIndex) {
        int knightCount = 0;
        // for i in range 4
        for (int i = 0; i < 4; i++) {
            // if i is not me
            if (i != myPlayerIndex) {
                if (gameState.getPlayerStateByIndex(i).getCharacterClass() == CharacterClass.KNIGHT) {
                    knightCount++;
                }
            }
        }
        if (knightCount >= 2 && (gameState.getPlayerStateByIndex(myPlayerIndex).getCharacterClass() != CharacterClass.ARCHER)) {
            return Item.STEEL_TIPPED_ARROW;
        }
        return primaryItem;
    }

}