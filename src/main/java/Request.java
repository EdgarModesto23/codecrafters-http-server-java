import java.util.HashMap;
import java.util.LinkedList;

interface CharacterFromWhitespaceStrategy {
  void execute(Request request, String data);
}

// Implement strategies
class SetMethod implements CharacterFromWhitespaceStrategy {
  @Override
  public void execute(Request request, String data) {
    request.setMethod(data);
  }
}

class SetPath implements CharacterFromWhitespaceStrategy {
  @Override
  public void execute(Request request, String data) {
    request.setURL(data);
  }
}

public class Request {
  private HashMap<String, String> headers;
  private String body;
  private String URL;
  private String method;
  private LinkedList<String> wildcards;
  private HashMap<String, String> URLParams;

  public Request() {
    this.headers = new HashMap<>();
    this.body = "";
    this.URL = "";
    this.method = "";
    this.wildcards = new LinkedList<>();
    this.URLParams = new HashMap<>();
  }


  public void addHeader(String key, String value) {
    this.headers.put(key, value);
  }

  public void setWildCard(String val) { this.wildcards.add(val); }

  public LinkedList<String> getWildcards() { return wildcards; }

  public void setMethod(String method) { this.method = method; }

  public String getMethod() { return method; }

  public String GetHeader(String key) { return this.headers.get(key); }

  public String GetBody() { return this.body; }

  public String getURL() { return URL; }

  public String getParam(String key) { return this.URLParams.get(key); }

  public HashMap<String, String> getParams() { return this.URLParams; }

  public void setHeaders(HashMap<String, String> headers) {
    this.headers = headers;
  }
  public void setURLParams(HashMap<String, String> uRLParams) {
    this.URLParams = uRLParams;
  }
  public void setURL(String URL) { this.URL = URL; }
  public void setBody(String body) { this.body = body; }
  public void addURLParam(String key, String val) {
    this.URLParams.put(key, val);
  }

  public int
  getCharacterUntilWhitespace(String request,
                              CharacterFromWhitespaceStrategy strategy) {
    int whitespace = request.indexOf(' ');
    String method = request.substring(0, whitespace);
    strategy.execute(this, method);
    return whitespace;
  }
}
