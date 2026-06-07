package Sunday_CC_Week_43_JigsawPuzzleGenerator;

public class PuzzlePiece {
    private final int id;
    private final int row;
    private final int col;
    private final int targetX;
    private final int targetY;
    private final String base64Image;

    public PuzzlePiece(int id, int row, int col, int targetX, int targetY, String base64Image) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.targetX = targetX;
        this.targetY = targetY;
        this.base64Image = base64Image;
    }

    public int getId() { return id; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getTargetX() { return targetX; }
    public int getTargetY() { return targetY; }
    public String getBase64Image() { return base64Image; }
}