package tech.tagline.trevor.common;

import com.google.gson.Gson;
import tech.tagline.trevor.api.TrevorAPI;
import tech.tagline.trevor.api.TrevorService;
import tech.tagline.trevor.api.database.DatabaseConnection;
import tech.tagline.trevor.api.network.payload.DisconnectPayload;
import tech.tagline.trevor.api.instance.InstanceData;
import tech.tagline.trevor.api.database.Database;
import tech.tagline.trevor.api.util.Keys;
import tech.tagline.trevor.common.proxy.DatabaseProxyImpl;
import tech.tagline.trevor.api.data.Platform;
import tech.tagline.trevor.common.util.Protocol;

import java.util.UUID;

public class TrevorCommon implements TrevorAPI {

  private final Platform platform;

  private Gson gson;

  private Database database;
  private DatabaseProxyImpl proxy;

  private InstanceData data;

  public TrevorCommon(Platform platform) {
    this.platform = platform;
  }

  public boolean load() {
    TrevorService.setAPI(this);

    // TODO: Verify instance configuration values before pool creation
    this.gson = new Gson();

    this.data = new InstanceData();

    this.database = platform.getDatabaseConfiguration().create(platform, data, gson);

    this.proxy = new DatabaseProxyImpl(platform, database, gson);

    return true;
  }

  public boolean start() {
    if (!database.init(proxy)) {
      return false;
    }

    return true;
  }

  public boolean stop() {
    if (database != null) {
      DatabaseConnection connection = database.open().join();

      connection.deleteHeartbeat();

      if (connection.getNetworkPlayerCount() > 0) {
        connection.getNetworkPlayers().forEach(uuid -> {
          DisconnectPayload payload = connection.destroy(UUID.fromString(uuid));

          connection.publish(Keys.CHANNEL_DATA.of(), Protocol.serialize(payload, gson));
        });
      }

      database.kill();
    }
    return true;
  }

  public InstanceData getInstanceData() {
    return data;
  }

  public Platform getPlatform() {
    return platform;
  }

  public Database getDatabase() {
    return database;
  }

  public DatabaseProxyImpl getDatabaseProxy() {
    return proxy;
  }
}
