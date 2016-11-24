package kr.hyosang.smtm.crawler.kb;

import com.google.firebase.database.*;
import com.google.gson.Gson;
import kr.hyosang.smtm.common.Atm;
import kr.hyosang.smtm.common.define.BankCode;
import kr.hyosang.smtm.crawler.WorkerBase;
import kr.hyosang.smtm.crawler.wooribank.BranchInfo;
import kr.hyosang.util.Util;
import kr.hyosang.webclient.HsWebClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hyosang on 2016. 10. 26..
 */
public class KbWorker extends WorkerBase {
    @Override
    public void run() {
        HsWebClient webClient = new HsWebClient();
        webClient.setRequestMethod("POST");
        webClient.setDefaultResponseCharset("EUC-KR");

        Gson gson = new Gson();

        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("atm");

            int page = 0;
            int pageItemCount;
            int totalCount = 0;

            do {
                pageItemCount = 0;

                log("Page " + page + "...");

                Map<String, String> params = new HashMap<String, String>();
                params.put("searchtype", "atm_region");
                params.put("type01", String.valueOf(page));     //page는 0부터 시작함
                params.put("type02", "495286");
                params.put("type03", "1129803");
                params.put("type04", "");   //검색어. 없으면 전체..?
                params.put("type05", "0");
                params.put("type06", "1");
                params.put("type07", "1");
                params.put("type08", "");
                params.put("type09", "");
                params.put("type10", "20");
                params.put("type11", "undefined");
                params.put("USER_TYPE", "03");

                String listBody = webClient.getPage("https://omoney.kbstar.com/quics?asfilecode=548565", params);

                ListSet listSet = gson.fromJson(listBody, ListSet.class);

                Map<String, String> detailParam = new HashMap<>();

                if(listSet.list != null) {
                    for(AtmListItem item : listSet.list) {
                        detailParam.put("searchtype", "atm_dtl");
                        detailParam.put("USER_TYPE", "03");
                        detailParam.put("type01", item.code.trim());

                        String detailBody = webClient.getPage("https://omoney.kbstar.com/quics?asfilecode=548565", detailParam);

                        AtmDetail atmDetail = gson.fromJson(detailBody, AtmDetail.class);

                        String time = atmDetail.getOpenTime();

                        Atm atm =  item.toAtm();
                        parseOpentime(time, atm);

                        String key = String.format("%s_%s", BankCode.KB, atm.cornerCode);

                        ref.child(key).setValue(atm);

                        log("ATM: " + atm.toString());

                        pageItemCount++;
                    }
                }

                page++;

                totalCount += pageItemCount;

                if(page > 2) break;
            }while(pageItemCount > 0);

            log("Total found: " + totalCount);

        }catch(IOException e) {
            e.printStackTrace();
        }

    }

    private void parseOpentime(String time, Atm atm) {
        short open = 0;
        short close = 0;

        Pattern p = Pattern.compile("([0-9]{2}):([0-9]{2})~([0-9]{2}):([0-9]{2})");
        Matcher m = p.matcher(time);
        if(m.matches()) {
            atm.openTime = Util.parseInt(m.group(1) + m.group(2));
            atm.closeTime =  Util.parseInt(m.group(3) + m.group(4));
        }else {
            if("24시간".equals(time)) {
                atm.openTime = 0;
                atm.closeTime = 2400;
            }else {
                System.err.println("Cannot parse opentime: " + time);
            }
        }
    }

    @Override
    protected String getTag() {
        return "KB";
    }
}
