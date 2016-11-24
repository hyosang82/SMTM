import kr.hyosang.smtm.crawler.CrawlerRunner;

/**
 * Created by hyosang on 2016. 11. 3..
 */
public class Main {
    public static void main(String [] args) {
        CrawlerRunner runner = new CrawlerRunner();

        runner.start();

        try {
            runner.join();
        }catch(InterruptedException e) {
            e.printStackTrace();
        }

    }
}
