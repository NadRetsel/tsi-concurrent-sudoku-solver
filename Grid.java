import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Grid {

    private int size;
    private int blockSize;

    private Cell[][] cellsGrid;
    private Cell[][][][] cellsGridBlocks;

    public Grid(int blockSize) {
        this.size = blockSize * blockSize;
        this.blockSize = blockSize;

        this.cellsGrid = new Cell[this.size][this.size];
        this.cellsGridBlocks = new Cell[blockSize][blockSize] [blockSize][blockSize];

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
                this.cellsGridBlocks[blockCoords[0]][blockCoords[1]] [blockCoords[2]][blockCoords[3]] = newCell;
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
        RecursivelyCreateSolvedGrid( 0);
        PrintGrids();


    }


    public boolean RecursivelyCreateSolvedGrid(int ind) {

        Cell[][] gridCopy =
                Arrays.stream(this.cellsGrid)
                        .map((Cell[] cellRow) -> Arrays.stream(cellRow)
                                .map(Cell::new)
                                .toList()
                                .toArray(new Cell[this.size])
                        )
                        .toList()
                        .toArray(new Cell[this.size][]);


        for(int row = 0; row < this.size; ++row)
        {
            for(int column = 0; column < this.size; ++column)
            {
                int[] blockCoords = ConvertToBlockCoords(row, column);

                Cell newCell = gridCopy[row][column];
                this.cellsGridBlocks[blockCoords[0]][blockCoords[1]] [blockCoords[2]][blockCoords[3]] = newCell;
            }
        }





        // If no unfilled Cells left, grid is solved
        if(ind == this.size*this.size) return true;

        int row = ind / this.size;
        int column = ind % this.size;

        Cell originalCell = this.cellsGrid[row][column];

        for(Integer chosenNumber : originalCell.getPossibleNumbers())
        {
            PrintGrids();
            System.out.println(originalCell.getName());


            this.cellsGrid = Arrays.stream(gridCopy)
                    .map((Cell[] cellRow) -> Arrays.stream(cellRow)
                            .map(Cell::new)
                            .toList()
                            .toArray(new Cell[this.size])
                    )
                    .toList()
                    .toArray(new Cell[this.size][]);

            for(int newRow = 0; newRow < this.size; ++newRow)
            {
                for(int newColumn = 0; newColumn < this.size; ++newColumn)
                {
                    int[] blockCoords = ConvertToBlockCoords(newRow, newColumn);

                    Cell newCell = this.cellsGrid[newRow][newColumn];
                    this.cellsGridBlocks[blockCoords[0]][blockCoords[1]] [blockCoords[2]][blockCoords[3]] = newCell;
                }
            }

            Cell chosenCell = this.cellsGrid[originalCell.getRow()][originalCell.getColumn()];

            chosenCell.setSolution(chosenNumber);

            // Get the row, column, and block Cell is in
            int chosenRow = chosenCell.getRow();
            int chosenColumn = chosenCell.getColumn();

            Cell[] cellsRow = this.cellsGrid[chosenRow];
            Cell[] cellsColumn = new Cell[this.size];
            for(int i = 0; i < this.size; ++i) cellsColumn[i] = this.cellsGrid[i][chosenColumn];

            int[] blockCoords = ConvertToBlockCoords(chosenRow, chosenColumn);
            Cell[][] cellsBlock = this.cellsGridBlocks[blockCoords[0]][blockCoords[1]];


            // Remove assigned number from possible numbers of Cells in same row, column, block
            for(Cell cell : cellsRow) cell.getPossibleNumbers().remove(chosenNumber);
            for(Cell cell : cellsColumn) cell.getPossibleNumbers().remove(chosenNumber);
            for(Cell[] cellsBlockCols : cellsBlock) for(Cell cell : cellsBlockCols) cell.getPossibleNumbers().remove(chosenNumber);


            if(RecursivelyCreateSolvedGrid(ind+1)) return true;
        }

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

}
