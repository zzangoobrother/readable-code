package cleancode.minesweeper.tobe.cell;

public interface Cell {

    final String FLAG_SIGN = "⚑";

    final String UNCHECKED_SIGN = "□";


    boolean isLandMine();

    boolean hasLandMineCount();

    String getSign();

    void flag();

    boolean isChecked();

    void open();

    boolean isOpened();
}
