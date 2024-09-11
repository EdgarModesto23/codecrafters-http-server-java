import java.util.HashMap;

public class Response {
  private HashMap<String, String> headers;
  private String body;
  private String status;

  public String GetHeader(String key) { return this.headers.get(key); }

  public String getStatus() { return status; }

  public void setStatus(String status) { this.status = status; }

  public void setHeaders(HashMap<String, String> headers) {
    this.headers = headers;
  }

  public void setBody(String body) { this.body = body; }
  public String getBody() { return body; }

  public String toCLRF() { return ""; }
}
