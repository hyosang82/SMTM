package kr.hyosang.smtm.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hyosang on 2016. 11. 24..
 */
public class BankInfo {
    public String code;
    public String name;

    public BankInfo() {
        code = "";
        name = "";
    }

    public BankInfo(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Map<String, String> asMap() {
        Map<String, String> map = new HashMap<>();
        map.put("code", this.code);
        map.put("name", this.name);

        return map;
    }
}
