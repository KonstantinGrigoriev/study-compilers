package kg.study.lang.lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Arrays;

public class Lexer {

    private final BufferedReader reader;
    private int row = 0;
    private int col = 0;
    private String line;

    private Lexer(BufferedReader reader) {
        this.reader = reader;
        readNextLine();
    }

    public static Lexer forStream(InputStream stream) {
        return new Lexer(new BufferedReader(new InputStreamReader(stream)));
    }

    public static Lexer forString(String str) {
        return new Lexer(new BufferedReader(new StringReader(str)));
    }

    public Token next() {
        while (true) {
            if (line == null) {
                return Token.EOF;
            }

            if (col == line.length()) {
                readNextLine();
                continue;
            }

            char ch = currentChar();

            if (Character.isSpaceChar(ch)) {
                col++;
                continue;
            }

            if (Arrays.binarySearch(ComplexSymbol.START_SYMBOLS, ch) >= 0) {
                String complex = "" + ch + nextChar();
                if (ComplexSymbol.VALUES.containsKey(complex)) {
                    col++;
                    return ComplexSymbol.VALUES.get(complex);
                } else {
                    col--;
                }
            }

            if (Symbol.VALUES.containsKey(ch)) {
                col++;
                return Symbol.VALUES.get(ch);
            } else if (Character.isDigit(ch)) {
                return numberValue();
            } else if (ch == '"') {
                return stringValue();
            } else if (Character.isLetter(ch)) {
                return identifierOrKeyword();
            } else {
                throw new LexerException("Unexpected character : " + ch, row, col);
            }
        }
    }

    private Token identifierOrKeyword() {
        StringBuilder sb = new StringBuilder();
        char ch = currentChar();
        while (Character.isLetterOrDigit(ch)) {
            sb.append(ch);
            ch = nextChar();
        }
        String word = sb.toString();
        if (Keyword.VALUES.containsKey(word)) {
            return Keyword.VALUES.get(word);
        } else {
            return new Identifier(word);
        }
    }

    private Token numberValue() {
        Token result;
        Number value;
        StringBuilder sb = new StringBuilder();
        char ch = currentChar();
        while (Character.isDigit(ch)) {
            sb.append(ch);
            ch = nextChar();
        }
        if (ch == '.') {
            sb.append(ch);
            ch = nextChar();
            while (Character.isDigit(ch)) {
                sb.append(ch);
                ch = nextChar();
            }
            value = Float.valueOf(sb.toString());
        } else {
            value = Integer.valueOf(sb.toString());
        }
        if (Character.isLetter(ch)) {
            throw new LexerException("Unexpected character in number literal : " + ch, row, col);
        }
        result = new Value(value);
        return result;
    }

    private Token stringValue() {
        StringBuilder sb = new StringBuilder();
        char ch = nextChar();
        while (ch != '"') {
            sb.append(ch);
            ch = nextChar();
            if (col == line.length()) {
                throw new LexerException("Illegal line end in string literal", row, col);
            }
        }
        col++;
        return new Value(sb.toString());
    }

    private char nextChar() {
        col++;
        return currentChar();
    }

    private char currentChar() {
        if (col < line.length()) {
            return line.charAt(col);
        } else {
            return '\n';
        }
    }

    private void readNextLine() {
        try {
            line = reader.readLine();
        } catch (IOException e) {
            throw new LexerException(e, row, col);
        }
        if (line != null) {
            row++;
        }
        col = 0;
    }
}
