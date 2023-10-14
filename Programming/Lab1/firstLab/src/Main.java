import static java.lang.Math. *;


public class Main {
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
            x[i] = (random() * (MAX - MIN)) + MIN;
        }
        // создаем и инициализируем массив переменных типа long для проверки
        long[] checkSet = {5, 7, 17, 19, 23};
        // создаем двумерный массив типа double и заполняем его по формулам
        double[][] array = new double[11][16];

        for (int i = 0; i <11*16; i++){
            int row = i / 16;
            int column = i % 16;
            if (c[row] == 15) array[row][column] = cbrt(pow((log(x[column]) / 2), asin((x[column] + 4.5) / 15)));
            else if (contains(checkSet, c[row])) array[row][column] = sqrt(0.75 / cos(cbrt(PI / x[column])));
            else array[row][column] = sin(sqrt(3 / pow(E, x[column])));
        }

        //выводим итоговый массив
        for (int i = 0; i <11*16; i++){
            int row = i / 16;
            int column = i % 16;
            System.out.printf("%6.3f; ", array[row][column]);
            if ((i+1) % 16 == 0) System.out.println();
        }
    }
}