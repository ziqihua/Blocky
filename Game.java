import java.awt.*;
import java.util.ArrayDeque;
import java.util.List;

public class Game implements IGame {

    private int    maxDepth;
    private Color  target;
    private IBlock root;


    public Game(int maxDepth, Color target) {
        this.maxDepth = maxDepth;
        this.target = target;
        this.root = randomInit();
    }


    /**
     * @return the maximum dept of this blocky board.
     */
    @Override
    public int maxDepth() {
        return this.maxDepth;
    }

    /**
     * initializes a random board game. Details about how to approach
     * this are available in the assignment instructions; there is no
     * specific output that you need to generate, but calls to this
     * method should generally result in "interesting" game boards.
     *
     * @return the root of the tree of blocks
     */
    @Override
    public IBlock randomInit() {
        this.root = new Block();
        randomInitHelper(this.root, 0);
        return this.root;
    }

    private void randomInitHelper(IBlock block, int curDepth) {
        if (curDepth < this.maxDepth()) {
            double chanceToSmash = Math.random();
            if (chanceToSmash < 0.75) {
                block.smash(this.maxDepth);
                for (IBlock child : block.children()) {
                    randomInitHelper(child, curDepth + 1);
                }
            } else {
                int colorIndex = (int) (Math.random() * IBlock.COLORS.length);
                block.setColor(IBlock.COLORS[colorIndex]);
            }
        } else {
            int colorIndex = (int) (Math.random() * IBlock.COLORS.length);
            block.setColor(IBlock.COLORS[colorIndex]);
        }
    }

    /**
     * Traverse the tree of blocks to find a sub block based on its id
     *
     * @param pos the id of the block
     * @return the block with id pos or null
     */
    @Override
    public IBlock getBlock(int pos) {
        if (root == null || pos < 0) {
            return null; // Return null if the tree is empty or the position is invalid.
        }

        ArrayDeque<IBlock> queue = new ArrayDeque<>();
        queue.offer(root);

        int index = 0; // Start with the root block which has an index of 0

        // Perform BFS until we find the block or the queue is empty
        while (!queue.isEmpty()) {
            IBlock current = queue.poll();

            // Check if the current block is the one we're looking for
            if (index == pos) {
                return current;
            }

            // Increment the index as we're moving to the next block
            index++;

            // Enqueue child blocks if they exist
            if (current.getTopLeftTree() != null) {
                queue.offer(current.getTopLeftTree());
            }
            if (current.getTopRightTree() != null) {
                queue.offer(current.getTopRightTree());
            }
            if (current.getBotRightTree() != null) {
                queue.offer(current.getBotRightTree());
            }
            if (current.getBotLeftTree() != null) {
                queue.offer(current.getBotLeftTree());
            }
        }

        // If we've gone through the tree without finding the block, it doesn't exist
        return null;
    }

    /**
     * @return the root of the quad tree representing this
     * blockly board
     *
     * @implNote getRoot() == getBlock(0)
     */
    @Override
    public IBlock getRoot() {
        return this.root;
    }

    /**
     * The two blocks  must be at the same level / have the same size.
     * We should be able to swap a block with no sub blocks with
     * one with sub blocks.
     *
     *
     * @param x the block to swap
     * @param y the other block to swap
     */
    @Override
    public void swap(int x, int y) {
        Block blockX = (Block) getBlock(x);
        Block blockY = (Block) getBlock(y);

        // Check if both blocks exist and are at the same depth
        if (blockX != null && blockY != null && blockX.depth() == blockY.depth()) {
            Block xParent = (Block)blockX.getParent();
            Block yParent = (Block)blockY.getParent();
            // Check for valid parents before attempting to swap
            if (xParent == null || yParent == null) {
                return; // Can't swap if either block has no parent
            }
            blockX.setParent(yParent);
            blockY.setParent(xParent);

            int xPosition = findPositionInParent(xParent, blockX);
            int yPosition = findPositionInParent(yParent,blockY);
            if (xPosition == -1 || yPosition == -1) {
                return;
            }

            setChildAtPosition(xParent, blockY, xPosition);
            setChildAtPosition(yParent, blockX, yPosition);
            blockX.setParent(yParent);
            blockY.setParent(xParent);

            Point xTopLeft = blockX.getTopLeft();
            Point xBotRight = blockX.getBotRight();
            Point yTopLeft = blockY.getTopLeft();
            Point yBotRight = blockY.getBotRight();
            blockX.updateChildBlock(blockX, yTopLeft, yBotRight);
            blockY.updateChildBlock(blockY, xTopLeft, xBotRight);
        }

    }

    private int findPositionInParent(Block parent, IBlock child) {
        if (parent.getTopLeftTree().equals(child)) {
            return 0;
        } else if (parent.getTopRightTree().equals(child)) {
            return 1;
        } else if (parent.getBotRightTree().equals(child)) {
            return 2;
        } else if (parent.getBotLeftTree().equals(child)) {
            return 3;
        }
        return -1; // Not found
    }

    private void setChildAtPosition(Block parent, IBlock child, int pos) {
        switch (pos) {
            case 0:
                parent.setTopLeftTree(child);
                break;
            case 1:
                parent.setTopRightTree(child);
                break;
            case 2:
                parent.setBotRightTree(child);
                break;
            case 3:
                parent.setBotLeftTree(child);
                break;
            default:
                break;
        }
    }

    /**
     * Turns (flattens) the quadtree into a 2D-array of blocks.
     * Each cell in the array represents a unit cell.
     * This method should not mutate the tree.
     * @return a 2D array of the tree
     */
    @Override
    public IBlock[][] flatten() {
        int arraySize = (int)Math.pow(2, this.maxDepth);
        IBlock[][] grid = new IBlock[arraySize][arraySize];

        // Initialize the flattening process from the root, covering the entire grid.
        flattenHelper(this.root, grid, 0, 0, arraySize);
        return grid;
    }

    private void flattenHelper(IBlock block, IBlock[][] grid,
                               int startX, int startY, int blockSize) {
        if (block == null) {
            return;
        }

        if (block.isLeaf()) {
            for (int i = startY; i < startY + blockSize; i++) {
                for (int j = startX; j < startX + blockSize; j++) {
                    grid[i][j] = block;
                }
            }
        } else {
            int newSize = blockSize / 2;
            List<IBlock> children = block.children();
            if (children.size() == 4) { // Ensure the block has exactly four children
                flattenHelper(children.get(0), grid,
                        startX, startY, newSize); // Top-left
                flattenHelper(children.get(1), grid,
                        startX + newSize, startY, newSize); // Top-right
                flattenHelper(children.get(2), grid,
                        startX + newSize, startY + newSize, newSize); // Bottom-right
                flattenHelper(children.get(3), grid,
                        startX, startY + newSize, newSize); // Bottom-left
            }
        }
    }

    /**
     * computes the scores based on perimeter blocks of the same color
     * as the target color.
     * The quadtree must be flattened first
     *
     * @return the score of the user (corner blocs count twice)
     */
    @Override
    public int perimeterScore() {
        int score = 0;
        IBlock[][] grid = this.flatten();
        for (int i = 0; i < grid.length; i++) {
            if (grid[0][i].getColor() == this.target) {
                score++;
            }
            if (grid[i][0].getColor() == this.target) {
                score++;
            }
            if (grid[grid.length - 1][i].getColor() == this.target) {
                score++;
            }
            if (grid[i][grid.length - 1].getColor() == this.target) {
                score++;
            }
        }
        return score;
    }

    /**
     * This method will be useful to test your code
     * @param root the root of this blocky board
     */
    @Override
    public void setRoot(IBlock root) {
        this.root = root;
    }
}
