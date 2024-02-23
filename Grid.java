import java.util.*;
import java.util.stream.IntStream;

public class Grid {

    private final int size;
    private final int blockRows;
    private final int blockColumns;

    private Cell[][] cellsGrid;
    private Cell[][][][] cellsBlocksGrid;

    public Grid(int blockSize) {
        this(blockSize, blockSize);
    }

    public Grid(int blockRows, int blockColumns) {
        this.blockRows = blockRows;
        this.blockColumns = blockColumns;
        this.size = blockRows * blockColumns;

        this.cellsGrid = new Cell[this.size][this.size];
        this.cellsBlocksGrid = new Cell[this.blockColumns][this.blockRows] [this.blockRows][this.blockColumns];
    }

    public Grid(Grid grid) {
        this.size = grid.size;
        this.blockRows = grid.blockRows;
        this.blockColumns = grid.blockColumns;

        this.cellsGrid = CopyGrid(grid.cellsGrid);
        this.cellsBlocksGrid = ConvertToGridBlocks(this.cellsGrid);
    }

    public Cell[][] getCellsGrid() {
        return cellsGrid;
    }


    /**
     * Creates an empty 2D grid and a 4D 'block' version of Cells/
     */
    public void CreateGrid() {
        CreateGrid(new Integer[this.size * this.size]);
    }


    public void CreateGrid(Integer[] setGrid) {
        LinkedList<Cell> filledCells = new LinkedList<>();

        for(int index = 0; index < this.size * this.size; ++index)
        {
            int[] coords = ConvertToCoords(index);
            int[] blockCoords = ConvertToBlockCoords(coords[0], coords[1]);

            Cell newCell = new Cell(this.size, index, coords[0], coords[1]);
            if(setGrid[index] != null)
            {
                newCell.setSolution(setGrid[index]);
                filledCells.add(newCell);
            }

            this.cellsGrid[coords[0]][coords[1]] = newCell;
            this.cellsBlocksGrid[blockCoords[0]][blockCoords[1]] [blockCoords[2]][blockCoords[3]] = newCell;
        }

        for(Cell cell : filledCells) RemovePossibleNumbers(cell, cell.getSolution());

        SolveGrid(CreateUnfilledIndexsList(), 1);
        System.out.println(PrintBlockGrid());
        System.out.println(ValidateGrid());
        CreatePuzzleGrid(CreateAllIndexsList());
    }


    /**
     * Create a shuffled list of all indices corresponding to a Cell in the grid.
     *
     * @return Shuffled LinkedList of Cell indices
     */
    public LinkedList<Integer> CreateUnfilledIndexsList() {

        LinkedList<Integer> cellsIndexs = new LinkedList<>();
        for(int i = 0; i < this.size * this.size; ++i) {
            int[] coords = ConvertToCoords(i);
            Cell cell = this.cellsGrid[coords[0]][coords[1]];

            if(!cell.isGiven()) cellsIndexs.add(i);
        }

        Collections.shuffle(cellsIndexs);
        return cellsIndexs;
    }


    public LinkedList<Integer> CreateAllIndexsList() {

        LinkedList<Integer> list = new LinkedList<>(IntStream.range(0, this.size*this.size).boxed().toList());
        Collections.shuffle(list);
        return list;
    }


    /**
     * Using the list of indices of unfilled Cells, recursively find a valid solved grid via DFS.
     *
     * @param unfilledIndexs - List of Cells that are yet to be filled.
     * @param maximumSolutions - Flag to indicate whether to find multiple solutions.
     *
     * @return Number of solutions found
     */
    public int SolveGrid(LinkedList<Integer> unfilledIndexs, int maximumSolutions) {

        // Base case
        // If no unfilled Cells left, grid is solved
        if(unfilledIndexs.isEmpty()) return 1;


        // Create a saved state copy of the grid
        Cell[][] savedGridState = CopyGrid(this.cellsGrid);


        // Select unfilled Cell and remove index from list
        Cell chosenCell = SelectUnfilledCellIndex(unfilledIndexs);

        LinkedList<Integer> updatedUnfilledCellsIndex  = new LinkedList<>(unfilledIndexs);
        updatedUnfilledCellsIndex.removeAll(List.of(chosenCell.getIndex()));


        // Iterate through possible solutions
        int solutions = 0;
        for(Integer chosenNumber : new LinkedList<>(chosenCell.getPossibleNumbers()))
        {
            // Update all Cells's possibleNumbers lists
            Cell chosenCellCopy = this.cellsGrid[chosenCell.getRow()][chosenCell.getColumn()];
            RemovePossibleNumbers(chosenCellCopy, chosenNumber);

            // Check next possible solution via effectively DFS, and increment if a solution is found
            solutions += SolveGrid(updatedUnfilledCellsIndex, maximumSolutions);
            if(solutions >= maximumSolutions) break;

            // If dead-end reached -> Restore saved state and try next possible number
            RestoreState(savedGridState);
        }

        // Dead-end
        return solutions;
    }



    /**
     * Select an unfilled Cell based on fewest number of remaining possible numbers as a heuristic.
     *
     * @param unfilledIndexs - List of Cells that are yet to be filled.
     *
     * @return The selected cell.
     */
    public Cell SelectUnfilledCellIndex(LinkedList<Integer> unfilledIndexs) {

        Cell chosenCell = null;
        for(Integer cellIndex : unfilledIndexs)
        {
            int[] coords = ConvertToCoords(cellIndex);
            Cell cell = this.cellsGrid[coords[0]][coords[1]];

            if(null == chosenCell) chosenCell = cell;

            if(cell.getPossibleNumbers().size() < chosenCell.getPossibleNumbers().size()) chosenCell = cell;

        }
        return chosenCell;
    }


    /**
     * Check the grid for repeated numbers in each row, column, and block.
     *
     * @return Boolean of whether the grid is valid.
     */
    public boolean ValidateGrid() {
        for(Cell[] cellsRow : this.cellsGrid) {
            for(Cell cell : cellsRow) {

                int chosenRow = cell.getRow();
                int chosenColumn = cell.getColumn();

                // Get all solution numbers of current row
                LinkedList<Integer> row = new LinkedList<>
                        (
                                Arrays.stream(this.cellsGrid[chosenRow])
                                        .map(Cell :: getSolution)
                                        .toList()
                        );

                // Get all solution numbers of current colummn
                LinkedList<Integer> column = new LinkedList<>();
                for(int i = 0; i < this.size; ++i) column.add(this.cellsGrid[i][chosenColumn].getSolution());

                // Get all solution numbers of current block
                int[] blockCoords = ConvertToBlockCoords(chosenRow, chosenColumn);
                LinkedList<Integer> block = new LinkedList<>
                        (
                                Arrays.stream(Arrays.stream(this.cellsBlocksGrid[blockCoords[0]][blockCoords[1]])
                                                .map(blockRow -> Arrays.stream(blockRow)
                                                        .map(Cell :: getSolution)
                                                        .toList()
                                                        .toArray(new Integer[this.blockColumns])
                                                )
                                                .toList()
                                                .toArray(new Integer[this.blockRows][]))
                                        .flatMap(Arrays :: stream)
                                        .toList()
                        );

                // Check if current number appears more than once (i.e. other than itself)
                Integer current = cell.getSolution();
                if(row.stream().filter(current :: equals).count() > 1
                        || column.stream().filter(current :: equals).count() > 1
                        || block.stream().filter(current :: equals).count() > 1
                ) return false;

            }
        }
        return true;
    }


    /**
     * Create a Sudoku puzzle that has a unique single solution.
     *
     * @param cellsIndexs - Indices of all Cells in the grid
     */
    public void CreatePuzzleGrid(LinkedList<Integer> cellsIndexs) {

        // Create a list to store the indices that are unfilled
        LinkedList<Integer> unfilledIndexs = new LinkedList<>();


        // Remove each cell at each index until a board with multiple solutions is found
        for(Integer cellIndex : cellsIndexs)
        {
            //PrintBlockGrid();
            // Save the grid before removing the current Cell
            Cell[][] beforeRemovalState = CopyGrid(this.cellsGrid);

            // Remove Cell at current index
            int[] coords = ConvertToCoords(cellIndex);
            Cell cell = this.cellsGrid[coords[0]][coords[1]];
            cell.setGiven(false);
            unfilledIndexs.add(cellIndex);
            AddPossibleNumbers(unfilledIndexs);


            // Get the number of solutions
            int solutions = SolveGrid(unfilledIndexs, 2);


            // Restore the board to before most recent removal and break out
            if(solutions > 1) {
                unfilledIndexs.removeAll(List.of(cellIndex));
                RestoreState(beforeRemovalState);
                //break;
            }
        }

        //AddPossibleNumbers(unfilledIndexs);

        System.out.println(PrintBlockGrid());
        //System.out.println(SolveGrid(unfilledIndexs, 2));


    }


    /**
     * Convert the coordinates that refer to the row and column to a corresponding block coordinates that refer to the
     * block and offsets within the block.
     *
     * @param row - Row coordinate.
     * @param column - Column coordinate.
     *
     * @return An array that holds the block's row coordinate, column coordinate, Cell's row offset, and Cell's column
     * offset.
     */
    public int[] ConvertToBlockCoords(int row, int column) {
        int[] blockCoords = new int[4];

        blockCoords[0] = row / this.blockRows;
        blockCoords[1] = column / this.blockColumns;

        blockCoords[2] = row % this.blockRows;
        blockCoords[3] = column % this.blockColumns;

        return blockCoords;
    }


    /**
     * Convert the index referring to a Cell in a flat grid to corresponding row and column coordinates
     *
     * @param index - Index of the Cell.
     *
     * @return An array that holds the Cell's row and column coordinates.
     */
    public int[] ConvertToCoords(int index) {
        int[] coords = new int[2];

        coords[0] = index / this.size;
        coords[1] = index % this.size;

        return coords;
    }


    /**
     * Create a copy of the grid to be used for safe mutation without affecting the original.
     *
     * @param cellsGrid - 2D array representing the grid to be copied.
     *
     * @return A 2D array copy that is a new object.
     */
    public Cell[][] CopyGrid(Cell[][] cellsGrid) {

        return Arrays.stream(cellsGrid)
                        .map((Cell[] cellRow) -> Arrays.stream(cellRow)
                                .map(Cell::new)
                                .toList()
                                .toArray(new Cell[this.size])
                        )
                        .toList()
                        .toArray(new Cell[this.size][]);
    }


    /**
     * Convert the 2D grid of Cells to a 4D grid of blocks
     *
     * @param cellsGrid - 2D grid to convert
     *
     * @return The 4D representation of the grid as blocks
     */
    public Cell[][] [][] ConvertToGridBlocks(Cell[][] cellsGrid) {

        Cell[][] [][] newCellsBlocksGrid = new Cell[this.blockColumns][this.blockRows] [this.blockRows][this.blockColumns];

        for(int row = 0; row < this.size; ++row)
        {
            for(int column = 0; column < this.size; ++column)
            {
                int[] blockCoords = ConvertToBlockCoords(row, column);

                Cell newCell = cellsGrid[row][column];
                newCellsBlocksGrid[blockCoords[0]][blockCoords[1]] [blockCoords[2]][blockCoords[3]] = newCell;
            }
        }

        return newCellsBlocksGrid;
    }


    /**
     * Restore the cellsGrid and cellsBlockGrid attributes to the saved state.
     *
     * @param savedGridState - The saved state of cellsGrid to be restored.
     */
    public void RestoreState(Cell[][] savedGridState) {
        this.cellsGrid = CopyGrid(savedGridState);
        this.cellsBlocksGrid = ConvertToGridBlocks(this.cellsGrid);
    }


    /**
     * Remove the chosen number from the possible numbers remaining for all Cells in same row, column, and block as the
     * Cell selected.
     *
     * @param chosenCell - Cell that is selected.
     * @param chosenNumber - Value that is set to the Cell.
     */
    public void RemovePossibleNumbers(Cell chosenCell, Integer chosenNumber) {

        // Update chosen Cell
        chosenCell.setSolution(chosenNumber);
        chosenCell.getPossibleNumbers().clear();


        // Get the row, column, and block Cell is in
        int chosenRow = chosenCell.getRow();
        int chosenColumn = chosenCell.getColumn();

        Cell[] cellsRow = this.cellsGrid[chosenRow];
        Cell[] cellsColumn = new Cell[this.size];
        for(int i = 0; i < this.size; ++i) cellsColumn[i] = this.cellsGrid[i][chosenColumn];

        int[] blockCoords = ConvertToBlockCoords(chosenRow, chosenColumn);
        Cell[][] cellsBlock = this.cellsBlocksGrid[blockCoords[0]][blockCoords[1]];


        // Remove chosen number from possible numbers of Cells in same row, column, block
        for(Cell cell : cellsRow) cell.getPossibleNumbers().remove(chosenNumber);
        for(Cell cell : cellsColumn) cell.getPossibleNumbers().remove(chosenNumber);
        for(Cell[] cellsBlockCols : cellsBlock) for(Cell cell : cellsBlockCols) cell.getPossibleNumbers().remove(chosenNumber);

    }


    /**
     * Add possible numbers to all unfilled Cells based on visible Cells in the same row, column, and block.
     *
     * @param unfilledIndexs - List of indices referring to all unfilled Cells
     */
    public void AddPossibleNumbers(LinkedList<Integer> unfilledIndexs) {

        for(int unfilledIndex : unfilledIndexs)
        {
            // Reset the possible numbers of each unfilled Cell to the full possibilities
            int[] coords = ConvertToCoords(unfilledIndex);
            Cell unfilledCell = this.cellsGrid[coords[0]][coords[1]];

            unfilledCell.getPossibleNumbers().addAll(unfilledCell.FillPossibleNumbers(this.size));


            // Get the row, column, and block Cell is in
            int chosenRow = unfilledCell.getRow();
            int chosenColumn = unfilledCell.getColumn();

            Cell[] cellsRow = this.cellsGrid[chosenRow];
            Cell[] cellsColumn = new Cell[this.size];
            for(int i = 0; i < this.size; ++i) cellsColumn[i] = this.cellsGrid[i][chosenColumn];

            int[] blockCoords = ConvertToBlockCoords(chosenRow, chosenColumn);
            Cell[][] cellsBlock = this.cellsBlocksGrid[blockCoords[0]][blockCoords[1]];


            // Remove all numbers that appear in the row, column, and block from the unfilled Cell's possibilities
            for(Cell cell : cellsRow) if(cell.isGiven()) unfilledCell.getPossibleNumbers().remove(cell.getSolution());
            for(Cell cell : cellsColumn) if(cell.isGiven()) unfilledCell.getPossibleNumbers().remove(cell.getSolution());
            for(Cell[] cellsBlockCols : cellsBlock) for(Cell cell : cellsBlockCols) if(cell.isGiven()) unfilledCell.getPossibleNumbers().remove(cell.getSolution());
        }
    }

    /**
     * Print the grid in a 2D representation
     */
    public void PrintGrid() {
        for(Cell[] row : this.cellsGrid) {
            System.out.println(Arrays.stream(row)
                    .map(Cell::getSolution)
                    .toList());
        }
    }

    /**
     * Print the grid in a block representation
     */
    public String PrintBlockGrid() {
        String output = "";
        for(int x = 0; x < this.size; ++x)
        {
            for(int y = 0; y < this.size; ++y)
            {
                int[] blockCoords = ConvertToBlockCoords(x,y);
                Cell cell = this.cellsBlocksGrid[blockCoords[0]][blockCoords[1]] [blockCoords[2]][blockCoords[3]];
                output +=  "[" + (cell.isGiven() ? cell.getSolution() : " ") + "]";
                //System.out.print(cell.getPossibleNumbers());

                if(y != this.size - 1)output += ((y+1) % this.blockColumns == 0) ? "   " : "";

            }
            output += "\n";
            if( (x+1) % this.blockRows == 0 ) output += "\n";
        }

        output += "----------";
        return output;

    }


}
