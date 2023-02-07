package com.laudynetwork.networkutils.api.queue;

import java.util.UUID;
import java.util.concurrent.Future;

@SuppressWarnings("unused")
public interface Destination {
  /**
   * Send a query to destination
   *
   * @param user to check for
   */
  Future<Verdict> query(UUID user);

  /**
   * Get priority
   *
   * @return priority
   */
  int getPriority();

  /**
   * Get name of destination
   *
   * @return name
   */
  String getName();
}
