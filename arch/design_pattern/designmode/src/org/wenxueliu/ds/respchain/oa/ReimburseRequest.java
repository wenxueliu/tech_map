package org.wenxueliu.ds.respchain.oa;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class ReimburseRequest extends Request {
    private Level level;
    private String content;

    ReimburseRequest(Level level, String content) {
        this.level = level;
        this.content = content;
    }

    @Override
    public Level getLevel() {
        return this.level;
    }

    @Override
    public Type getType() {
        return Type.REIMBURSE;
    }

    @Override
    public String getContent() {
        return content;
    }

}
