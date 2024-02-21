import java.util.*;

public class Cell {

    private final Random random = new Random();

    private String name;
    private Integer index;
    private Integer solution;
    private Integer selectedNumber;
    private boolean visible;

    private final int row;
    private final int column;

    private final HashSet<Integer> possibleNumbers;


    public Cell(int size, int index, int row, int column) {
        this.possibleNumbers = FillPossibleNumbers(size);

        this.index = index;
        this.row = row;
        this.column = column;

        this.name = row + " " + column;
        this.visible = true;
    }

    // Copy constructor
    public Cell(Cell cell) {
        this.index = cell.getIndex();
        this.row = cell.getRow();
        this.column = cell.getColumn();

        this.name = cell.getName();
        this.solution = cell.getSolution();
        this.selectedNumber = cell.getSelectedNumber();
        this.visible = cell.isVisible();

        this.possibleNumbers = new HashSet<>(cell.getPossibleNumbers());
    }


    public String getName() {
        return name;
    }

    public Integer getIndex() {
        return index;
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

    public Set<Integer> getPossibleNumbers() {
        return possibleNumbers;
    }

    public boolean isVisible() {
        return visible;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setSolution(Integer solution) {
        this.solution = solution;
        this.possibleNumbers.clear();
    }

    public void setSelectedNumber(Integer selectedNumber) {
        this.selectedNumber = selectedNumber;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }


    public HashSet<Integer> FillPossibleNumbers(int size) {

        HashSet<Integer> newPossible = new HashSet<>();

        for(int i = 0; i < size; ++i) newPossible.add(i);

        return newPossible;
    }

}
