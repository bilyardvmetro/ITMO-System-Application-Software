package Utils;

import CollectionObject.Coordinates;
import CollectionObject.VehicleModel;
import CollectionObject.VehicleType;
import Exceptions.EmptyFieldException;
import Exceptions.NegativeFieldException;
import Network.User;

import java.util.Scanner;

import static CollectionObject.VehicleType.*;

public class VehicleAsker {

    public static VehicleModel createElement(User user){
        Scanner InputScanner = PromptScan.getUserScanner();

        System.out.println("Введите имя");
        String name = askString(InputScanner);

        System.out.println("Введите координату x:");
        float x = askX(InputScanner);

        System.out.println("Введите координату y:");
        double y = askY(InputScanner);

        Coordinates coordinates = new Coordinates(x, y);

        System.out.println("Введите мощность двигателя");
        double enginePower = askDouble(InputScanner);

        System.out.println("Введите объём двигателя");
        float capacity = askFloat(InputScanner);

        System.out.println("Введите пробег");
        float distanceTravelled = askFloat(InputScanner);

        System.out.print("""
                Введите один из доступных типов транспортного средства:
                BOAT
                HOVERBOARD
                SPACESHIP
                """);
        VehicleType vehicleType = askVehicleType(InputScanner);

        return new VehicleModel(name, coordinates, enginePower, capacity, distanceTravelled, vehicleType, user);
    }

    private static String askString(Scanner InputScanner) {
        while(true) {
            try {
                var name = InputScanner.nextLine();
                if (name.isBlank()){
                    throw new EmptyFieldException("Поле не может быть пустым. Введите его ещё раз: ");
                }
                return name.trim();
            }catch(EmptyFieldException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static float askX(Scanner InputScanner) {
        while(true) {
            try {
                return Float.parseFloat(InputScanner.nextLine());
            }catch (NumberFormatException e){
                System.out.println("Неверный формат числа. Введите его повторно:");
            }
        }
    }

    private static double askY(Scanner InputScanner) {
        while(true) {
            try {
                return Double.parseDouble(InputScanner.nextLine());
            }catch (NumberFormatException e){
                System.out.println("Неверный формат числа. Введите его повторно:");
            }
        }
    }

    private static float askFloat(Scanner InputScanner) {
        while(true) {
            try {
                float num = Float.parseFloat(InputScanner.nextLine());
                if (num > 0){
                    return num;
                } else {
                    throw new NegativeFieldException("Число не может быть отрицательным. Введите его ещё раз:");
                }

            } catch (NumberFormatException e){
                System.out.println("Неверный формат числа. Введите его повторно:");
            } catch (NegativeFieldException e){
                System.out.println(e.getMessage());
            }
        }
    }

    private static double askDouble(Scanner InputScanner) {
        while(true) {
            try {
                double num = Double.parseDouble(InputScanner.nextLine());
                if (num > 0){
                    return num;
                } else {
                    throw new NegativeFieldException("Число не может быть отрицательным. Введите его ещё раз:");
                }
            } catch (NumberFormatException e){
                System.out.println("Неверный формат числа. Введите его повторно:");
            } catch (NegativeFieldException e){
                System.out.println(e.getMessage());
            }
        }
    }

    private static VehicleType askVehicleType(Scanner InputScanner) {
        while (true){
            try {
                String type = InputScanner.nextLine().toUpperCase();
                VehicleType vehicleType;
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
                        throw new EmptyFieldException("Такого типа транспортного средства не существует. " +
                                "Заполните тип корректно: ");
                }
                return vehicleType;
            } catch (EmptyFieldException e){
                System.out.println(e.getMessage());
            }
        }
    }
}
