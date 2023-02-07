package com.laudynetwork.networkutils.api.queue;

import java.util.logging.Logger;

@SuppressWarnings("unused")
public interface QueueLogManager {
  /**
   * Get logger with prefix (for example, Queue name)
   *
   * @param name of logger
   * @return logger
   */
  Logger prefix(String name);

  /**
   * Get default logger
   *
   * @return logger
   */
  Logger get();
}
