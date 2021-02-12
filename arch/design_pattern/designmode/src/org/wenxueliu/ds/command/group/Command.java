package org.wenxueliu.ds.command.group;

import com.sun.org.apache.regexp.internal.RE;
import org.wenxueliu.ds.command.define.Receiver;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public abstract class Command {
    protected Group group;

    Command(Group group) {
        this.group = group;
    }

    public abstract void execute();
}
