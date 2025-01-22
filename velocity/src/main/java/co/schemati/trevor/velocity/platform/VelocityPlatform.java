package co.schemati.trevor.velocity.platform;

import co.schemati.trevor.api.instance.InstanceConfiguration;
import co.schemati.trevor.api.network.event.EventProcessor;
import co.schemati.trevor.api.util.Strings;
import co.schemati.trevor.common.platform.AbstractPlatformBase;
import co.schemati.trevor.velocity.TrevorVelocity;
import lol.arch.combinator.CombinatorProxyPlugin;

import java.util.logging.Logger;

public class VelocityPlatform extends AbstractPlatformBase {

  private final TrevorVelocity plugin;

  private VelocityEventProcessor eventProcessor;

  public VelocityPlatform(TrevorVelocity plugin) {
    super(plugin.getDataFolder().toFile());

    this.plugin = plugin;
  }

  public boolean init() {
    if (!super.init()) {
      return false;
    }

    try {
      final String newInstanceId = CombinatorProxyPlugin.Companion.getSelfGameServerID();
      instanceConfiguration = new InstanceConfiguration(newInstanceId);
      Logger.getGlobal().info("Connecting to Trevor using Agones Game Server ID");
    } catch (RuntimeException ignored) {

    }

    this.eventProcessor = new VelocityEventProcessor(plugin);

    return true;
  }

  @Override
  public EventProcessor getEventProcessor() {
    return eventProcessor;
  }

  @Override
  public boolean isOnlineMode() {
    return plugin.getProxy().getConfiguration().isOnlineMode();
  }

  @Override
  public void log(String message, Object... values) {
    plugin.getLogger().info(Strings.format(message, values));
  }
}
