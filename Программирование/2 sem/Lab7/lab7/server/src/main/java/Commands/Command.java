package Commands;

import CollectionObject.VehicleModel;
import Network.Response;
import Network.User;

public interface Command {
    Response execute(User user, String strArgument, VehicleModel objArgument);
}
