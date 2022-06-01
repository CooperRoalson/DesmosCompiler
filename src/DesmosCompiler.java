import java.io.*;
import java.util.*;

public class DesmosCompiler {
    private static final int TAB_WIDTH = 4;

    public static void main(String[] args) throws IOException {
        System.out.print("Input: ");
        Scanner scan = new Scanner(System.in);
        String inputStr = scan.nextLine()+"\n";
        List<Error> errors = new ArrayList<>();
        System.out.println(tokenize(new StringReader(inputStr),errors));
        System.out.println(errors);
    }

    public static List<List<Token>> tokenize(Reader inputReader, List<Error> errors) throws IOException {
        PushbackReader pbReader = new PushbackReader(inputReader,Token.MAX_SYMBOL_LEN);

        List<List<Token>> tokens = new ArrayList<>();
        List<Token> curTokenList = new ArrayList<>();
        tokens.add(curTokenList);

        int line = 0, col = 0;
        int c;
        String str;

        StringBuilder invalidSymbolStr = new StringBuilder();
        int invalidSymbolLine = 0, invalidSymbolCol = 0;
        boolean invalidSymbolFlag;

        while ((c = pbReader.read()) != -1) {
            invalidSymbolFlag = false;
            col++;
            str = "" + (char)c;
            if (str.equals("/")) {
                c = pbReader.read();
                if ((char)c == '/') {
                    col += readWhileMatches(pbReader,".*[^\\n]","").length() + 1;
                    continue;
                } else if (c != -1) {pbReader.unread(c);}
            }
            if (str.matches("[a-zA-Z]")) {
                str = readWhileMatches(pbReader,"[a-zA-Z][a-zA-Z\\d]*",str);
                curTokenList.add(new Token(Token.KEYWORDS.contains(str) ? Token.TokenType.KEYWORD : Token.TokenType.IDENTIFIER, str,line,col));
                col += str.length()-1;
            } else if (str.matches("\\d")) {
                str = readWhileMatches(pbReader,"\\d+(\\.\\d+)?",str);
                try {Double.parseDouble(str); curTokenList.add(new Token(Token.TokenType.NUMBER,str,line,col));}
                catch (NumberFormatException e) {
                    errors.add(new Error(line, col, "Invalid number ('" + str + "')"));
                }
                col += str.length()-1;
            } else if (str.matches("\\S")) {
                str = readWhileMatches(pbReader,Token.SYMBOL_REGEX,str);
                if (Token.SYMBOLS.contains(str)) {
                    curTokenList.add(new Token(Token.TokenType.SYMBOL, str, line, col));
                } else {
                    for (int i = 0; i < str.length()-1; i++) {pbReader.unread(str.charAt(str.length() - 1));}
                    invalidSymbolFlag = true;
                    if (invalidSymbolStr.length() == 0) {
                        invalidSymbolLine = line;
                        invalidSymbolCol = col;
                    }
                    invalidSymbolStr.append(str);
                }
                col += str.length()-1;
            } else if (str.equals("\t")) {
                col += TAB_WIDTH-1;
            } else if (str.equals("\n")) {
                curTokenList = new ArrayList<>();
                tokens.add(curTokenList);
                line++;
                col = 0;
            }

            if (invalidSymbolStr.length() > 0 && !invalidSymbolFlag) {
                errors.add(new Error(invalidSymbolLine,invalidSymbolCol,"Invalid symbol ('"+invalidSymbolStr+"')"));
                invalidSymbolStr = new StringBuilder();
            }
        }
        if (invalidSymbolStr.length() > 0) {errors.add(new Error(invalidSymbolLine,invalidSymbolCol,"Invalid symbol ('"+invalidSymbolStr+"')"));}

        return tokens;
    }

    public static String readWhileMatches(PushbackReader input, String regex, String start) throws IOException {
        StringBuilder sb = new StringBuilder(start);
        int i;
        String s;
        while ((i = input.read()) != -1) {
            s = ""+(char)i;
            if (!(sb+s).matches(regex)) {
                input.unread(i);
                break;
            }
            sb.append(s);
        }
        return sb.toString();
    }

    public static ParseData treeify(List<List<Token>> tokens, List<Error> errors) {
        ParseData data = new ParseData();

        int idt,idx;
        Stack<ParseData.Structure> context = new Stack<>();

        for (List<Token> l : tokens) {
            idt = l.get(0).getSrcCol() - 1;
            if (idt % TAB_WIDTH != 0) {errors.add(new Error(l.get(0).getSrcLine(),0,"Improper indent amount"));}
            idt /= TAB_WIDTH;

            if (-1 != (idx = indexOfSymbol(new String[]{"=",":=","+=","-=","*=","/=","%="},l))) {
                String op = l.get(idx).getData();

                Expression target = treeifyExpression(l.subList(0,idx),data,errors);
                Expression assignment = treeifyExpression(l.subList(idx+1,l.size()),data,errors);
                if (!op.equals("=") && !op.equals(":=")) {
                    Token expandedOp = new Token(Token.TokenType.SYMBOL,op.substring(0,op.length()-1),l.get(idx).getSrcLine(),l.get(idx).getSrcCol());
                    assignment = new Expression(Expression.ExpressionType.OPERATOR,new Expression[]{target,assignment},expandedOp);
                }
                data.assignTo(target, assignment, !op.equals("="), context,errors);
            }

            treeifyExpression(l.subList(),errors));
        }


        return new ArrayList<>();
    }

    public static Expression treeifyExpression(List<Token> tokens, ParseData data, List<Error> errors) {



    }

    public static int indexOfSymbol(String[] symbols, List<Token> list) {
        Optional<Token> tok = list.stream().filter(t->(t.getType()==Token.TokenType.SYMBOL && symbols.contains(t.getData()))).findFirst();
        if (tok.isEmpty()) {return -1;}
        return list.indexOf(tok.get());
    }

    public static String generate(List<Expression> expressionList) {
        return "";
    }

    public static void compile(File f) {

    }

}
