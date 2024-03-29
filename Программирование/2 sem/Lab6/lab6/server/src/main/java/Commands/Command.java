package Commands;

import CollectionObject.Vehicle;
import Network.Response;

public interface Command {
    Response execute(String strArgument, Vehicle objArgument);
}
