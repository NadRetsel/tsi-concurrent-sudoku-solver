import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class Solver {

    private Grid grid;

    public Solver(Grid grid) {
        this.grid = grid;
    }

    public void solve() {

        LinkedList<Integer> unfilledIndexs = this.grid.CreateUnfilledIndexsList();
        int[] coords = this.grid.ConvertToCoords(unfilledIndexs.getFirst());
        Cell cell = this.grid.getCellsGrid()[coords[0]][coords[1]];

        LinkedList<SolveGrid> possibleSolutions = new LinkedList<>();

        Object found = new Object();
        int threadCount = 0;
        for(Integer chosenNumber : new LinkedList<>(cell.getPossibleNumbers()))
        {
            possibleSolutions.add(new SolveGrid(++threadCount + " ", new Grid(this.grid), chosenNumber, new LinkedList<>(unfilledIndexs), found));
        }

        for (SolveGrid sg : possibleSolutions) sg.fork();
        for (SolveGrid sg : possibleSolutions) sg.join();


        /*
        this.grid.SolveGrid(this.grid.CreateUnfilledIndexsList(), 1);
        System.out.println(this.grid.PrintBlockGrid());
         */


    }






}
