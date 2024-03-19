import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class GameTest {

    @Test
    public void testGame() {
        Game game = new Game(2, Color.BLACK);
        assertEquals(game.maxDepth(), 2);
    }

    @Test
    public void testRandomInit() {
        Game game = new Game(3, Color.BLACK);
        game.randomInit();
        assertEquals(0, game.getRoot().depth());
    }

    @Test
    public void testGetBlock() {
        Game game = new Game(1, Color.BLACK);
        game.randomInit();
        IBlock root = game.getRoot();
        root.smash(1);
        IBlock topLeftTree =  root.getTopLeftTree();
        IBlock topRightTree =  root.getTopRightTree();
        IBlock botRightTree =  root.getBotRightTree();
        IBlock botLeftTree =  root.getBotLeftTree();
        assertEquals(root, game.getBlock(0));
        assertEquals(topLeftTree, game.getBlock(1));
        assertEquals(topRightTree, game.getBlock(2));
        assertEquals(botRightTree, game.getBlock(3));
        assertEquals(botLeftTree, game.getBlock(4));
        assertNull(game.getBlock(5));
    }

    @Test
    public void testSwap() {
        Game game = new Game(1, Color.BLACK);
        IBlock root = game.getRoot();
        root.smash(1);
        IBlock topLeftTree =  root.getTopLeftTree();
        IBlock botRightTree =  root.getBotRightTree();
        game.swap(1, 3);
        assertEquals(topLeftTree, root.getBotRightTree());
        assertEquals(botRightTree, root.getTopLeftTree());
    }

    @Test
    public void testFlatten() {
        Game game = new Game(2, Color.BLACK);
        IBlock root = game.getRoot();
        root.smash(2);
        IBlock topLeft1 =  root.getTopLeftTree();
        topLeft1.smash(2);
        IBlock topLeft2 =  topLeft1.getTopLeftTree();
        IBlock[][] grid = game.flatten();
        assertEquals(topLeft2, grid[0][0]);
    }

    @Test
    public void testPerimeterScore() {
        Game game = new Game(2, Color.BLUE);
        Block root = new Block();
        game.setRoot(root);
        game.getRoot().smash(2);
        IBlock botLeftTree =  game.getRoot().getBotLeftTree();
        botLeftTree.smash(2);
        IBlock topLeftTree =  game.getRoot().getTopLeftTree();
        IBlock topRightTree =  game.getRoot().getTopRightTree();
        IBlock botRightTree =  game.getRoot().getBotRightTree();
        topLeftTree.setColor(Color.BLUE);
        topRightTree.setColor(Color.BLUE);
        botRightTree.setColor(Color.BLACK);
        IBlock blTopLeft = botLeftTree.getTopLeftTree();
        blTopLeft.setColor(Color.BLUE);
        IBlock blTopRight = botLeftTree.getTopRightTree();
        blTopRight.setColor(Color.BLACK);
        IBlock blBotRight = botLeftTree.getBotRightTree();
        blBotRight.setColor(Color.BLACK);
        IBlock blBotLeft = botLeftTree.getBotLeftTree();
        blBotLeft.setColor(Color.BLACK);
        assertEquals(9,game.perimeterScore());
    }
}