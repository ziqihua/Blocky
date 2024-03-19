import static org.junit.Assert.*;
import org.junit.Test;

public class BlockTest {

    @Test
    public void testBlock() {
        Block root = new Block();
        Point topLeft = new Point(0, 0);
        Point botRight = new Point(8, 8);
        assertNull(root.getParent());
        assertNull(root.getColor());
        assertEquals(0, root.depth());
        assertEquals(topLeft.getX(), root.getTopLeft().getX());
        assertEquals(botRight.getY(), root.getBotRight().getY());
    }

    @Test
    public void testSubBlock() {
        Point topLeft = new Point(2, 0);
        Point botRight = new Point(4, 2);
        Block block = new Block(topLeft, botRight, 0, null);
        assertEquals(topLeft, block.getTopLeft());
        assertEquals(botRight, block.getBotRight());
    }

    @Test
    public void testSmash() {
        Point tl = new Point(0, 0);
        Point br = new Point(8, 8);
        Block block = new Block(tl, br, 0, null);
        block.smash(2);
        assertEquals(0, block.depth());
        assertEquals(1, block.getTopLeftTree().depth());
    }

    @Test
    public void testChildren() {
        Point tl = new Point(0, 0);
        Point br = new Point(8, 8);
        Block block = new Block(tl, br, 0, null);
        block.smash(2);
        assertEquals(4, block.children().size());
    }

    @Test
    public void testIsLeaf() {
        Point tl = new Point(0, 0);
        Point br = new Point(8, 8);
        Block block = new Block(tl, br, 0, null);
        block.smash(2);
        assertTrue(block.getTopLeftTree().isLeaf());
        assertFalse(block.isLeaf());
    }

    @Test
    public void testRotate() {
        Point tl = new Point(0, 0);
        Point br = new Point(8, 8);
        Block block = new Block(tl, br, 0, null);
        block.smash(2);
        Block tlTree = (Block) block.getTopLeftTree();
        Block trTree = (Block) block.getTopRightTree();
        Block brTree = (Block) block.getBotRightTree();
        Block blTree = (Block) block.getBotLeftTree();
        block.rotate();
        assertEquals(block.getTopLeftTree(), blTree);
        assertEquals(block.getTopRightTree(), tlTree);
        assertEquals(block.getBotRightTree(), trTree);
        assertEquals(block.getBotLeftTree(), brTree);
    }
}