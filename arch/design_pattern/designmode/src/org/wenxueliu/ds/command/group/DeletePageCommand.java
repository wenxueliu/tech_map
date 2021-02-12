package org.wenxueliu.ds.command.group;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class DeletePageCommand extends Command {

    DeletePageCommand() {
        super(new PageGroup());
    }

    DeletePageCommand(PageGroup group) {
       super(group);
    }

    @Override
    public void execute() {
        this.group.find();
        this.group.delete();
        this.group.plan();
    }
}
