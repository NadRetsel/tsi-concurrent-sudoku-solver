import java.util.LinkedList;
import java.util.concurrent.RecursiveTask;

public class SolveGrid extends RecursiveTask<Boolean> {

    private String threadId;
    private Grid grid;
    private Integer chosenNumber;
    private LinkedList<Integer> remainingIndexs;
    private Object found;

    public SolveGrid(String threadId, Grid grid, Integer chosenNumber, LinkedList<Integer> remainingIndexs, Object found) {
        this.threadId = threadId;

        this.grid = grid;
        this.chosenNumber =  chosenNumber;
        this.remainingIndexs = remainingIndexs;

        this.found = found;
    }


    @Override
    public Boolean compute() {
        // System.out.println(this.threadId);

        if(this.remainingIndexs.isEmpty() || this.found == null) {
            this.found = null;

            System.out.println("SOLUTION FOUND" + this.threadId + "\n" + this.grid.PrintBlockGrid() + "\n");
            return true;
        }


        int[] coords = this.grid.ConvertToCoords(this.remainingIndexs.getFirst());
        Cell cell = this.grid.getCellsGrid()[coords[0]][coords[1]];
        this.grid.RemovePossibleNumbers(cell, this.chosenNumber);

        this.remainingIndexs.removeFirst();
        if(this.remainingIndexs.isEmpty()) {
            this.found = null;

            System.out.println("SOLUTION FOUND " + this.threadId + "\n" + this.grid.PrintBlockGrid() + "\n");
            return true;
        }


        LinkedList<SolveGrid> possibleSolutions = GetPossibleSolutions();
        if(possibleSolutions.isEmpty()) return false;

        SolveGrid hold = possibleSolutions.getFirst();
        possibleSolutions.removeFirst();

        for (SolveGrid sg :possibleSolutions) sg.fork();
        if(hold.compute()) return true;
        for (SolveGrid sg :possibleSolutions) if(sg.join()) return true;


        return false;
    }


    public LinkedList<SolveGrid> GetPossibleSolutions() {
        LinkedList<SolveGrid> possibleSolutions = new LinkedList<>();

        int[] coords = this.grid.ConvertToCoords(this.remainingIndexs.getFirst());
        Cell cell = this.grid.getCellsGrid()[coords[0]][coords[1]];


        int threadCount = 0;
        for(Integer chosenNumber : new LinkedList<>(cell.getPossibleNumbers()))
        {
            SolveGrid sg = new SolveGrid(this.threadId + " " + ++threadCount, new Grid(this.grid), chosenNumber, new LinkedList<>(this.remainingIndexs), this.found);
            possibleSolutions.add(sg);
        }

        return possibleSolutions;
    }
}
