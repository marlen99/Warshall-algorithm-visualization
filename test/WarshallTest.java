import com.company.WarshallAlgorithm;
import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;

public class WarshallTest {
    @Test(expected = IllegalArgumentException.class)
    public void testsetGraphData() { //проверка ввода данных и построение матрицы смежности
        WarshallAlgorithm graph1 = new WarshallAlgorithm("0 0\na b\n");
        WarshallAlgorithm graph2 = new WarshallAlgorithm("A B\n"); //введены  названия вершин русским алфавитом
        WarshallAlgorithm graph3 = new WarshallAlgorithm("a b\nb c\n c j\nj a");
        WarshallAlgorithm graph4 = new WarshallAlgorithm("a bh\nb c\n co j\nj a");
        WarshallAlgorithm graph5 = new WarshallAlgorithm("a b h\nb c\n co j\nj a");

        assertEquals("Неверно строится матрица смежности", graph3.toString(), "0 1 0 0 \r\n" +
            "0 0 1 0 \r\n" + "0 0 0 1 \r\n" + "1 0 0 0 \r\n");

        graph3 = new WarshallAlgorithm("a v \n v g\n");

        assertEquals("Неверно строится матрица смежности", graph3.toString(), "0 1 0 \r\n" +
            "0 0 1 \r\n0 0 0 \r\n");
    }
    @Test
    public void testtransitiveClosure() { //проверка алгоритма
        WarshallAlgorithm graph1 = new WarshallAlgorithm("a b\nb c\n c j\nj a");
        WarshallAlgorithm graph2 = new WarshallAlgorithm("a b\nb c\n");

        graph1.transitiveClosure();
        graph2.transitiveClosure();
        assertEquals("Неверная работа Алгоритма", graph1.toString(), "1 1 1 1 \r\n" +
            "1 1 1 1 \r\n" + "1 1 1 1 \r\n" + "1 1 1 1 \r\n");
        assertEquals("Неверная работа Алгоритма", graph2.toString(), "0 1 1 \r\n" +
            "0 0 1 \r\n" + "0 0 0 \r\n");
    }
    @Test
    public void teststep() { //Проверка шагов алгоритма
        WarshallAlgorithm graph1 = new WarshallAlgorithm("a b\nb c\n c j\nj a");
        WarshallAlgorithm graph2 = new WarshallAlgorithm("a b\nb c\n c j\nj a");

        graph1.stepUp(); //переход на шаг вперед
        assertEquals("Неверно посчитан следующий шаг", graph1.toString(), "0 1 1 0 \r\n" +
            "0 0 1 1 \r\n" + "1 0 0 1 \r\n" + "1 1 0 0 \r\n");

        graph1.stepDown(); //переход на шаг назад
        assertEquals("Неверно посчитан предыдущий шаг", graph1.toString(), graph2.toString());

        graph1.stepUp();
        graph1.toFinalResult(); //переход к конечному результату не с начального состояния
        assertEquals("Неверный конечный результат", graph1.toString(), "1 1 1 1 \r\n" +
            "1 1 1 1 \r\n" + "1 1 1 1 \r\n" + "1 1 1 1 \r\n");

        graph1.toStart(); //переход к начальному состоянию
        assertEquals("Неверный возврат к начальным значениям", graph1.toString(), graph2.toString());
    }
    @Test
    public void filetest() {
        String inputData = "";
        String path = "C:/Users/nadez/IdeaProjects/file.txt";
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
                WarshallAlgorithm graph1 = new WarshallAlgorithm(inputData);
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
