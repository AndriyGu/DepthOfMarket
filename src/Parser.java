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

                    //если

                    //старый-текущий <размер> по указаной цене если он меньше нуля значит записи были в АСК
                    long sizeOld = orderBook.query.get(price);
                    // если был ask,
                    if (orderBook.query.get(price) < 0) {
                        orderBook.query.put(price, sizeNew);
                        orderBook.ask.remove(price);
                        orderBook.bid.put(price, sizeNew);
                    }
                    // если был спред добавляем в книгу бид новую запись, а если был бид то просто обнавляем
                    else {
                        orderBook.query.put(price, sizeNew);
                        orderBook.bid.put(price, sizeNew);
                    }
                }
                // если в дереве еще нет такого значения цены
                else {
                        orderBook.query.put(price, sizeNew);
                        orderBook.bid.put(price, sizeNew);

                }
            }

            //  если в обновлении книги запрос аск
            else if (keyForAction.equals("ask")) {
                if (orderBook.query.containsKey(price)) {


                    //старый-текущий <размер> по указаной цене если он больше нуля значит записи были в БИД
                    //long sizeOld = orderBook.query.get(price);

                    // если был бид
                    if (orderBook.query.get(price) > 0) {
                        orderBook.query.put(price, -sizeNew);
                        orderBook.bid.remove(price);
                        orderBook.ask.put(price, -sizeNew);

                    }
                    //если было аск то обновляем книгу аск, а если спред то просто делаем новую запись
                    else {
                        orderBook.query.put(price, -sizeNew);
                        orderBook.ask.put(price, -sizeNew);
                    }
                }
                // если такого значения нет
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

            // если buy


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
                        // если после покупки предложение больше нуля ищем другую цену
                        // нужно удалить из ask заявку
                        // в query станет спредом

                        // в текущею минимальную цену ставим объем ноль (делаем спредом)
                        orderBook.query.put(orderBook.ask.firstKey(), 0L);
                        // удаляем текущую минимальную цену из списка заявок на продажу
                        orderBook.ask.remove(orderBook.ask.firstKey());
                        // получаем объем по новой минимальной цене
                        sizeOld = orderBook.ask.get(orderBook.ask.firstKey());
                        // манипуляции с именами переменных
                        sizeInWhile = sizeOld + sizeNew;
                        sizeNew = sizeInWhile;
                        // в текущую минимальную цену ставим новый объем
                        orderBook.query.put(orderBook.ask.firstKey(), sizeNew);
                        orderBook.ask.put(orderBook.ask.firstKey(), sizeNew);
                        // если станет 0 то делаем спредом в книге запросов и закрываем в книге АСК
                        if (sizeNew == 0) {
                            orderBook.query.put(orderBook.ask.firstKey(), sizeNew);
                            orderBook.ask.remove(orderBook.ask.firstKey());
                        }
                    }
                }
            }

            // если sell
            else {


                long sizeOld = orderBook.bid.get(orderBook.bid.lastKey());
                long sizeNew = sizeOld - sizeTemp;
                long sizeInWhile = 0L;


                // если размер больше ноля то заявка по максимальной цене еще осталась
                if (sizeNew > 0) {
                    orderBook.bid.put(orderBook.bid.lastKey(), sizeNew);
                    orderBook.query.put(orderBook.bid.lastKey(), sizeNew);
                }

                // если размер == 0 то заявка исчерпала себя и переходит в спред
                else if (sizeNew == 0) {
                    orderBook.query.put(orderBook.bid.lastKey(), sizeNew);
                    orderBook.bid.remove(orderBook.bid.lastKey());
                } else if (sizeNew < 0) {
                    while (sizeNew < 0) {
                        // если после продажи предложение меньшe нуля ищем другую цену
                        // нужно удалить из ask заявку
                        // в query станет спредом

                        // в текущею максимальную цену ставим объем ноль (делаем спредом)
                        orderBook.query.put(orderBook.bid.lastKey(), 0L);
                        // удаляем текущую максимальную цену из списка заявок на покупку
                        orderBook.bid.remove(orderBook.bid.lastKey());
                        // получаем объем по новой минимальной цене
                        sizeOld = orderBook.bid.get(orderBook.bid.lastKey());
                        // манипуляции с именами переменных
                        sizeInWhile = sizeOld + sizeNew;
                        sizeNew = sizeInWhile;
                        // в текущую максимальную цену ставим новый объем
                        orderBook.query.put(orderBook.bid.lastKey(), sizeNew);
                        orderBook.bid.put(orderBook.bid.lastKey(), sizeNew);
                        // если станет 0 то делаем спредом в книге запросов и закрываем в книге БИД
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
