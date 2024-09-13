import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Util {

  public static boolean comparePattern(String pattern, String key, Request request) {
    Pattern regex = Pattern.compile(
        "^" + pattern.replaceAll(":([a-zA-Z0-9]+)", "([^/]+)") + "$");
    Matcher matcher = regex.matcher(key);
    if (matcher.matches()) {
      HashMap<String, String> params = new HashMap<>();
      for (int i = 1; i <= matcher.groupCount(); i++) {
        params.put(pattern.substring(matcher.start(i) + 1), matcher.group(i));
      }
      request.setURLParams(params);
      return true;
    } else {
      return false;
    }
  }
}
