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

        String name = askString(InputScanner);

        float x = askX(InputScanner);
        double y = askY(InputScanner);
        Coordinates coordinates = new Coordinates(x, y);

        double enginePower = askDouble(InputScanner);
        float capacity = askFloat(InputScanner);
        float distanceTravelled = askFloat(InputScanner);

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
                return switch (type) {
                    case "BOAT" -> BOAT;
                    case "HOVERBOARD" -> HOVERBOARD;
                    case "SPACESHIP" -> SPACESHIP;
                    default -> throw new EmptyFieldException("Такого типа транспортного средства не существует. " +
                            "Заполните тип корректно: ");
                };
            } catch (EmptyFieldException e){
                System.out.println(e.getMessage());
            }
        }
    }

}
