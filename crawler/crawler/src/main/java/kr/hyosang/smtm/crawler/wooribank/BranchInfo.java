package kr.hyosang.smtm.crawler.wooribank;

import kr.hyosang.coordinate.CoordPoint;
import kr.hyosang.coordinate.TransCoord;
import kr.hyosang.smtm.common.Atm;
import kr.hyosang.util.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hyosang on 2016. 10. 26..
 */
public class BranchInfo {
    public String brNm;
    public String brCode;
    public String dis;
    public String giroNo;
    public String telNo;
    public String faxNo;
    public String tmpTxt;
    public String post1;
    public String post2;
    public String addr;
    public String tmpEstbr;
    public String hocme;
    public String weekYn;
    public String mapXpt;
    public String mapYpt;
    public String newAddr;
    public String newZipCd;
    public String newPost1;
    public String newPost2;
    public String brtm;
    public String etc;

    public Atm toAtm() {
        Atm atm = new Atm();
        atm.branchCode = this.brCode;
        atm.name = replaceEmChar(this.brNm);
        atm.address = this.newAddr;
        atm.description = this.tmpEstbr;

        try {
            int x = Integer.parseInt(this.mapXpt, 10);
            int y = Integer.parseInt(this.mapYpt, 10);

            CoordPoint pt = new CoordPoint(x, y);
            CoordPoint convPt = TransCoord.getTransCoord(pt, TransCoord.COORD_TYPE_WCONGNAMUL, TransCoord.COORD_TYPE_WGS84);

            atm.xE6 = (int)(convPt.x * 100_000);
            atm.yE6 = (int)(convPt.y * 100_000);


        }catch(NumberFormatException e) {
            e.printStackTrace();

            atm.xE6 = 0;
            atm.yE6 = 0;
        }

        String openTime = parseOpenTime();
        try {
            if (openTime.length() == 8) {
                atm.openTime = Util.parseInt(openTime.substring(0, 4));
                atm.closeTime = Util.parseInt(openTime.substring(4, 8));
            }else {
                System.err.println("Fail to parse opentime: " + this.brtm);
            }
        }catch(NumberFormatException e) {
            e.printStackTrace();
        }

        return atm;
    }

    private String parseOpenTime() {
        Pattern pattern = Pattern.compile("([0-9]{4}) ~ ([0-9]{4})");
        Matcher matcher = pattern.matcher(this.brtm);
        if(matcher.matches()) {
            return (matcher.group(1) + matcher.group(2));
        }else {
            pattern = Pattern.compile("([0-9]{2}):([0-9]{2})~([0-9]{2}):([0-9]{2})");
            matcher = pattern.matcher(this.brtm);

            if(matcher.matches()) {
                return (matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4));
            }
        }

        return "";
    }

    private String replaceEmChar(String str) {
        char [] emChars = new char[]{'Ａ', 'Ｂ', 'Ｃ', 'Ｄ', 'Ｆ', 'Ｇ', 'Ｈ', 'Ｉ', 'Ｊ', 'Ｋ', 'Ｌ', 'Ｏ', 'Ｐ', 'Ｓ', 'Ｔ', 'Ｘ'};
        char [] toChar = new char[]{'A', 'B', 'C', 'D', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'O', 'P', 'S', 'T', 'X'};

        String patternStr = String.format("[\\u%04X-\\u%04X]", (int)emChars[0], (int)emChars[emChars.length-1]);
        Pattern p = Pattern.compile(patternStr);
        Matcher m = p.matcher(str);
        if(m.find()) {
            for(int i=0;i<emChars.length;i++) {
                str = str.replaceAll(""+emChars[i], ""+toChar[i]);
            }
        }

        return str;

    }
}
