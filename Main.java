
public class Main {

    public static void main(String[] args) {

        // TODO - Generate possible puzzle
        int blockSize = 3;
        Grid grid = new Grid(blockSize);

        // TODO - Mark each Cell with possible numbers
            // TODO - Check with separate thread for row/column/box (?)


        // TODO - Check for any Cells with guaranteed number
            // TODO - If only 1 possible number for Cell, populate Cell and update row/column/box
            // TODO - If only 1 number appears in row/column/box, populate cell and update


        // TODO - Once every cell is occupied, find cell with duplicate possible numbers
            // TODO - Pick cell (random or cell with fewest possibilities) and try on different threads
            // TODO - If dead-end found, kill thread
    }
}
