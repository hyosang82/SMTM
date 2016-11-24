package kr.hyosang.smtm.crawler.kb;

import kr.hyosang.smtm.common.Atm;
import kr.hyosang.smtm.common.define.BankCode;

import java.io.IOException;

/**
 * Created by hyosang on 2016. 10. 28..
 */
public class AtmListItem {
    public String name = "";
    public String code = "";
    public String tel = "";
    public String addr = "";
    public String addr2 = "";
    public String road = "";
    public String road2 = "";
    public String x = "";
    public String y = "";
    public String dist = "";
    public String wgsx = "";
    public String wgsy = "";
    public String frgnCrrATM = "";
    public String outAccYN = "";

    public Atm toAtm() {
        Atm atm = new Atm();
        atm.bankCode = BankCode.KB;
        atm.name = this.name.trim();
        atm.branchCode = "";     //지점정보 없음
        atm.cornerCode = this.code.trim();
        atm.address = this.road.trim() + " " + this.road2.trim();
        atm.description = atm.name;
        atm.openTime = 0;
        atm.closeTime = 0; //영업시간정보없음..

        try {
            double x = Double.parseDouble(this.wgsx.trim());
            double y = Double.parseDouble(this.wgsy.trim());

            atm.xE6 = (int)((double) x * (double) 100_000f);
            atm.yE6 = (int)((double) y * (double) 100_000f);
        }catch(NumberFormatException e) {
            System.err.println("Coordinate information fail! : " + this.wgsx + ", " + this.wgsy);
            atm.xE6 = 0;
            atm.yE6 = 0;
        }

        return atm;
    }
}
