package org.jenkinsci.plugins.teamstrigger;

public class FoundJob {

  private final String fullName;
  private final TeamsTrigger genericTrigger;

  public FoundJob(String fullName, TeamsTrigger genericTrigger) {
    this.fullName = fullName;
    this.genericTrigger = genericTrigger;
  }

  public TeamsTrigger getGenericTrigger() {
    return genericTrigger;
  }

  public String getFullName() {
    return fullName;
  }
}
