import java.util.Map;
import java.util.TreeMap;

// Вспомогательный класс для объекта Auto (методы по манипуляциям с характеристиками авто)
public class Auto {
    public final Map<String, String> map;

    public Auto(String number, String model, String color, int year) {
        map = new TreeMap<>();
        String[] key = { "number", "model", "color", "year" };
        String[] value = { number, model, color, year == 0 ? null : String.valueOf(year) };
        for (int i = 0; i < value.length; i++) {
            if (value[i] != null) map.put(key[i], value[i]);
        }
    }

    public String number() {
        return map.get("number");
    }

    public String model() {
        return map.get("model");
    }

    public String color() {
        return map.get("color");
    }

    public int year() {
        return Integer.parseInt(map.get("year"));
    }


    public String toString() {
        return String.format("%-15s%-20s%-20s%-6s", map.get("number"), map.get("model"),
                             map.get("color"), map.get("year"));
    }

    public static void main(String[] args) {

    }
}
