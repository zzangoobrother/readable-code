package cleancode.minesweeper.tobe;

import cleancode.minesweeper.tobe.cell.Cell;
import cleancode.minesweeper.tobe.cell.EmptyCell;
import cleancode.minesweeper.tobe.cell.LandMineCell;
import cleancode.minesweeper.tobe.cell.NumberCell;
import cleancode.minesweeper.tobe.gamelevel.GameLevel;
import cleancode.minesweeper.tobe.position.CellPosition;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static cleancode.minesweeper.tobe.position.RelativePosition.SURROUNDED_POSITIONS;

public class GameBoard {

    private final Cell[][] board;
    private final int landMineCount;

    public GameBoard(GameLevel gameLevel) {
        int rowSize = gameLevel.getRowSize();
        int colSize = gameLevel.getColSize();
        board = new Cell[rowSize][colSize];
        landMineCount = gameLevel.getLandMineCount();
    }

    public int getRowSize() {
        return board.length;
    }

    public int getColSize() {
        return board[0].length;
    }

    public String getSign(CellPosition cellPosition) {
        return findCell(cellPosition).getSign();
    }

    public void flagAt(CellPosition cellPosition) {
        Cell cell = findCell(cellPosition);
        cell.flag();
    }

    public boolean isLandMineCellAt(CellPosition cellPosition) {
        return findCell(cellPosition).isLandMine();
    }

    public boolean isAllCellChecked() {
        return Arrays.stream(board)
                .flatMap(Arrays::stream)
                .allMatch(Cell::isChecked);
    }

    public void openAt(CellPosition cellPosition) {
        Cell cell = board[cellPosition.getRowIndex()][cellPosition.getColIndex()];
        cell.open();
    }

    public void openSurroundedCells(CellPosition cellPosition) {
        if (isOpenedCell(cellPosition)) {
            return;
        }
        if (isLandMineCellAt(cellPosition)) {
            return;
        }

        openAt(cellPosition);

        if (doesCellHaveLandMineCount(cellPosition)) {
            return;
        }

        calculateSurroundedPositions(cellPosition, getRowSize(), getColSize())
                .forEach(this::openSurroundedCells);
    }

    private boolean isOpenedCell(CellPosition cellPosition) {
        return findCell(cellPosition).isOpened();
    }

    private boolean doesCellHaveLandMineCount(CellPosition cellPosition) {
        return findCell(cellPosition).hasLandMineCount();
    }

    public void initializeGame() {
        int rowSize = getRowSize();
        int colSize = getColSize();

        for (int row = 0; row < rowSize; row++) {
            for (int col = 0; col < colSize; col++) {
                board[row][col] = new EmptyCell();
            }
        }

        for (int i = 0; i < landMineCount; i++) {
            int landMineCol = new Random().nextInt(colSize);
            int landMineRow = new Random().nextInt(rowSize);
            board[landMineRow][landMineCol] = new LandMineCell();
        }

        for (int row = 0; row < rowSize; row++) {
            for (int col = 0; col < colSize; col++) {
                CellPosition cellPosition = CellPosition.of(row, col);
                if (isLandMineCellAt(cellPosition)) {
                    continue;
                }

                int count = countNearbyLandMines(cellPosition);
                if (count == 0) {
                    continue;
                }
                board[row][col] = new NumberCell(count);
            }
        }
    }

    public boolean isInvalidCellPosition(CellPosition cellposition) {
        int rowSize = getRowSize();
        int colSize = getColSize();

        return cellposition.isRowIndexMoreThanOrEqual(rowSize) || cellposition.isColIndexMoreThanOrEqual(colSize);
    }

    private int countNearbyLandMines(CellPosition cellPosition) {
        int rowSize = getRowSize();
        int colSize = getColSize();

        return (int) calculateSurroundedPositions(cellPosition, rowSize, colSize).stream()
                .filter(this::isLandMineCellAt)
                .count();
    }

    private static List<CellPosition> calculateSurroundedPositions(CellPosition cellPosition, int rowSize, int colSize) {
        return SURROUNDED_POSITIONS.stream()
                .filter(cellPosition::canCalculatePositionBy)
                .map(cellPosition::calculatePositionBy)
                .filter(position -> position.isRowIndexLessThan(rowSize))
                .filter(position -> position.isColIndexLessThan(colSize))
                .toList();
    }

    private Cell findCell(CellPosition cellPosition) {
        return board[cellPosition.getRowIndex()][cellPosition.getColIndex()];
    }
}
