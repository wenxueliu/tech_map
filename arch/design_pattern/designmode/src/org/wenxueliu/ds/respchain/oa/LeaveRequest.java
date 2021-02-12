package org.wenxueliu.ds.respchain.oa;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class LeaveRequest extends Request {
    private Level level;
    private String content;

    LeaveRequest(Level level, String content) {
        this.level = level;
        this.content = content;
    }

    @Override
    public Level getLevel() {
        return this.level;
    }

    @Override
    public Type getType() {
        return Type.LEAVE;
    }

    @Override
    public String getContent() {
        return content;
    }

}
