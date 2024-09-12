import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class HandleRequest implements Runnable {
  private Socket socket;
  private Server server;

  @Override
  public void run() {
    try {
      BufferedReader in = new BufferedReader(
          new InputStreamReader(this.socket.getInputStream()));
      StringBuilder request = new StringBuilder();
      String line;
      int contentLength = 0;
      while ((line = in.readLine()) != null && !line.isEmpty()) {
        request.append(line).append("\r\n");
        if (line.toLowerCase().startsWith("content-length:")) {
          contentLength = Integer.parseInt(line.split(":")[1].trim());
        }
      }
      if (contentLength > 0) {
        char[] body = new char[contentLength];
        in.read(body, 0, contentLength);
        request.append("\r\n").append(new String(body));
      }
      this.server.setHttpResponse(new Response());
      String filespath = this.server.getHttpRequest().getFilespath();
      this.server.setHttpRequest(request.toString());
      this.server.getHttpRequest().setFilespath(filespath);
      Request current_request = this.server.getHttpRequest();
      String route =
          current_request.getMethod() + " " + current_request.getURL();
      boolean routeExists = this.server.matchRoute(route);
      if (!routeExists) {
        String response = "HTTP/1.1 404 Not Found\r\n\r\n";
        this.socket.getOutputStream().write(response.getBytes("UTF-8"));
        this.socket.getOutputStream().flush();
        this.socket.close();
      }
      Response response = this.server.getHttpResponse();
      if (response.getStatus() == "") {
        response.setStatus("200 OK");
      }
      if (current_request.GetHeader("Accept-Encoding") != null) {
        if (current_request.GetHeader("Accept-Encoding").contains("gzip")) {
          response.addHeader("Content-Encoding", "gzip");
        }
      }
      String clrf_response = response.toCLRF();
      this.socket.getOutputStream().write(clrf_response.getBytes("UTF-8"));
      this.socket.getOutputStream().flush();
      this.socket.close();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  public HandleRequest(Socket socket, Server server) {
    this.socket = socket;
    this.server = server;
  }
}
