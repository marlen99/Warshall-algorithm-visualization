import com.company.WarshallAlgorithm;
import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;

public class WarshallTest {
    private String path = "C:/Users/nadez/IdeaProjects/GUIlast/test/file.txt"; //файл с тестом
    private String str1 = "a b\nb c\n c j\nj a";
    private String str2 = "a b\nb c\n";
    private String res1 = "1 1 1 1 \r\n" + "1 1 1 1 \r\n" + "1 1 1 1 \r\n" + "1 1 1 1 \r\n";
    private String res2 = "0 1 1 \r\n0 0 1 \r\n0 0 0 \r\n";

    @Test(expected = IllegalArgumentException.class)
    public void testsetGraphData() { //проверка ввода данных и построение матрицы смежности
        WarshallAlgorithm graph3 = new WarshallAlgorithm("0 0\na b\n");
        WarshallAlgorithm graph2 = new WarshallAlgorithm("A B\n"); //введены  названия вершин русским алфавитом
        WarshallAlgorithm graph1 = new WarshallAlgorithm(str1);
        WarshallAlgorithm graph4 = new WarshallAlgorithm("a bh\nb c\n co j\nj a");
        WarshallAlgorithm graph5 = new WarshallAlgorithm("a b h\nb c\n co j\nj a");

        assertEquals("Неверно строится матрица смежности", graph1.toString(), "0 1 0 0 \r\n" +
                "0 0 1 0 \r\n" + "0 0 0 1 \r\n" + "1 0 0 0 \r\n");

        graph1 = new WarshallAlgorithm("a v \n v g\n");

        assertEquals("Неверно строится матрица смежности", graph1.toString(), "0 1 0 \r\n" +
                "0 0 1 \r\n0 0 0 \r\n");
    }
    @Test
    public void test_transitiveClosure() { //проверка алгоритма
        WarshallAlgorithm graph1 = new WarshallAlgorithm(str1);
        WarshallAlgorithm graph2 = new WarshallAlgorithm(str2);
        graph1.transitiveClosure();
        graph2.transitiveClosure();
        assertEquals("Неверная работа Алгоритма", graph1.toString(), res1);
        assertEquals("Неверная работа Алгоритма", graph2.toString(), res2);
    }
    @Test
    public void test_stepUp() { //проверка перехода на шаг вперед
        String str3 = "a g\n g h\n h g\n";
        WarshallAlgorithm graph1 = new WarshallAlgorithm(str1);
        WarshallAlgorithm graph2 = new WarshallAlgorithm(str3);
        graph1.stepUp(); //переход на шаг вперед
        graph2.stepUp();
        assertEquals("Неверно посчитан следующий шаг", graph1.toString(), "0 1 1 0 \r\n" +
                "0 0 1 1 \r\n" + "1 0 0 1 \r\n" + "1 1 0 0 \r\n");
        assertEquals("Неверно посчитан следующий ша", graph2.toString(), "0 1 1 \r\n0 1 1 \r\n0 1 1 \r\n");
    }
    @Test
    public void test_stepDown() { //проверка перехода на шаг назад
        WarshallAlgorithm graph1 = new WarshallAlgorithm(str1);
        WarshallAlgorithm graph2 = new WarshallAlgorithm(str1);
        graph1.stepUp(); //переход на шаг вперед
        graph1.stepDown(); //переход на шаг назад
        assertEquals("Неверно посчитан предыдущий шаг", graph1.toString(), graph2.toString());
    }
    @Test
    public void test_toFinalResult() { //Проверка перехода к конечному состоянию
        WarshallAlgorithm graph1 = new WarshallAlgorithm(str2);
        WarshallAlgorithm graph2 = new WarshallAlgorithm(str1);
        graph1.stepUp(); //переход на шаг вперед
        graph1.stepUp();
        graph1.toFinalResult(); //переход к конечному результату не с начального состояния
        assertEquals("Неверный конечный результат", graph1.toString(), res2);

        graph2.stepUp(); //переход на шаг вперед
        graph2.stepUp();
        graph2.toFinalResult(); //переход к конечному результату не с начального состояния
        assertEquals("Неверный конечный результат", graph2.toString(), res1);
    }
    @Test
    public void test_toStart() {
        WarshallAlgorithm graph1 = new WarshallAlgorithm(str2);
        WarshallAlgorithm graph2 = new WarshallAlgorithm(str2);
        graph1.transitiveClosure();
        graph1.toStart(); //переход к начальному состоянию
        assertEquals("Неверный возврат к начальным значениям", graph1.toString(), graph2.toString());
    }
    @Test
    public void test_file() {
        String inputData = "";
        try {
            FileInputStream fstream = new FileInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fstream));
            StringBuilder builder = new StringBuilder();
            String currentLine = reader.readLine();
            while (currentLine != null) {
                builder.append(currentLine);
                builder.append("\n");
                currentLine = reader.readLine();
            }
            reader.close();
            inputData = builder.toString();
            try {
                WarshallAlgorithm graph = new WarshallAlgorithm(inputData);
            } catch (IllegalArgumentException ex) {
                System.out.println("Wrong input data.");
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
