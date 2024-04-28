package Modules;

import CollectionObject.Coordinates;
import CollectionObject.Vehicle;
import CollectionObject.VehicleModel;
import CollectionObject.VehicleType;
import Network.User;

import java.sql.*;
import java.util.Date;

public class DBProvider{
    private static Connection connection;

    public static void establishConnection(String url, String user, String password){
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkUserExistence(String username){

        String query = "SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)";

        try (PreparedStatement p = connection.prepareStatement(query)){

            p.setString(1, username);
            ResultSet res = p.executeQuery();
            if (res.next()){
                return res.getBoolean(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean checkUserPassword(User user){
        var username = user.getUsername();
        var hashedPassword = user.getPassword();

        String query = "SELECT hashedpassword FROM users WHERE username = ?";

        try (PreparedStatement p = connection.prepareStatement(query)){

            p.setString(1, username);
            ResultSet res = p.executeQuery();

            if (res.next()){
                String storedHashedPassword = res.getString("hashedpassword");
                return storedHashedPassword.equals(hashedPassword);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static void addUser(User user){
        var username = user.getUsername();
        var hashedPassword = user.getPassword();

        String query = "INSERT INTO users (username, hashedpassword) VALUES (?, ?)";

        try (PreparedStatement p = connection.prepareStatement(query)){

            p.setString(1, username);
            p.setString(2, hashedPassword);
            p.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void load() {

        String query = "SELECT vehicles.id, vehicles.name, vehicles.x, vehicles.y, vehicles.creationdate, vehicles.enginepower," +
                "vehicles.capacity, vehicles.distancetravelled, vehicles.vehicletype, users.username FROM vehicles JOIN users ON users.id = vehicles.creatorid";

        try (PreparedStatement p = connection.prepareStatement(query)){
            ResultSet res = p.executeQuery();

            while (res.next()){
                try {
                    CollectionService.elementsCount = res.getLong(1);
                    var element = new Vehicle(
                            res.getLong(1),
                            res.getString(2),
                            new Coordinates(res.getFloat(3), res.getDouble(4)),
                            res.getDate(5),
                            res.getDouble(6),
                            res.getFloat(7),
                            res.getFloat(8),
                            VehicleType.valueOf(res.getString(9)),
                            res.getString(10)
                    );
                    if (checkUserExistence(element.getCreator())){
                        CollectionService.collection.add(element);
                    }

                } catch (IllegalArgumentException e){
                    Server.logger.error("Повреждённый атрибут type у элемента с id " + res.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean addVehicle(VehicleModel vehicleModel){

        String query = "INSERT INTO vehicles (name, x, y, creationDate, enginepower, capacity, distancetravelled, vehicletype, creatorid)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, (SELECT id FROM users WHERE username = ?))";

        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setString(1, vehicleModel.getName());
            p.setFloat(2, vehicleModel.getCoordinates().getX());
            p.setDouble(3, vehicleModel.getCoordinates().getY());

            long dateInMilliseconds = new Date().getTime();
            p.setTimestamp(4, new Timestamp(dateInMilliseconds));

            p.setDouble(5, vehicleModel.getEnginePower());
            p.setFloat(6, vehicleModel.getCapacity());
            p.setFloat(7, vehicleModel.getDistanceTravelled());
            p.setString(8, vehicleModel.getType().getType());
            p.setString(9, vehicleModel.getUser().getUsername());

            p.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateVehicle(long id, VehicleModel vehicleModel){

        String query = "UPDATE vehicles SET name = ?, x = ?, y = ?, enginepower = ?, capacity = ?," +
                " distancetravelled = ?, vehicletype = ? WHERE id = ?";

        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setString(1, vehicleModel.getName());
            p.setFloat(2, vehicleModel.getCoordinates().getX());
            p.setDouble(3, vehicleModel.getCoordinates().getY());
            p.setDouble(4, vehicleModel.getEnginePower());
            p.setFloat(5, vehicleModel.getCapacity());
            p.setFloat(6, vehicleModel.getDistanceTravelled());
            p.setString(7, vehicleModel.getType().getType());
            p.setInt(8, (int) id);

            p.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeVehicleById(long id){

        String query = "DELETE FROM vehicles WHERE id = ?";

        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setLong(1, id);
            p.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeVehiclesGreaterThanId(long id){

        String query = "DELETE FROM vehicles WHERE id > ?";

        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setLong(1, id);
            p.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeVehiclesByType(VehicleType type){

        String query = "DELETE FROM vehicles WHERE vehicletype = ?";

        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setString(1, type.getType());
            p.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean clearVehicles(){

        String query = "TRUNCATE vehicles";

        try (PreparedStatement p = connection.prepareStatement(query)){
            p.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
