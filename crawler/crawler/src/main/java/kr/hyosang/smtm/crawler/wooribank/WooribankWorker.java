package kr.hyosang.smtm.crawler.wooribank;

import com.google.firebase.database.*;
import com.google.gson.Gson;
import kr.hyosang.smtm.common.Atm;
import kr.hyosang.smtm.common.BankInfo;
import kr.hyosang.smtm.common.define.BankCode;
import kr.hyosang.smtm.crawler.WorkerBase;
import kr.hyosang.webclient.HsWebClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hyosang on 2016. 10. 26..
 */
public class WooribankWorker extends WorkerBase {
    @Override
    public void run() {
        HsWebClient webClient = new HsWebClient();
        webClient.setRequestMethod("POST");

        try {
            FirebaseDatabase db = FirebaseDatabase.getInstance();

            DatabaseReference ref = db.getReference("atm/" + BankCode.WOORIBANK);

            int page = 0;
            int pageItemCount;
            int totalCount = 0;

            do {
                page++;

                log("Page " + page + "...");

                Map<String, String> params = new HashMap<String, String>();
                params.put("search_tab", "2");
                params.put("search_select", "1");
                params.put("branch_kind", "");
                params.put("branch_check", "");
                params.put("search_range", "0");
                params.put("search_range_for", "");
                params.put("search_type", "name");
                params.put("result_type", "name");
                params.put("province", "서울");
                params.put("gungu", "");
                params.put("dong", "");
                params.put("branch_name_giro", "");
                params.put("branch_code_giro", "");
                params.put("subway_name", "");
                params.put("place_name", "");
                //params.put("pageno", "1");
                params.put("search_string", "");
                params.put("Total", "0");
                params.put("flag", "");
                params.put("code", "");
                params.put("imgcode", "365ok");
                params.put("PR_TYPE", "");
                params.put("PR_CONTENTS", "");
                params.put("PAGE_INDEX", String.valueOf(page));

                String listBody = webClient.getPage("https://spib.wooribank.com/pib/jcc?withyou=CMCOM0153&__ID=c009291", params);

                Map<String, Object> updateSet = new HashMap<>();

                pageItemCount = 0;

                Pattern atmPattern = Pattern.compile("goMap365ex\\('([0-9]+)', '([0-9]+)'\\)");
                Matcher listMatcher = atmPattern.matcher(listBody);
                Map<String, String> infoParam = new HashMap<>();
                Gson gson = new Gson();
                String id;

                while (listMatcher.find()) {
                    String branchCode = listMatcher.group(1);
                    String cornerCode = listMatcher.group(2);

                    pageItemCount++;

                    infoParam.put("brCode", branchCode);
                    infoParam.put("cornr_cd", cornerCode);
                    String branchBody = webClient.getPage("https://spib.wooribank.com/pib/jcc?withyou=CMCOM0153&__ID=c012467", infoParam);

                    //-_-...
                    int startIdx = branchBody.indexOf("({");
                    if (startIdx >= 0) {
                        int endIdx = branchBody.lastIndexOf("})");
                        if (endIdx > startIdx) {
                            branchBody = branchBody.substring(startIdx + 1, endIdx + 1);
                        }
                    }
                    BranchInfo branch = gson.fromJson(branchBody, BranchInfo.class);
                    Atm atm = branch.toAtm();
                    atm.bankCode = BankCode.WOORIBANK;
                    atm.cornerCode = cornerCode;

                    log("ATM: " + atm.toString());

                    insertOrUpdate(ref, atm);
                }

                log("Page found : " + pageItemCount);
                totalCount += pageItemCount;

                if(page > 10) break;
            }while(pageItemCount > 0);

            log("Total found: " + totalCount);

        }catch(IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected String getTag() {
        return "WOORI";
    }

    @Override
    protected BankInfo getBankInfo() {
        return new BankInfo(BankCode.WOORIBANK, "우리은행");
    }

    @Override
    protected String getIndexKey(Atm item) {
        return String.format("%s%s_%s", item.bankCode, item.branchCode, item.cornerCode);
    }
}
