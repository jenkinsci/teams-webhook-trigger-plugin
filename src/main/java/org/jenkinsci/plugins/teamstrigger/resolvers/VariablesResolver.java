package org.jenkinsci.plugins.teamstrigger.resolvers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.jenkinsci.plugins.teamstrigger.GenericHeaderVariable;
import org.jenkinsci.plugins.teamstrigger.GenericRequestVariable;
import org.jenkinsci.plugins.teamstrigger.GenericVariable;

public class VariablesResolver {
  private List<GenericVariable> configuredGenericVariables = new ArrayList<>();
  private final List<GenericRequestVariable> configuredGenericRequestVariables;
  private final String incomingPostContent;
  private final Map<String, String[]> incomingParameterMap;
  private final RequestParameterResolver requestParameterResolver = new RequestParameterResolver();
  private final RequestHeaderResolver requestHeaderResolver = new RequestHeaderResolver();
  private final PostContentParameterResolver postContentParameterResolver =
      new PostContentParameterResolver();
  private final List<GenericHeaderVariable> configuredGenericHeaderVariables;
  private final Map<String, List<String>> incomingHeaders;
  private final String textSeparator;
  private final boolean requestFromTeams;

  public VariablesResolver(
      final Map<String, List<String>> incomingHeaders,
      final Map<String, String[]> incomingParameterMap,
      final String incomingPostContent,
      final List<GenericVariable> configuredGenericVariables,
      final List<GenericRequestVariable> configuredGenericRequestVariables,
      final List<GenericHeaderVariable> configuredGenericHeaderVariables,
      final String textSeparator,
      boolean requestFromTeams) {
    this.textSeparator = textSeparator;
    this.requestFromTeams = firstNotNull(requestFromTeams, false);
    this.incomingPostContent = firstNotNull(incomingPostContent, "");
    this.configuredGenericVariables =
        firstNotNull(configuredGenericVariables, new ArrayList<GenericVariable>());
    this.incomingParameterMap = firstNotNull(incomingParameterMap, new HashMap<String, String[]>());
    this.configuredGenericRequestVariables =
        firstNotNull(configuredGenericRequestVariables, new ArrayList<GenericRequestVariable>());
    this.configuredGenericHeaderVariables =
        firstNotNull(configuredGenericHeaderVariables, new ArrayList<GenericHeaderVariable>());
    this.incomingHeaders = incomingHeaders;
  }

  private <T> T firstNotNull(final T o1, final T o2) {
    if (o1 != null) {
      return o1;
    }
    return o2;
  }

  public Map<String, String> getVariables() {
    final Map<String, String> resolvedVariables = new TreeMap<>();
    resolvedVariables.putAll(
        requestHeaderResolver.getRequestHeaders(configuredGenericHeaderVariables, incomingHeaders));
    resolvedVariables.putAll(
        requestParameterResolver.getRequestParameters(
            configuredGenericRequestVariables, incomingParameterMap));
    resolvedVariables.putAll(
        postContentParameterResolver.getPostContentParameters(
            configuredGenericVariables, incomingPostContent, textSeparator, requestFromTeams));
    return resolvedVariables;
  }
}
