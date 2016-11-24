package kr.hyosang.smtm.crawler.kb;

/**
 * Created by hyosang on 2016. 10. 28..
 */
public class AtmDetail {
    public static class DetailListItem {
        public String name = "";
        public String addr = "";
        public String addr2 = "";
        public String road = "";
        public String road2 = "";
        public String frgnCrrATM = "";
        public String outAccYN = "";
        public String code = "";
        public DetailInfo[] list = null;
    }

    public static class DetailInfo {
        public String card = "";
        public String time = "";
    }

    public String total = "";
    public DetailListItem [] list = null;

    public String getOpenTime() {
        if((this.list != null) && (this.list.length > 0)) {
            if((this.list[0].list != null) && (this.list[0].list.length > 0)) {
                return this.list[0].list[0].time.trim();
            }
        }

        return "";
    }
}
