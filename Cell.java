import java.util.LinkedList;
import java.util.Random;

public class Cell {

    private String name;

    private Random random = new Random();
    private Integer solution;
    private Integer selectedNumber;

    private final int row;
    private final int column;

    private final LinkedList<Integer> possibleNumbers;

    public Cell(int size, int row, int column) {
        this.possibleNumbers = FillPossibleNumbers(size);
        this.row = row;
        this.column = column;

        this.name = row + " " + column;
    }

    public Cell(Cell cell) {
        this.row = cell.getRow();
        this.column = cell.getColumn();

        this.name = cell.getName();
        this.solution = (cell.getSolution());
        this.selectedNumber = (cell.getSelectedNumber());
        this.possibleNumbers = new LinkedList<>(cell.getPossibleNumbers());

    }


    public String getName() {
        return name;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Integer getSolution() {
        return solution;
    }

    public Integer getSelectedNumber() {
        return selectedNumber;
    }

    public LinkedList<Integer> getPossibleNumbers() {
        return possibleNumbers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSolution(Integer solution) {
        this.solution = solution;
    }

    public void setSelectedNumber(Integer selectedNumber) {
        this.selectedNumber = selectedNumber;
    }

    public LinkedList<Integer> FillPossibleNumbers(int size) {

        LinkedList<Integer> newPossible = new LinkedList<>();

        for(int i = 0; i < size; ++i) newPossible.add(i);

        return newPossible;
    }

    public Integer AssignRandomly() {
        this.solution = this.possibleNumbers
                .get(this.random.nextInt(this.possibleNumbers.size()));

        this.possibleNumbers.clear();

        return this.solution;
    }



}
