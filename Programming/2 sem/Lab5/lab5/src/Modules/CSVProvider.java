package Modules;
import CollectionObject.Coordinates;
import CollectionObject.Vehicle;
import CollectionObject.VehicleType;
import Exceptions.EmptyFieldException;
import Exceptions.IllegalVehicleTypeException;
import Exceptions.NegativeFieldException;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static CollectionObject.VehicleType.*;


public class CSVProvider implements DataProvider{
    protected static Path COLLECTION_PATH;
    private long maxId = 0L;
    private Stack<Vehicle> stack = CollectionService.collection;

    public CSVProvider(Path collectionPath) {
        COLLECTION_PATH = collectionPath;
    }

    @Override
    public void save(Stack<Vehicle> collection) {
        File collectionFile = new File(String.valueOf(COLLECTION_PATH));

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(collectionFile));

            byte[] header = (
                 "id" +  "," +
                 "Название" +  "," +
                 "Координата x" +  "," +
                 "Координата y" +  "," +
                 "Дата создания" +  "," +
                 "Мощность двигателя" +  "," +
                 "Объем двигателя" +  "," +
                 "Пробег" +  "," +
                 "Тип" +  "\n"
            ).getBytes(StandardCharsets.UTF_8);

            bos.write(header);
            saveToCSV(collection, bos);
            bos.flush();
            bos.close();

            System.out.println("Коллекция успешно сохранена");

        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода");
        }
    }

    @Override
    public void load() {
        File collectionFile = new File(String.valueOf(COLLECTION_PATH));

            try {
                BufferedReader fileReader = new BufferedReader(new FileReader(collectionFile));
                CSVParser parser = new CSVParserBuilder().withSeparator(',').build();
                CSVReader csvReader = new CSVReaderBuilder(fileReader).withCSVParser(parser).withSkipLines(1).build();

                List<String[]> lines = csvReader.readAll();

                try{
                    for (String[] line : lines) {
                        Vehicle el = new Vehicle();
                        Coordinates coordinates = new Coordinates();

                        var id = askId(line[0]);
                        if (id > maxId){
                            maxId = id;
                        }
                        el.setId(id);
                        CollectionService.elementsCount = maxId;

                        var name = askString(line[1]);
                        el.setName(name);

                        var x = askX(line[2]);
                        var y = askY(line[3]);
                        coordinates.setX(x);
                        coordinates.setY(y);
                        el.setCoordinates(coordinates);

                        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                        Date date = formatter.parse(line[4]);
                        el.setCreationDate(date);

                        var power = askDouble(line[5]);
                        el.setEnginePower(power);

                        var capacity = askFloat(line[6]);
                        el.setCapacity(capacity);

                        var distance = askFloat(line[7]);
                        el.setDistanceTravelled(distance);

                        var type = askVehicleType(line[8]);
                        el.setType(type);

                        stack.add(el);
                        System.out.println("Элемент " + id + " был успешно загружен");
                    }
                } catch (NumberFormatException e){
                    System.out.println("Ошибка формата числа. Проверьте разделитель в файле коллекции");
                    System.exit(1);
                } catch (ParseException e) {
                    System.out.println("Ошибка конвертации объекта. Проверьте файл");
                    System.exit(1);
                } catch (EmptyFieldException | NegativeFieldException | IllegalVehicleTypeException e) {
                    System.out.println(e.getMessage());
                    System.exit(1);
                }
            }
            catch (IOException e) {
                System.out.println("Ошибка ввода/вывода. Проверьте путь или имя файла");
                System.exit(1);
            } catch (CsvException e) {
                System.out.println("Невалидный файл коллекции. Проверьте разделители");
                System.exit(1);
            }
    }

    private void saveToCSV(Stack<Vehicle> stack, BufferedOutputStream bos) throws IOException {
        for (Vehicle vehicle: stack){
            byte[] id = ( vehicle.getId() +  ",").getBytes(StandardCharsets.UTF_8);
            byte[] name = ( vehicle.getName() +  ",").getBytes(StandardCharsets.UTF_8);
            byte[] x = ( vehicle.getCoordinates().getX() +  ",").getBytes(StandardCharsets.UTF_8);
            byte[] y = ( vehicle.getCoordinates().getY() +  ",").getBytes(StandardCharsets.UTF_8);
            byte[] creationDate = ( vehicle.getCreationDate() +  ",").getBytes(StandardCharsets.UTF_8);
            byte[] enginePower = ( vehicle.getEnginePower() +  ",").getBytes(StandardCharsets.UTF_8);
            byte[] capacity = ( vehicle.getCapacity() +  ",").getBytes(StandardCharsets.UTF_8);
            byte[] distanceTravelled = ( vehicle.getDistanceTravelled() +  ",").getBytes(StandardCharsets.UTF_8);
            byte[] type = (vehicle.getType().toString()).getBytes(StandardCharsets.UTF_8);

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

    private Long askId(String num) throws NegativeFieldException {
        long id = Long.parseLong(num);
        if (id <= 0){
            throw new NegativeFieldException("id должен быть больше нуля");
        }
        return id;
    }

    private String askString(String name) throws EmptyFieldException {
        if (name.isEmpty()){
            throw new EmptyFieldException("Это поле не может быть пустым. Проверьте файл");
        }
        return name.trim();
    }

    private float askX(String num) throws NumberFormatException {
        return Float.parseFloat(num);
    }

    private double askY(String num) throws NumberFormatException {
        return Double.parseDouble(num);
    }

    private float askFloat(String n) throws NumberFormatException, NegativeFieldException {
        float num = Float.parseFloat(n);
        if (num > 0){
            return num;
        } else {
            throw new NegativeFieldException("Число в этом поле не может быть отрицательным");
        }
    }

    private double askDouble(String n) throws NumberFormatException, NegativeFieldException {
        double num = Double.parseDouble(n);
        if (num > 0){
            return num;
        } else {
            throw new NegativeFieldException("Число в этом поле не может быть отрицательным");
        }
    }

    private VehicleType askVehicleType(String t) throws IllegalVehicleTypeException {
                VehicleType vehicleType;
                var type = t.toUpperCase();
                switch (type){
                    case "BOAT":
                        vehicleType = BOAT;
                        break;
                    case "HOVERBOARD":
                        vehicleType = HOVERBOARD;
                        break;
                    case "SPACESHIP":
                        vehicleType = SPACESHIP;
                        break;
                    default:
                        throw new IllegalVehicleTypeException("Такого типа транспортного средства не существует");
                }
                return vehicleType;

    }
}

