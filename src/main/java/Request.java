import java.util.HashMap;

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
  private String URLParams;

  public void setMethod(String method) { this.method = method; }

  public String getMethod() { return method; }

  public String GetHeader(String key) { return this.headers.get(key); }

  public String GetBody() { return this.body; }

  public String getURL() { return URL; }

  public String getURLParams() { return URLParams; }

  public void setHeaders(HashMap<String, String> headers) {
    this.headers = headers;
  }
  public void setURL(String URL) { this.URL = URL; }
  public void setBody(String body) { this.body = body; }
  public void setURLParams(String URLParams) { this.URLParams = URLParams; }

  public int
  getCharacterUntilWhitespace(String request,
                              CharacterFromWhitespaceStrategy strategy) {
    int whitespace = request.indexOf(' ');
    String method = request.substring(0, whitespace);
    strategy.execute(this, method);
    return whitespace;
  }
}
