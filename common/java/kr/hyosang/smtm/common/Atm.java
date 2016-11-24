package kr.hyosang.smtm.common;

import com.google.firebase.database.Exclude;

/**
 * Created by hyosang on 2016. 10. 27..
 */
public class Atm {
    public String bankCode = "";
    public String branchCode = "";
    public String cornerCode = "";
    public String name = "";
    public String address = "";
    public String description = "";
    public int xE6 = 0;
    public int yE6 = 0;
    public int openTime = 0;
    public int closeTime = 0;

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("[").append(bankCode).append("][").append(branchCode).append("][").append(cornerCode).append("] ")
                .append(name).append("(").append(address).append(") (").append(description).append(")")
                .append(String.format("(%.6f, %.6f)", (double)xE6 / 100000f, (double)yE6 / 100000f))
                .append(String.format("[%s ~ %s]", getOpenTimeString(), getCloseTimeString()));

        return sb.toString();
    }

    @Exclude
    public String getOpenTimeString() {
        String str = String.format("%04d", openTime);
        return str.substring(0, 2) + ":" + str.substring(2, 4);
    }

    @Exclude
    public String getCloseTimeString() {
        String str = String.format("%04d", closeTime);
        return str.substring(0, 2) + ":" + str.substring(2, 4);
    }
}
