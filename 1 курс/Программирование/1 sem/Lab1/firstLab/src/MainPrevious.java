import java.lang.Math;


public class MainPrevious {
    // функция для проверки элемента на его наличии в списке
    public static boolean contains(long[] arr, long num) {
        boolean res = false;
        for (long el : arr){
            if (el == num) {
                res = true;
                break;
            }
        }
        return res;
    }
    public static void main(String[] args) {
        // создаем массив c из переменных типа long
        long[] c = new long[11];
        // заполняем его нечётными числами от 3 до 23 в порядке возрастания
        for (int i = 0; i < 11; i++) {
            c[i] = i*2 + 3;
        }
        // создаем массив x из переменных типа double
        double[] x = new double[16];
        // задаем константы для генерации рандомных чисел
        final double MIN = -3.0;
        final double MAX = 12.0;
        // заполняем массив
        for (int i = 0; i < 16; i++) {
            x[i] = (Math.random() * (MAX - MIN)) + MIN;
        }
        // создаем и инициализируем массив переменных типа long
        long[] checkSet = {5, 7, 17, 19, 23};
        // создаем двумерный массив типа double и заполняем его по формулам
        double[][] array = new double[11][16];
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 16; j++) {
                if (c[i] == 15) array[i][j] = Math.cbrt(Math.pow((Math.log(x[j]) / 2), Math.asin((x[j] + 4.5) / 15)));
                else if (contains(checkSet, c[i])) array[i][j] = Math.sqrt(0.75 / Math.cos(Math.cbrt(Math.PI / x[j])));
                else array[i][j] = Math.sin(Math.sqrt(3 / Math.pow(Math.E, x[j])));
            }
        }

        //выводим итоговый массив
        for (int i = 0; i < 11; i++) {
            System.out.print("\n");
            for (int j = 0; j < 16; j++){
                System.out.printf("%6.3f; ", array[i][j]);
            }
        }
    }
}
