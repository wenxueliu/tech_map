package com.wexueliu.bizhidao.auth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* @author liuwenxue
* @date 2021-10-31
*/
class Tokenizer {
    // If this gets changed, it must remain sorted...
    private static final String[] ALTERNATIVE_OPERATOR_NAMES =
            {"AND", "OR", "NOT"};

    private static final byte[] FLAGS = new byte[256];

    private static final byte IS_DIGIT = 0x01;

    private static final byte IS_HEXDIGIT = 0x02;

    private static final byte IS_ALPHA = 0x04;

    static {
        for (int ch = '0'; ch <= '9'; ch++) {
            FLAGS[ch] |= IS_DIGIT | IS_HEXDIGIT;
        }
        for (int ch = 'A'; ch <= 'F'; ch++) {
            FLAGS[ch] |= IS_HEXDIGIT;
        }
        for (int ch = 'a'; ch <= 'f'; ch++) {
            FLAGS[ch] |= IS_HEXDIGIT;
        }
        for (int ch = 'A'; ch <= 'Z'; ch++) {
            FLAGS[ch] |= IS_ALPHA;
        }
        for (int ch = 'a'; ch <= 'z'; ch++) {
            FLAGS[ch] |= IS_ALPHA;
        }
    }


    private String expressionString;

    private char[] charsToProcess;

    private int pos;

    private int max;

    private List<Token> tokens = new ArrayList<>();


    public Tokenizer(String inputData) {
        this.expressionString = inputData;
        this.charsToProcess = (inputData + "\0").toCharArray();
        this.max = this.charsToProcess.length;
        this.pos = 0;
    }


    public List<Token> process() {
        while (this.pos < this.max) {
            char ch = this.charsToProcess[this.pos];
            if (isIdentifier(ch)) {
                lexIdentifier();
            }
            else {
                switch (ch) {
                    case '_': // the other way to start an identifier
                        lexIdentifier();
                        break;
                    case '.':
                        pushCharToken(TokenKind.DOT);
                        break;
                    case '(':
                        pushCharToken(TokenKind.LPAREN);
                        break;
                    case ')':
                        pushCharToken(TokenKind.RPAREN);
                        break;
                    case ' ':
                    case '\t':
                    case '\r':
                    case '\n':
                        // drift over white space
                        this.pos++;
                        break;
                    case 0:
                        // hit sentinel at end of value
                        this.pos++;  // will take us to the end
                        break;
                    default:
                        throw new IllegalStateException("Cannot handle (" + (int) ch + ") '" + ch + "'");
                }
            }
        }
        return this.tokens;
    }

    private void lexIdentifier() {
        int start = this.pos;
        do {
            this.pos++;
        }
        while (isIdentifier(this.charsToProcess[this.pos]));
        char[] subarray = subarray(start, this.pos);

        // Check if this is the alternative (textual) representation of an operator (see
        // alternativeOperatorNames)
        if ((this.pos - start) == 2 || (this.pos - start) == 3) {
            String asString = new String(subarray).toUpperCase();
            int idx = Arrays.binarySearch(ALTERNATIVE_OPERATOR_NAMES, asString);
            if (idx >= 0) {
                pushOneCharOrTwoCharToken(TokenKind.valueOf(asString), start, subarray);
                return;
            }
        }
        this.tokens.add(new Token(TokenKind.IDENTIFIER, subarray, start, this.pos));
    }

    private char[] subarray(int start, int end) {
        return Arrays.copyOfRange(this.charsToProcess, start, end);
    }

    /**
     * Check if this might be a two character token.
     */
    private boolean isTwoCharToken(TokenKind kind) {
        return (kind.tokenChars.length == 2 &&
                this.charsToProcess[this.pos] == kind.tokenChars[0] &&
                this.charsToProcess[this.pos + 1] == kind.tokenChars[1]);
    }

    /**
     * Push a token of just one character in length.
     */
    private void pushCharToken(TokenKind kind) {
        this.tokens.add(new Token(kind, this.pos, this.pos + 1));
        this.pos++;
    }

    private void pushOneCharOrTwoCharToken(TokenKind kind, int pos, char[] data) {
        this.tokens.add(new Token(kind, data, pos, pos + kind.getLength()));
    }

    // ID: ('a'..'z'|'A'..'Z'|'_'|'$') ('a'..'z'|'A'..'Z'|'_'|'$'|'0'..'9'|DOT_ESCAPED)*;
    private boolean isIdentifier(char ch) {
        return isAlphabetic(ch) || isDigit(ch) || ch == '_' || ch == ':';
    }

    private boolean isColon(char ch) {
        return ch == ':';
    }

    private boolean isChar(char a, char b) {
        char ch = this.charsToProcess[this.pos];
        return ch == a || ch == b;
    }

    private boolean isDigit(char ch) {
        if (ch > 255) {
            return false;
        }
        return (FLAGS[ch] & IS_DIGIT) != 0;
    }

    private boolean isAlphabetic(char ch) {
        if (ch > 255) {
            return false;
        }
        return (FLAGS[ch] & IS_ALPHA) != 0;
    }

    static void test1() {
        String expression = "role:develop AND group:dev OR group:test AND NOT role:system";
        Tokenizer tokenizer = new Tokenizer(expression);
        List<Token> tokens = tokenizer.process();
        tokens.forEach(System.out::println);
    }

    static void test2() {
        String expression = "role:develop AND ( group:dev OR group:test ) AND NOT role:system";
        Tokenizer tokenizer2 = new Tokenizer(expression);
        List<Token> tokens2 = tokenizer2.process();
        tokens2.forEach(System.out::println);
    }

    public static void main(String[] args) {
        test2();
    }
}
