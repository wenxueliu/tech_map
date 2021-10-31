package com.wexueliu.bizhidao.auth;/**
* @author liuwenxue
* @date 2021-10-31
*/
public class Token {
    TokenKind kind;

    String data;

    // index of first character
    int startPos;

    // index of char after the last character
    int endPos;


    /**
     * Constructor for use when there is no particular data for the token
     * (e.g. TRUE or '+')
     * @param startPos the exact start
     * @param endPos the index to the last character
     */
    Token(TokenKind tokenKind, int startPos, int endPos) {
        this.kind = tokenKind;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    Token(TokenKind tokenKind, char[] tokenData, int startPos, int endPos) {
        this(tokenKind, startPos, endPos);
        this.data = new String(tokenData);
    }


    public TokenKind getKind() {
        return this.kind;
    }

    public boolean isIdentifier() {
        return (this.kind == TokenKind.IDENTIFIER);
    }

    public String stringValue() {
        return (this.data != null ? this.data : "");
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("[").append(this.kind.toString());
        if (this.kind.hasPayload()) {
            s.append(":").append(this.data);
        }
        s.append("]");
        s.append("(").append(this.startPos).append(",").append(this.endPos).append(")");
        return s.toString();
    }
}
