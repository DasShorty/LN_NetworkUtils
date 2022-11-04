import com.laudynetwork.networkutils.NetworkHandler;
import com.laudynetwork.networkutils.api.player.NetworkPlayer;

import java.util.UUID;

public class Test {

    public static void main(String[] args) {

        var networkHandler = new NetworkHandler("mongodb://anthony:anthony@localhost:27017/?authMechanism=SCRAM-SHA-1&authSource=admin");

        var networkPlayer = new NetworkPlayer(networkHandler.getMongoConnection(), UUID.fromString("1f9905df-3e25-4852-b7b8-92e1e61e67ac"));

        networkPlayer.createNetworkData(SQLTextHandler.Language.GERMAN);

    }

}
