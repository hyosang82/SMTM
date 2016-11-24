package kr.hyosang.smtm.crawler;

import kr.hyosang.smtm.common.BankInfo;

/**
 * Created by hyosang on 2016. 11. 3..
 */
public abstract class WorkerBase extends Thread {
    protected abstract String getTag();
    protected abstract BankInfo getBankInfo();

    protected void log(String str) {
        System.out.println(String.format("[%s] %s", getTag(), str));
    }
}
