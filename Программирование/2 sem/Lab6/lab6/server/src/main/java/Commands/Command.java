package Commands;

import CollectionObject.VehicleModel;
import Network.Response;

public interface Command {
    Response execute(String strArgument, VehicleModel objArgument);
}
