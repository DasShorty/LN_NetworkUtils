package com.laudynetwork.networkutils.api.queue;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("unused")
public enum Verdict {
  OK(true, "Accepted"),
  FORBIDDEN(false, "Forbidden"),
  WHITELISTED(false, "Whitelisted"),
  ALREADY_AT(false, "Current server"),
  TIMED_OUT(false, "Timed out"),
  UNKNOWN(false, "Unknown");

  public final boolean ok;
  /**
   * you can set a custom message if you want
   * */
  @Setter @Getter
  public String description;

  Verdict(boolean ok, String description) {
    this.ok = ok;
    this.description = description;
  }
}
