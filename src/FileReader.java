import java.io.*;

/**
 * Created by AdanaC on 02.03.2020.
 */
public class FileReader {
    public Parser parser = new Parser();

    public void readfile(OrderBook orderBook) {



        try {
            File file = new File("file3.txt");
            //создаем объект FileReader для объекта File
            java.io.FileReader fr = new java.io.FileReader(file);
            //создаем BufferedReader с существующего FileReader для построчного считывания
            BufferedReader reader = new BufferedReader(fr);
            // считаем сначала первую строку
            String line = reader.readLine();
            while (line != null) {
                parser.parserLine(line, orderBook);
                // считываем остальные строки в цикле
                line = reader.readLine();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void writefile(){


        String filePath = "outFile.txt";


        try {
            FileWriter writer = new FileWriter(filePath);
            BufferedWriter bufferWriter = new BufferedWriter(writer);
            for (String text: parser.toFile) {
                bufferWriter.write(text+"\n");
            }

            bufferWriter.close();
        }
        catch (IOException e) {
            System.out.println(e);
            System.exit(1);
            }
    }


}
