package org.wenxueliu.ds.adaptor.user;

import java.util.Map;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class OuterUserInfo extends OuterUser implements IUserInfo {
    private Map<String, String> baseInfo = super.getUserBaseInfo();
    private Map<String, String> homeInfo = super.getUserHomeInfo();
    private Map<String, String> officeInfo = super.getUserOfficeInfo();

    @Override
    public String getUserName() {
        return baseInfo.get("userName");
    }

    @Override
    public String getHomeAddress() {
        return homeInfo.get("homeAddress");
    }

    @Override
    public String getMobileNumber() {
        return baseInfo.get("homeAddress");
    }

    @Override
    public String getOfficelTelNumber() {
        return officeInfo.get("officeNumber");
    }

    @Override
    public String getJobPosition() {
        return officeInfo.get("jobPosition");
    }

    @Override
    public String getHomeTelNumber() {
        return homeInfo.get("homeTelNumber");
    }
}
