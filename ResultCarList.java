import java.util.ArrayList;
import java.util.List;

// Класс для формирования списка результатов
public class ResultCarList {
    private ArrayList<Auto> list;

    public ResultCarList() {
        list = new ArrayList<>();
    }

    /* Так и не использовал метод получения копии списка
    Но он мог бы пригодиться при кэшировании результатов
     */
    public List<Auto> getList() {
        if (list != null) {
            ArrayList<Auto> returnList = new ArrayList<>(list.size());
            for (Auto a : list) {
                returnList.add(a);
            }
            return returnList;
        }
        return null;
    }

    public void addCar(Auto a) {
        list.add(a);
    }

    public String toString() {
        StringBuilder str = new StringBuilder(
                String.format("%-15s%-20s%-20s%-6s", "Номер", "Модель", "Цвет", "Год"));

        if (list != null) {
            for (Auto a : list) {
                str.append(
                        String.format("\n%s", a.toString())
                );
            }
        }
        return str.toString();
    }

    public static void main(String[] args) {

    }
}
