package org.wenxueliu.ds.command.house;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class ForbidSellCommand extends Command {
    @Override
    public void execute() {
        for (House h : this.houses) {
            h.forbidSell();
        }
    }
}
