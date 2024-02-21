import java.util.*;

public class Grid {

    private int size;
    private int blockSize;

    private Cell[][] cellsGrid;
    private Cell[][][][] cellsBlocksGrid;

    public Grid(int blockSize) {
        this.size = blockSize * blockSize;
        this.blockSize = blockSize;

        this.cellsGrid = new Cell[this.size][this.size];
        this.cellsBlocksGrid = new Cell[blockSize][blockSize] [blockSize][blockSize];

        CreateGrid();
        CreateSolvedGrid();
    }


    public void CreateGrid() {

        for(int row = 0; row < this.size; ++row)
        {
            for(int column = 0; column < this.size; ++column)
            {
                int[] blockCoords = ConvertToBlockCoords(row, column);

                Cell newCell = new Cell(this.size, row, column);

                this.cellsGrid[row][column] = newCell;
                this.cellsBlocksGrid[blockCoords[0]][blockCoords[1]] [blockCoords[2]][blockCoords[3]] = newCell;
            }
        }

        //PrintGrids();
    }

    public void PrintGrids() {
        for(Cell[] row : this.cellsGrid) {
            System.out.println(Arrays.stream(row)
                    .map(Cell :: getSolution)
                    .toList());
        }

        System.out.println();
        /*
        for(int x = 0; x < this.size; ++x){
            System.out.print("[");
            for(int y = 0; y < this.size; ++y) {
                int[] blockCoords = ConvertToBlockCoords(x,y);

                System.out.print(this.cellsGridBlocks[blockCoords[0]][blockCoords[1]] [blockCoords[2]][blockCoords[3]]
                        .getName());

                if(y != this.size - 1) System.out.print(", ");
            }
            System.out.print("]\n");
        }
         */
    }

    public void CreateSolvedGrid() {

        LinkedList<Cell> unfilledCells = new LinkedList<>();
        for(Cell[] cellColumn : this.cellsGrid) unfilledCells.addAll(Arrays.asList(cellColumn));

        //Collections.shuffle(unfilledCells);
        CreateSolvedGrid( 0);
        PrintGrids();


    }


    public boolean CreateSolvedGrid(int ind) {

        // Base case
        // If no unfilled Cells left, grid is solved
        if(ind == this.size*this.size) return true;


        Cell[][] savedGridState = CopyGrid(this.cellsGrid);

        int row = ind / this.size;
        int column = ind % this.size;

        Cell originalCell = this.cellsGrid[row][column];

        for(Integer chosenNumber : originalCell.getPossibleNumbers())
        {
            // Restore saved state
            this.cellsGrid = CopyGrid(savedGridState);
            this.cellsBlocksGrid = ConvertToGridBlocks(this.cellsGrid);

            // Update all Cells's possibleNumbers lists
            Cell chosenCell = this.cellsGrid[originalCell.getRow()][originalCell.getColumn()];
            UpdatePossibleNumbers(chosenCell, chosenNumber);

            // Check next possible solution via effective DFS
            // Returns true if a valid complete grid found
            // Returns false if dead-end reached
            if(CreateSolvedGrid(ind+1)) return true;

            // If dead-end reached -> Try next possible number and restore saved state
        }

        // All possible numbers cause dead-end
        return false;

    }


    public int[] ConvertToBlockCoords(int row, int column) {
        int[] blockCoords = new int[4];

        blockCoords[0] = row / this.blockSize;
        blockCoords[1] = column / this.blockSize;
        blockCoords[2] = row % this.blockSize;
        blockCoords[3] = column % this.blockSize;

        return blockCoords;
    }


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


    public Cell[][] [][] ConvertToGridBlocks(Cell[][] cellsGrid) {

        Cell[][] [][] newCellsBlocksGrid = new Cell[this.blockSize][this.blockSize] [this.blockSize][this.blockSize];

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


    public void UpdatePossibleNumbers(Cell chosenCell, Integer chosenNumber) {

        chosenCell.setSolution(chosenNumber);

        // Get the row, column, and block Cell is in
        int chosenRow = chosenCell.getRow();
        int chosenColumn = chosenCell.getColumn();

        Cell[] cellsRow = this.cellsGrid[chosenRow];
        Cell[] cellsColumn = new Cell[this.size];
        for(int i = 0; i < this.size; ++i) cellsColumn[i] = this.cellsGrid[i][chosenColumn];

        int[] blockCoords = ConvertToBlockCoords(chosenRow, chosenColumn);
        Cell[][] cellsBlock = this.cellsBlocksGrid[blockCoords[0]][blockCoords[1]];


        // Remove assigned number from possible numbers of Cells in same row, column, block
        for(Cell cell : cellsRow) cell.getPossibleNumbers().remove(chosenNumber);
        for(Cell cell : cellsColumn) cell.getPossibleNumbers().remove(chosenNumber);
        for(Cell[] cellsBlockCols : cellsBlock) for(Cell cell : cellsBlockCols) cell.getPossibleNumbers().remove(chosenNumber);

    }

}
