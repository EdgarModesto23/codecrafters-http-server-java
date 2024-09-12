import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class Response {
  private HashMap<String, String> headers;
  private String body;
  private String status;
  private String contentType;

  public Response() {
    this.headers = new HashMap<>();
    this.body = "";
    this.status = "";
    this.contentType = "text/plain";
  }

  public void addHeader(String key, String value) {
    this.headers.put(key, value);
  }

  public void setContentType(String value) { this.contentType = value; }

  public String GetHeader(String key) { return this.headers.get(key); }

  public String getStatus() { return status; }

  public void setStatus(String status) { this.status = status; }

  public String headersToCLRF() {
    StringBuilder result = new StringBuilder();

    // Iterate over the entries in the map
    for (Map.Entry<String, String> entry : this.headers.entrySet()) {
      result.append(entry.getKey())
          .append(": ")
          .append(entry.getValue())
          .append("\r\n"); // Append CRLF after each key-value pair
    }
    result.append(String.format("Content-Type: %s\r\n", this.contentType));
    if (this.body.length() > 0) {
      result.append(
          String.format("Content-Length: %s\r\n", this.body.length()));
    }

    return result.toString();
  }

  public void compressBody() {
    try {
    ByteArrayOutputStream obj=new ByteArrayOutputStream();
    GZIPOutputStream gzip = new GZIPOutputStream(obj);
    gzip.write(this.body.getBytes());
    gzip.close();
    String compressedBody = Base64.getEncoder().encodeToString(obj.toByteArray());
    this.setBody(compressedBody);
    } catch (IOException e) {
      System.out.println(e);
    }

  }

  public void setHeaders(HashMap<String, String> headers) {
    this.headers = headers;
  }

  public void setBody(String body) { this.body = body; }
  public String getBody() { return body; }

  public String toCLRF() {
    String response = String.format("HTTP/1.1 %s\r\n%s\r\n%s", this.status,
                                    this.headersToCLRF(), this.body);

    return response;
  }
}
