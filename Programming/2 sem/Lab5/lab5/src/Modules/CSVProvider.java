package Modules;
import CollectionObject.Vehicle;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Stack;


public class CSVProvider implements DataProvider{

    @Override
    public void save(Stack<Vehicle> collection) {
        File collectionFile = new File("collection.csv");

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(collectionFile));
            byte[] header = (
                "'" + "id" + "'" + "," +
                "'" + "Название" + "'" + "," +
                "'" + "Координата x" + "'" + "," +
                "'" + "Координата y" + "'" + "," +
                "'" + "Дата создания" + "'" + "," +
                "'" + "Мощность двигателя" + "'" + "," +
                "'" + "Объем двигателя" + "'" + "," +
                "'" + "Пробег" + "'" + "," +
                "'" + "Тип" + "'" + "\n"
            ).getBytes(StandardCharsets.UTF_8);

            bos.write(header);
            saveToCSV(collection, bos);
            bos.close();

            System.out.println("Коллекция успешно сохранена");

        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода");
        }
    }

    @Override
    public void load(String arguments) {
        // TODO: 19.02.2024 сделать загрузку из файла

        Path path = Path.of(arguments);
        File collectionFile = path.getFileName().toFile();

//        CollectionService.collection =

    }


    private void saveToCSV(Stack<Vehicle> stack, BufferedOutputStream bos) throws IOException {
        for (Vehicle vehicle: stack){
            byte[] id = ("'" + vehicle.getId() + "'" + ",").getBytes(StandardCharsets.UTF_8);
            byte[] name = ("'" + vehicle.getName() + "'" + ",").getBytes(StandardCharsets.UTF_8);
            byte[] x = ("'" + vehicle.getCoordinates().getX() + "'" + ",").getBytes(StandardCharsets.UTF_8);
            byte[] y = ("'" + vehicle.getCoordinates().getY() + "'" + ",").getBytes(StandardCharsets.UTF_8);
            byte[] creationDate = ("'" + vehicle.getCreationDate() + "'" + ",").getBytes(StandardCharsets.UTF_8);
            byte[] enginePower = ("'" + vehicle.getEnginePower() + "'" + ",").getBytes(StandardCharsets.UTF_8);
            byte[] capacity = ("'" + vehicle.getCapacity() + "'" + ",").getBytes(StandardCharsets.UTF_8);
            byte[] distanceTravelled = ("'" + vehicle.getDistanceTravelled() + "'" + ",").getBytes(StandardCharsets.UTF_8);
            byte[] type = ("'" + vehicle.getType() + "'").getBytes(StandardCharsets.UTF_8);

            bos.write(id);
            bos.write(name);
            bos.write(x);
            bos.write(y);
            bos.write(creationDate);
            bos.write(enginePower);
            bos.write(capacity);
            bos.write(distanceTravelled);
            bos.write(type);

            bos.write("\n".getBytes(StandardCharsets.UTF_8));
        }
    }
}
