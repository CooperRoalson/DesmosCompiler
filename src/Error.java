public class Error {
    private int line, col;
    private String message;

    public Error(int line, int col) {this(line,col,"");}

    public Error(Token t, String message) {this(t.getSrcLine(),t.getSrcCol(),message);}

    public Error(int line, int col, String message) {
        this.line = line;
        this.col = col;
        this.message = message;
    }

    public String toString() {
        return "Error at line " + line + ": " + message;
    }
}
