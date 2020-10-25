import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.Date;

/* Вспомогательный класс для ведения лога в локальном файле
Если файл carLog.txt существует, то продолжает писать в него,
если - нет, то создает новый
*/
public class Log {
    private OutputStreamWriter writer;

    public Log() {
        try {
            File file = new File("carLog.txt");
            if (!(file.isFile())) {
                file.createNewFile();
            }
            FileOutputStream fileStream = new FileOutputStream("carLog.txt", true);
            writer = new OutputStreamWriter(fileStream, "Windows-1251");
        }
        catch (java.io.IOException e) {
            System.out.println(e.toString());
        }
    }

    public void writeLog(String str) {
        try {
            writer.write(String.format(
                    "%s %s\n", getTime(), str));
            writer.flush();

        }
        catch (java.io.IOException e) {
            System.out.println(e.toString());
        }
    }

    public void printLog() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("carLog.txt"));
            String str;
            while ((str = reader.readLine()) != null) {
                System.out.println(str);
            }
            reader.close();
        }
        catch (java.io.IOException e) {
            System.out.println(e.toString());
        }
    }

    // Текущий Timestamp для лога
    private String getTime() {
        return new Timestamp(new Date().getTime()).toString();
    }

    public static void main(String[] args) {
        Log log = new Log();
        log.printLog();
    }
}
