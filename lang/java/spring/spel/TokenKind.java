package com.wexueliu.bizhidao.auth;

/**
 * @author liuwenxue
 * @date 2021-10-31
 */
public enum TokenKind {
    // ordered by priority - operands first

    LITERAL_STRING,

    LPAREN("("),

    RPAREN(")"),

    IDENTIFIER,

    COLON(":"),

    DOT("."),

    NOT("NOT"),

    AND("AND"),

    OR("OR");

    final char[] tokenChars;

    private final boolean hasPayload;  // is there more to this token than simply the kind


    private TokenKind(String tokenString) {
        this.tokenChars = tokenString.toCharArray();
        this.hasPayload = (this.tokenChars.length == 0);
    }

    private TokenKind() {
        this("");
    }


    @Override
    public String toString() {
        return (name() + (this.tokenChars.length != 0 ? "(" + new String(this.tokenChars) + ")" : ""));
    }

    public boolean hasPayload() {
        return this.hasPayload;
    }

    public int getLength() {
        return this.tokenChars.length;
    }
}
