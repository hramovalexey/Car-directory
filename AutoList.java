import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class AutoList {
    private Connection conn;
    private Log log;

    /* Конструктор создает соединение с базой данных, запускает лог
    Если файл carLog.txt существует, то продолжает писать в него,
    если - нет, то создает новый
    */
    public AutoList() {
        String user = "postgres";
        String pass = "1";

        log = new Log();
        try {
            Class.forName("org.postgresql.Driver");
            // Подключение к моей локальной БД PostgreSQL
            conn = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/AutoDB", user, pass);
            log.writeLog(String.format("Connected to database AutoDB as %s", user));
        }

        catch (ClassNotFoundException | java.sql.SQLException e) {
            System.out.println(e.toString());
            log.writeLog(String.format("Connection fail to database AutoDB as %s. Error: %s", user,
                                       e.toString()));
        }
    }

    /* добавление авто
     str - Json запрос вида
     { "number" : "A020BE96", "model" : "Wolksvagen Tiguan", "color" : "Зеленый", "year" : 2016}
     */
    public void addCar(String str) {

        Auto auto = parseJson(str);
        if (auto != null) {
            String query = String.format("INSERT INTO auto_list VALUES('%s', '%s', '%s', %d);",
                                         auto.number(), auto.model(), auto.color(), auto.year());
            try {
                Statement stat = conn.createStatement();
                stat.execute(query);
                stat.close();
                System.out.println("Успех");
                log.writeLog(
                        String.format("Transaction succeeded. Query to DB: %s", query));
            }
            catch (java.sql.SQLException e) {
                if (e.getSQLState().equals("23505")) System.out.println("Объект уже существует");
                else System.out.println("Ошибка");
                log.writeLog(
                        String.format("Transaction failed. Query to DB: %s. Error: %s", query,
                                      e.toString()));
            }
        }
    }

    // удаление авто. str - номер автомобиля
    public void deleteCar(String str) {
        String query = String.format("SELECT FROM auto_list WHERE number = '%s';",
                                     str);
        String query2 = String.format("DELETE FROM auto_list WHERE number = '%s'", str);
        try {
            Statement stat = conn
                    .createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            stat.execute(query);
            ResultSet result = stat.getResultSet();
            if (result.next()) {
                // result.deleteRow(); - данная команда вызвала ошибку No primary key found for table auto_list. Поэтому само удаление реализовано через отдельный запрос
                stat.execute(query2);
                System.out.println("Успех");
                log.writeLog(
                        String.format("Transaction succeeded. Query to DB: %s", query2));
            }
            else System.out.println("Объект не найден");
            stat.close();
            String.format("Transaction failed. Query to DB: %s. Error: Object not found", query2);
        }
        catch (java.sql.SQLException e) {
            System.out.println(e.toString());
            System.out.println("Ошибка");
            log.writeLog(
                    String.format("Transaction failed. Query to DB: %s. Error: %s", query2,
                                  e.toString()));
        }
    }

    /* формирование sql запроса вида
    "SELECT * FROM auto_list WHERE color = 'Белый' AND model = 'Datsun' AND year = '2016' ORDER BY color, model, year;"
    из Json запроса
    */
    public String makeQuery(String str) {
        String query = "SELECT * FROM auto_list";
        String order = " ORDER BY ";
        if (str != null) {
            Auto auto = parseJson(str);
            if (auto != null && !(auto.map.isEmpty())) {
                query = query.concat(" WHERE ");
                for (String entry : auto.map.keySet()) {
                    query = query
                            .concat(String.format("%s = '%s' AND ", entry, auto.map.get(entry)));
                    order = order.concat(String.format("%s, ", entry));
                }
                query = query.substring(0, query.length() - 5);
                order = order.substring(0, order.length() - 2);
                query = query.concat(order);
            }
        }
        query = query.concat(";");
        return query;
    }

    // Текущий список авто без параметров
    public void listCar() {
        listCar(null);
    }

    /* текущий список авто с параметрами
    str - Json запрос вида
    { "model" : "Datsun", "color" : "Белый", "year" : 2016}
    в примере будут выведены все белые Datsun 2016го года
    и отсортированы в порядке model, color, year
     */
    public void listCar(String str) {
        String query = makeQuery(str);
        try {
            Statement stat = conn.createStatement();
            stat.execute(query);
            ResultSet result = stat.getResultSet();
            ResultCarList resultCarList = new ResultCarList();
            while (result.next()) {
                String number = result.getString("number");
                String model = result.getString("model");
                String color = result.getString("color");
                int year = result.getInt("year");
                resultCarList.addCar(new Auto(number, model, color, year));
            }
            stat.close();
            System.out.println(resultCarList.toString());
            log.writeLog(
                    String.format("Transaction succeeded. Query to DB: %s", query));
        }
        catch (java.sql.SQLException e) {
            System.out.println(e.toString());
            log.writeLog(
                    String.format("Transaction failed. Query to DB: %s. Error: %s", query,
                                  e.toString()));
        }
    }

    /* Показать статистику
    - Количество записей
    - Дата первой записи
    - Дата последней записи
     */
    public void getStats() {
        try {
            Statement stat = conn
                    .createStatement();
            // stat.execute("SELECT COUNT(*) from auto_list");
            ResultSet result = stat.executeQuery("SELECT COUNT(*) from auto_list");
            result.next();
            String count = result.getString(1);
            result = stat.executeQuery("SELECT MAX(timestamp), MIN(timestamp) from auto_list");
            result.next();
            String maxStamp = result.getString(1);
            String minStamp = result.getString(2);
            System.out
                    .printf("\nКоличество записей: %s\nДата первой записи: %s\nДата последней записи: %s\n",
                            count, minStamp, maxStamp);
            result.close();
            stat.close();
        }
        catch (java.sql.SQLException e) {
            System.out.println(e.toString());
        }
    }

    // Вывести лог
    public void printLog() {
        log.printLog();
    }

    // Преобразование json запроса в объект Auto
    private Auto parseJson(String str) {
        try {
            Object obj = new JSONParser().parse(str);
            JSONObject json = (JSONObject) obj;
            String number = (String) json.get("number");
            String model = (String) json.get("model");
            String color = (String) json.get("color");
            long year = (Long) json.get("year");
            return new Auto(number, model, color, (int) year);
        }
        catch (ParseException e) {
            System.out.println(e.toString());
        }
        return null;
    }

    // Тестовый клиент
    public static void main(String[] args) {
        // Запрос для добавления авто
        String str
                = "{ \"number\" : \"A020BE96\", \"model\" : \"Wolksvagen Tiguan\", \"color\" : \"Зеленый\", \"year\" : 2016}";

        // запрос для поиска с параметрами
        String request
                = "{ \"model\" : \"Datsun\", \"color\" : \"Белый\", \"year\" : 2016}";

        AutoList list = new AutoList();

        // Добавление нового авто (успешная транзакция)
        list.addCar(str);

        // Добавление нового авто (неуспешная транзакция)
        list.addCar(str);

        // Список машин
        list.listCar();

        // Список машин с параметрами
        list.listCar(request);

        // удаление машины
        list.deleteCar("A020BE96");

        // попытка удаления несуществующей машины
        list.deleteCar("111111");

        // статистика
        list.getStats();

        // показать лог
        list.printLog();
    }
}
