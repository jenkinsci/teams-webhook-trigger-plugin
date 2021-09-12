package org.jenkinsci.plugins.teamstrigger.whitelist;

public class WhitelistException extends Exception {

  private static final long serialVersionUID = -3821871257758501700L;

  public WhitelistException(final String string) {
    super(string);
  }
}
