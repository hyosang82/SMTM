package kr.hyosang.smtm.crawler;

/**
 * Created by hyosang on 2016. 11. 3..
 */
public abstract class WorkerBase extends Thread {
    protected abstract String getTag();

    protected void log(String str) {
        System.out.println(String.format("[%s] %s", getTag(), str));
    }
}
