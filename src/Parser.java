import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by AdanaC on 02.03.2020.
 */
public class Parser {

    public List<String> toFile = new ArrayList<>();

    public void parserLine(String line, OrderBook orderBook) {


        List<String> items = Arrays.asList(line.split(","));
        //_____________________________________________________________________________________
        // upgrade
        if (items.get(0).equals("u")) {
            String keyForAction = items.get(3);
            long price = Long.parseLong(items.get(1));
            long sizeNew = Long.parseLong(items.get(2));


            if (sizeNew == 0) {
                orderBook.query.put(price, 0L);
                orderBook.ask.remove(price);
                orderBook.bid.remove(price);

            }
            else if (keyForAction.equals("bid")) {


                if (orderBook.query.containsKey(price)) {

                    //����

                    //������-������� <������> �� �������� ���� ���� �� ������ ���� ������ ������ ���� � ���
                    long sizeOld = orderBook.query.get(price);
                    // ���� ��� ask,
                    if (orderBook.query.get(price) < 0) {
                        orderBook.query.put(price, sizeNew);
                        orderBook.ask.remove(price);
                        orderBook.bid.put(price, sizeNew);
                    }
                    // ���� ��� ����� ��������� � ����� ��� ����� ������, � ���� ��� ��� �� ������ ���������
                    else {
                        orderBook.query.put(price, sizeNew);
                        orderBook.bid.put(price, sizeNew);
                    }
                }
                // ���� � ������ ��� ��� ������ �������� ����
                else {
                        orderBook.query.put(price, sizeNew);
                        orderBook.bid.put(price, sizeNew);

                }
            }

            //  ���� � ���������� ����� ������ ���
            else if (keyForAction.equals("ask")) {
                if (orderBook.query.containsKey(price)) {


                    //������-������� <������> �� �������� ���� ���� �� ������ ���� ������ ������ ���� � ���
                    //long sizeOld = orderBook.query.get(price);

                    // ���� ��� ���
                    if (orderBook.query.get(price) > 0) {
                        orderBook.query.put(price, -sizeNew);
                        orderBook.bid.remove(price);
                        orderBook.ask.put(price, -sizeNew);

                    }
                    //���� ���� ��� �� ��������� ����� ���, � ���� ����� �� ������ ������ ����� ������
                    else {
                        orderBook.query.put(price, -sizeNew);
                        orderBook.ask.put(price, -sizeNew);
                    }
                }
                // ���� ������ �������� ���
                else {
                        orderBook.query.put(price, -sizeNew);
                        orderBook.ask.put(price, -sizeNew);

                }
            }
        }

        //________________________________________________________________________________________
        // query
        if (items.get(0).equals("q")) {
            String recordLine = "";
            if (items.get(1).equals("best_bid") && orderBook.bid.size() > 0) {
                long max = orderBook.bid.lastKey();
                recordLine = max + "," + orderBook.bid.get(max);
            } else if (items.get(1).equals("best_ask") && orderBook.ask.size() > 0) {
                long max = orderBook.ask.firstKey();
                recordLine = max + "," + (-orderBook.ask.get(max));
            } else if (items.get(1).equals("size") && orderBook.query.containsKey(Long.parseLong(items.get(2)))) {
                recordLine = (Math.abs(orderBook.query.get(Long.parseLong(items.get(2)))))+"";
            } else if (items.get(1).equals("best_bid") && orderBook.bid.size() == 0) {
                recordLine = "best_bid is empty";
            } else if (items.get(1).equals("best_ask") && orderBook.ask.size() == 0) {
                recordLine = "best_ask is empty";
            } else if (items.get(1).equals("size") && !orderBook.query.containsKey(Long.parseLong(items.get(2)))) {
                recordLine = "price "+ Long.parseLong(items.get(2))+" not found";
            }

            toFile.add(recordLine);
        }

        // __________________________________________________________________________________________
        // option
        if (items.get(0).equals("o")) {
            long sizeTemp = Long.parseLong(items.get(2));

            // ���� buy


            if (items.get(1).equals("buy")) {
                long sizeOld = orderBook.ask.get(orderBook.ask.firstKey());
                long sizeNew = sizeOld + sizeTemp;
                long sizeInWhile = 0L;

                if (sizeNew < 0) {
                    orderBook.ask.put(orderBook.ask.firstKey(), sizeNew);
                    orderBook.query.put(orderBook.ask.firstKey(), sizeNew);
                } else if (sizeNew == 0) {
                    orderBook.query.put(orderBook.ask.firstKey(), sizeNew);
                    orderBook.ask.remove(orderBook.ask.firstKey());
                } else if (sizeNew > 0) {

                    while (sizeNew > 0) {
                        // ���� ����� ������� ����������� ������ ���� ���� ������ ����
                        // ����� ������� �� ask ������
                        // � query ������ �������

                        // � ������� ����������� ���� ������ ����� ���� (������ �������)
                        orderBook.query.put(orderBook.ask.firstKey(), 0L);
                        // ������� ������� ����������� ���� �� ������ ������ �� �������
                        orderBook.ask.remove(orderBook.ask.firstKey());
                        // �������� ����� �� ����� ����������� ����
                        sizeOld = orderBook.ask.get(orderBook.ask.firstKey());
                        // ����������� � ������� ����������
                        sizeInWhile = sizeOld + sizeNew;
                        sizeNew = sizeInWhile;
                        // � ������� ����������� ���� ������ ����� �����
                        orderBook.query.put(orderBook.ask.firstKey(), sizeNew);
                        orderBook.ask.put(orderBook.ask.firstKey(), sizeNew);
                        // ���� ������ 0 �� ������ ������� � ����� �������� � ��������� � ����� ���
                        if (sizeNew == 0) {
                            orderBook.query.put(orderBook.ask.firstKey(), sizeNew);
                            orderBook.ask.remove(orderBook.ask.firstKey());
                        }
                    }
                }
            }

            // ���� sell
            else {


                long sizeOld = orderBook.bid.get(orderBook.bid.lastKey());
                long sizeNew = sizeOld - sizeTemp;
                long sizeInWhile = 0L;


                // ���� ������ ������ ���� �� ������ �� ������������ ���� ��� ��������
                if (sizeNew > 0) {
                    orderBook.bid.put(orderBook.bid.lastKey(), sizeNew);
                    orderBook.query.put(orderBook.bid.lastKey(), sizeNew);
                }

                // ���� ������ == 0 �� ������ ��������� ���� � ��������� � �����
                else if (sizeNew == 0) {
                    orderBook.query.put(orderBook.bid.lastKey(), sizeNew);
                    orderBook.bid.remove(orderBook.bid.lastKey());
                } else if (sizeNew < 0) {
                    while (sizeNew < 0) {
                        // ���� ����� ������� ����������� �����e ���� ���� ������ ����
                        // ����� ������� �� ask ������
                        // � query ������ �������

                        // � ������� ������������ ���� ������ ����� ���� (������ �������)
                        orderBook.query.put(orderBook.bid.lastKey(), 0L);
                        // ������� ������� ������������ ���� �� ������ ������ �� �������
                        orderBook.bid.remove(orderBook.bid.lastKey());
                        // �������� ����� �� ����� ����������� ����
                        sizeOld = orderBook.bid.get(orderBook.bid.lastKey());
                        // ����������� � ������� ����������
                        sizeInWhile = sizeOld + sizeNew;
                        sizeNew = sizeInWhile;
                        // � ������� ������������ ���� ������ ����� �����
                        orderBook.query.put(orderBook.bid.lastKey(), sizeNew);
                        orderBook.bid.put(orderBook.bid.lastKey(), sizeNew);
                        // ���� ������ 0 �� ������ ������� � ����� �������� � ��������� � ����� ���
                        if (sizeNew == 0) {
                            orderBook.query.put(orderBook.bid.lastKey(), sizeNew);
                            orderBook.bid.remove(orderBook.bid.lastKey());
                        }
                    }
                }
            }
        }
    }
}
