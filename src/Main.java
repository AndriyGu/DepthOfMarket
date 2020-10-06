import java.util.TreeMap;

public class Main {

    public static void main(String[] args) {
        OrderBook orderBook = new OrderBook();

        FileReader fileWorker = new FileReader();


        fileWorker.readfile(orderBook);

        fileWorker.writefile();


        TreeMap<Long, Long> ask = new TreeMap<Long, Long>();

        ask.remove(2L);
    }
}
