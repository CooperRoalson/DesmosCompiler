import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Token {
    public enum TokenType {
        SYMBOL,
        KEYWORD,
        IDENTIFIER,
        NUMBER
    }

    public static final List<String> SYMBOLS = Arrays.asList(
            "(",")","[","]","{","}",",",
            "~","&","^","|",
            "+","-","*","/","%",
            "!","||",
            "=",":=","+=","-=","*=","/=","%=",
            "==",">=","<=",">","<","!=",
            "?",":",
            "."
    );

    public static final int MAX_SYMBOL_LEN = SYMBOLS.stream().mapToInt(String::length).max().getAsInt();
    public static final String SYMBOL_REGEX = "("+SYMBOLS.stream().map(s->new AbstractMap.SimpleEntry<>(s,s.length())).map(e->("\\Q"+e.getKey().replaceAll("(?<=.)(?=.)","\\\\E(\\\\Q")+"\\E"+")?".repeat(e.getValue()-1))).collect(Collectors.joining("|"))+")";
    public static final List<String> KEYWORDS = Arrays.asList("def","action","struct","shape","draw","solve","static","constructor","operator");

    private String data;
    private TokenType type;
    private int srcLine;
    private int srcCol;

    public Token(TokenType type, String data, int srcLine, int srcCol) {
        this.data = data;
        this.type = type;
        this.srcLine = srcLine;
        this.srcCol = srcCol;
    }

    public TokenType getType() {
        return type;
    }
    public String getData() {
        return data;
    }

    public int getSrcLine() {
        return srcLine;
    }

    public int getSrcCol() {
        return srcCol;
    }

    @Override
    public String toString() {
        return type.name() + "('"+data+"')"; //+ " "+srcCol;
    }

}
