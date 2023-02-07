package com.laudynetwork.networkutils.api.queue;

@SuppressWarnings("unused")
public interface QueueHandlerFactory {
  /**
   * Produce a handler for a queue
   *
   * @param queue to produce for
   * @return handler
   */
  QueueHandler produce(Queue queue);

  /**
   * Get name of a factory
   *
   * @return name
   */
  String getName();
}
