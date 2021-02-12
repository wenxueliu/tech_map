package org.wenxueliu.ds.respchain.oa;

import com.sun.org.apache.regexp.internal.RE;

/**
 * Created by liuwenxue on 07/10/2017.
 */
public class Main {


    public static  void main(String []args) {
        Request leave1 = new LeaveRequest(Level.A, "I'm sick");
        Request leave2 = new LeaveRequest(Level.C, "I'm cough");
        Request reimburse1 = new ReimburseRequest(Level.A, "sth done");
        Request reimburse2 = new ReimburseRequest(Level.B, "bussinesso");

        Invoker.processRequest(leave1);
        Invoker.processRequest(leave2);
        Invoker.processRequest(reimburse1);
        Invoker.processRequest(reimburse2);
    }
}
