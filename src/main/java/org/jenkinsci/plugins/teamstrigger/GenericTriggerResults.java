package org.jenkinsci.plugins.teamstrigger;

public class GenericTriggerResults {

  private final String regexpFilterText;
  private final String regexpFilterExpression;
  private final String url;
  private final long id;
  private final boolean triggered;

  public GenericTriggerResults(
      final hudson.model.Queue.Item item,
      final String regexpFilterText,
      final String regexpFilterExpression) {
    if (item != null) {
      this.url = item.getUrl();
      this.id = item.getId();
      this.triggered = true;
    } else {
      this.url = null;
      this.id = 0;
      this.triggered = false;
    }

    this.regexpFilterText = regexpFilterText;
    this.regexpFilterExpression = regexpFilterExpression;
  }

  public boolean isTriggered() {
    return triggered;
  }

  public long getId() {
    return id;
  }

  public String getUrl() {
    return url;
  }

  public String getRegexpFilterExpression() {
    return regexpFilterExpression;
  }

  public String getRegexpFilterText() {
    return regexpFilterText;
  }
}
