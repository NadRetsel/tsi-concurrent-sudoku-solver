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
        System.out.println(ValidateGrid());

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


    public void CreateSolvedGrid() {

        LinkedList<Integer> unfilledCellsIndexs = new LinkedList<>();
        while(unfilledCellsIndexs.size() < this.size*this.size) unfilledCellsIndexs.add(unfilledCellsIndexs.size());

        Collections.shuffle(unfilledCellsIndexs);
        CreateSolvedGrid(unfilledCellsIndexs);
        PrintBlockGrid();
    }


    public boolean CreateSolvedGrid(LinkedList<Integer> unfilledCellsIndexs) {

        // Base case
        // If no unfilled Cells left, grid is solved
        if(unfilledCellsIndexs.isEmpty()) return true;


        // Create a saved state copy of the grid
        Cell[][] savedGridState = CopyGrid(this.cellsGrid);


        // Get the Cell referred by the list and remove from the list
        int chosenCellIndex = unfilledCellsIndexs.getFirst();
        int chosenCellRow = chosenCellIndex / this.size;
        int chosenCellColumn = chosenCellIndex % this.size;
        Cell chosenCell = this.cellsGrid[chosenCellRow][chosenCellColumn];


        // Using number of possible numbers left as heuristic, select Cell with the fewest left
        for(Integer cellIndex : unfilledCellsIndexs) {
            int cellRow = cellIndex / this.size;
            int cellColumn = cellIndex % this.size;
            Cell cell = this.cellsGrid[cellRow][cellColumn];

            if(cell.getPossibleNumbers().size() < chosenCell.getPossibleNumbers().size()) {
                chosenCellIndex = cellIndex;
                chosenCellRow = chosenCellIndex / this.size;
                chosenCellColumn = chosenCellIndex % this.size;
                chosenCell = cell;
            }
        }


        LinkedList<Integer> updatedUnfilledCellsIndex  = new LinkedList<>(unfilledCellsIndexs);
        updatedUnfilledCellsIndex.removeAll(List.of(chosenCellIndex));

        for(Integer chosenNumber : chosenCell.getPossibleNumbers())
        {
            // Restore saved state
            this.cellsGrid = CopyGrid(savedGridState);
            this.cellsBlocksGrid = ConvertToGridBlocks(this.cellsGrid);

            // Update all Cells's possibleNumbers lists
            Cell chosenCellCopy = this.cellsGrid[chosenCellRow][chosenCellColumn];
            UpdatePossibleNumbers(chosenCellCopy, chosenNumber);

            // Check next possible solution via effectively DFS
            // Returns true if a valid complete grid found
            // Returns false if dead-end was reached during search -> Backtrack
            if(CreateSolvedGrid(updatedUnfilledCellsIndex)) return true;

            // If dead-end reached -> Try next possible number and restore saved state
        }

        // All possible numbers cause dead-end
        return false;

    }


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
                                                        .toArray(new Integer[this.blockSize])
                                                )
                                                .toList()
                                                .toArray(new Integer[this.blockSize][]))
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

        // Update chosen Cell
        chosenCell.setSolution(chosenNumber);


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


    public void PrintGrid() {
        for(Cell[] row : this.cellsGrid) {
            System.out.println(Arrays.stream(row)
                    .map(Cell::getSolution)
                    .toList());
        }
    }

    public void PrintBlockGrid() {

        for(int x = 0; x < this.size; ++x)
        {
            System.out.print("[");
            for(int y = 0; y < this.size; ++y)
            {
                int[] blockCoords = ConvertToBlockCoords(x,y);

                System.out.print(this.cellsBlocksGrid[blockCoords[0]][blockCoords[1]] [blockCoords[2]][blockCoords[3]]
                        .getSolution());

                if(y != this.size - 1) System.out.print( ((y+1) % this.blockSize == 0) ? " \t " : " | ");

            }
            System.out.print("]\n");
            if( (x+1) % this.blockSize == 0 ) System.out.println();
        }

    }


}
