package starterpack;

import starterpack.player.CharacterClass;
import starterpack.player.Item;
import starterpack.player.Position;

public class InfiniteLoopStrategy implements Strategy{
    @Override
    public CharacterClass strategyInitialize() {
        return null;
    }

    @Override
    public Position moveActionDecision(GameState gameState, int myPlayerIndex) {
        return null;
    }

    @Override
    public int attackActionDecision(GameState gameState, int myPlayerIndex) {
        int i = 0;
        while(i < 100) {}
        return 0;
    }

    @Override
    public Item buyActionDecision(GameState gameState, int myPlayerIndex) {
        return null;
    }

    @Override
    public boolean useActionDecision(GameState gameState, int myPlayerIndex) {
        return false;
    }
}