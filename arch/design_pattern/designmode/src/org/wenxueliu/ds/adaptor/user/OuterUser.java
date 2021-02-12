package org.wenxueliu.ds.adaptor.user;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuwenxue on 08/10/2017.
 */
public class OuterUser implements IOuterUser {
    @Override
    public Map getUserBaseInfo() {
        HashMap<String, String> baseInfoMap = new HashMap<>();
        baseInfoMap.put("userName", "s1");
        baseInfoMap.put("moibleNumber", "12345678");
        return baseInfoMap;
    }

    @Override
    public Map getUserOfficeInfo() {
        HashMap<String, String> officeInfo = new HashMap<>();
        officeInfo.put("jobPosition", "beijing");
        officeInfo.put("officeTelNumber", "13512345678");
        return officeInfo;
    }

    @Override
    public Map getUserHomeInfo() {
        HashMap<String, String> homeInfo = new HashMap<>();
        homeInfo.put("homeTelNumber", "1233456666");
        homeInfo.put("homeAddress", "shanghai");
        return homeInfo;
    }
}
