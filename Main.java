
public class Main {

    public static void main(String[] args) {

        int blockSize = 3;
        Grid grid = new Grid(2,2);
        grid.CreateGrid();

        /*
        Integer[] setGrid = {
                1, null, null,      null, 2, null,      3, null, 4,
                null, 4, null,      5, 6, null,         7, 8, null,
                null, 0, null,      null, 8, null,      null, null, 2,

                null, null, 3,      null, null, 8,      null, null, 7,
                null, null, 7,      null, null, null,   6, null, null,
                8, null, null,      2, null, null,      0, null, null,

                6, null, null,      null, 1, null,      null, 3, null,
                null, 5, 8,         null, 9, 3,         null, 7, null,
                2, null, 1,         null, 4, null,      null, null, 6

        };

        grid.CreateGrid(setGrid);
        */

        Solver solver = new Solver(grid);
        solver.solve();


    }
}
