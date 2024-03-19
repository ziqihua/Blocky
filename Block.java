import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Block implements IBlock {

    /**
     * ===Representation Invariants ===
     *
     * - children.size() == 0 or children.size() == 4
     *
     * - If this Block has children,
     *      - their max_depth is the same as that of this Block,
     *      - their size is half that of this Block,
     *      - their level is one greater than that of this Block,
     *      - their position is determined by the position and
     *        size of this Block, as defined in the Assignment handout, and
     *      - this Block's color is null
     *
     * - If this Block has no children,
     *      - its color is not null
     *      - level <= max_depth
     */

    //  The top left and bottom right points delimiting this block
    private Point topLeft;
    private Point botRight;


    /**
     *  If this block is not subdivided, <color> stores its color.
     *   Otherwise, <color> is <null> and this  block's sublocks
     *   store their individual colors.
     */
    private Color color;

    // The level of this block within the overall block structure.
    //    * The outermost block corresponding to the root of the tree is at level/depth zero.
    //    * If a block is at level i, its children are at level i+1.
    private int depth;

    private IBlock topLeftTree;
    private IBlock topRightTree;
    private IBlock botLeftTree;
    private IBlock botRightTree;

    private IBlock parent;

    /**
     * No-argument constructor. This should:
     * - Initialize the top left and bottom right to two dummy points at (0, 0)
     * - Set the depth to be 0
     * - Set all parent and child pointers to null
     *
     * Even if you don't use this constructor anywhere, it may be useful for testing.
     */
    public Block() {
        this.topLeft = new Point(0,0);
        this.botRight = new Point(8, 8);
        this.topLeftTree = null;
        this.topRightTree = null;
        this.botLeftTree = null;
        this.botRightTree = null;
        this.depth = 0;
    }

    // ----------------------------------------------------------
    /**
     * Create a new Quad object.
     *
     * @param topL   top left point / bound of this block
     * @param botR   bottom right point / bound of this block
     * @param depth  of this block
     * @param parent of this block
     */
    public Block(Point topL, Point botR, int depth, Block parent) {
        this.topLeft = topL;
        this.botRight = botR;
        this.depth = depth;
        this.parent = parent;
        this.topLeftTree = null;
        this.topRightTree = null;
        this.botLeftTree = null;
        this.botRightTree = null;
    }

    @Override
    public int depth() {
        return depth;
    }

    /**
     * smash this block into 4 sub block of random color. the depth of the new
     * blocks should be less than maximum depth
     *
     * @param maxDepth the max depth of this board/quadtree
     */
    @Override
    public void smash(int maxDepth) {
        if (this.depth() < maxDepth && this.isLeaf()) {
            Random random = new Random();
            Point topLeftP = this.getTopLeft();
            Point botRightP = this.getBotRight();
            int midX = (topLeftP.getX() + botRightP.getX()) / 2;
            int midY = (topLeftP.getY() + botRightP.getY()) / 2;
            Point midOne = new Point(midX,midY);
            Point midTop = new Point(midX, topLeftP.getY());
            Point midRight = new Point(botRightP.getX(), midY);
            Point midLeft = new Point(topLeftP.getX(), midY);
            Point midBot = new Point(midX, botRightP.getY());
            this.setColor(null);
            IBlock topLeft = new Block(topLeftP, midOne, this.depth() + 1, this);
            int colorIndex = random.nextInt(COLORS.length);
            topLeft.setColor(COLORS[colorIndex]);
            this.setTopLeftTree(topLeft);
            IBlock topRight = new Block(midTop, midRight, this.depth() + 1, this);
            colorIndex = random.nextInt(COLORS.length);
            topRight.setColor(COLORS[colorIndex]);
            this.setTopRightTree(topRight);
            IBlock botLeft = new Block(midLeft, midBot, this.depth() + 1, this);
            colorIndex = random.nextInt(COLORS.length);
            botLeft.setColor(COLORS[colorIndex]);
            this.setBotLeftTree(botLeft);
            IBlock botRight = new Block(midOne, botRightP, this.depth() + 1, this);
            colorIndex = random.nextInt(COLORS.length);
            botRight.setColor(COLORS[colorIndex]);
            this.setBotRightTree(botRight);
        }
    }

    /**
     * used by {@link IGame#randomInit()} random_init
     * to keep track of sub blocks.
     *
     * The children are returned in this order:
     * upper-left child (NE),
     * upper-right child (NW),
     * lower-right child (SW),
     * lower-left child (SE).
     *
     * @return the list of all the children of this block (clockwise order,
     *         starting with top left block)
     */
    @Override
    public List<IBlock> children() {
        List<IBlock> allChildren = new ArrayList<IBlock>();
        if (this.topLeftTree != null) {
            allChildren.add(this.topLeftTree);
        }
        if (this.topRightTree != null) {
            allChildren.add(this.topRightTree);
        }
        if (this.botRightTree != null) {
            allChildren.add(this.botRightTree);
        }
        if (this.botLeftTree != null) {
            allChildren.add(this.botLeftTree);
        }

        return allChildren;
    }

    /**
     * rotate this block clockwise.
     *
     *  To rotate, first move the children's pointers
     *  then recursively update the top left and
     *  bottom right points of each child.
     *
     *  You may want to write a helper method that
     *  takes in a Block and its new topLeft and botRight and
     *  sets these values for the current Block before calculating
     *  the values for its children and recursively setting them.
     */
    @Override
    public void rotate() {
        if (!this.children().isEmpty()) {
            IBlock temporary = this.topLeftTree;
            this.topLeftTree = this.botLeftTree;
            this.botLeftTree = this.botRightTree;
            this.botRightTree = this.topRightTree;
            this.topRightTree = temporary;
            adjustChildrenPositions();
        }
    }

    private void adjustChildrenPositions() {
        int midX = (this.topLeft.getX() + this.botRight.getX()) / 2;
        int midY = (this.topLeft.getY() + this.botRight.getY()) / 2;
        if (this.topLeftTree != null) {
            updateChildBlock((Block)this.topLeftTree, new Point(this.topLeft.getX(),
                    this.topLeft.getY()), new Point(midX, midY));
        }
        if (this.botLeftTree != null) {
            updateChildBlock((Block)this.botLeftTree,
                    new Point(this.topLeft.getX(), midY), new Point(midX, this.botRight.getY()));
        }
        if (this.botRightTree != null) {
            updateChildBlock((Block)this.botRightTree,
                    new Point(midX, midY), new Point(this.botRight.getX(), this.botRight.getY()));
        }
        if (this.topRightTree != null) {
            updateChildBlock((Block)this.topRightTree,
                    new Point(midX, this.topLeft.getY()), new Point(this.botRight.getX(), midY));
        }
    }

    public void updateChildBlock(Block child, Point newTopLeft, Point newBotRight) {
        child.topLeft = newTopLeft;
        child.botRight = newBotRight;

        int midX = (newTopLeft.getX() + newBotRight.getX()) / 2;
        int midY = (newTopLeft.getY() + newBotRight.getY()) / 2;

        if (child.topLeftTree != null) {
            updateChildBlock((Block)child.topLeftTree, newTopLeft, new Point(midX, midY));
        }
        if (child.topRightTree != null) {
            updateChildBlock((Block)child.topRightTree,
                    new Point(midX, newTopLeft.getY()), new Point(newBotRight.getX(), midY));
        }
        if (child.botLeftTree != null) {
            updateChildBlock((Block)child.botLeftTree,
                    new Point(newTopLeft.getX(), midY), new Point(midX, newBotRight.getY()));
        }
        if (child.botRightTree != null) {
            updateChildBlock((Block)child.botRightTree, new Point(midX, midY), newBotRight);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Block block = (Block) o;
        return this.depth == block.depth() && this.topLeft == block.getTopLeft()
                && this.botRight == block.getBotRight() && this.color == block.getColor();
    }




    /*
     * ========================
     *  Block getters
     *  You should implement these yourself.
     *  The implementations should be very simple.
     * ========================
     */

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public Point getTopLeft() {
        return this.topLeft;
    }

    @Override
    public Point getBotRight() {
        return this.botRight;
    }

    @Override
    public boolean isLeaf() {
        if (this.children().isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public IBlock getTopLeftTree() {
        return this.topLeftTree;
    }

    @Override
    public IBlock getTopRightTree() {
        return this.topRightTree;
    }

    @Override
    public IBlock getBotLeftTree() {
        return this.botLeftTree;
    }

    @Override
    public IBlock getBotRightTree() {
        return this.botRightTree;
    }

    public IBlock getParent() {
        return this.parent;
    }


    /*
     * ========================
     *  Provided setters
     *  Don't delete these!
     *  Necessary for testing.
     * ========================
     */

    @Override
    public void setColor(Color c) {
        this.color = c;
    }

    public void setTopLeftTree(IBlock topLeftTree) {
        this.topLeftTree = topLeftTree;
    }

    public void setTopRightTree(IBlock topRightTree) {
        this.topRightTree = topRightTree;
    }

    public void setBotLeftTree(IBlock botLeftTree) {
        this.botLeftTree = botLeftTree;
    }

    public void setBotRightTree(IBlock botRightTree) {
        this.botRightTree = botRightTree;
    }

    public void setParent(IBlock parent) {
        this.parent = parent;
    }
}
