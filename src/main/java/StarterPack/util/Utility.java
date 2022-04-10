package StarterPack.util;

import StarterPack.Config;
import StarterPack.player.Position;

import java.util.concurrent.ThreadLocalRandom;

public class Utility {
  public static int manhattanDistance(Position p1, Position p2) {
    return Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
  }

  public static int squareDistance(Position p1, Position p2) {
    return Math.max(Math.abs(p1.getX()-p2.getX()), Math.abs(p1.getY()- p2.getY()));
  }

  public static int randomInt(int min, int max) {
    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }

  public static boolean inBounds(Position p) {
    // Assume board runs from 0 to BOARD_SIZE - 1
    return ((p.getX() >= 0)
        && (p.getX() < Config.BOARD_SIZE)
        && (p.getY() >= 0)
        && (p.getY() < Config.BOARD_SIZE));
  }


}
