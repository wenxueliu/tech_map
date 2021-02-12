package org.wenxueliu.ds.command.group;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class AddRequirementCommand extends Command {

    AddRequirementCommand() {
        super(new RequirementGroup());
    }
    AddRequirementCommand(Group group) {
        super(group);
    }

    @Override
    public void execute() {
        this.group.find();
        this.group.add();
        this.group.plan();
    }
}
