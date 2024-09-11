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
      this.server.setHttpRequest(request.toString());
      Request current_request = this.server.getHttpRequest();
      String route =
          current_request.getMethod() + " " + current_request.getURL();
      boolean routeExists = this.server.mathRoute(route);
      if (!routeExists) {
        String response = "HTTP/1.1 404 Not Found\r\n\r\n";
        this.socket.getOutputStream().write(response.getBytes("UTF-8"));
        this.socket.getOutputStream().flush();
        this.socket.close();
      } else {
        String response = "HTTP/1.1 200 OK\r\n"
                          + "Content-Type: text/plain\r\n"
                          + "Content-Length: 13\r\n"
                          + "\r\n"
                          + "Hello, world!";
        this.socket.getOutputStream().write(response.getBytes("UTF-8"));
        this.socket.getOutputStream().flush();
        this.socket.close();
      }

    } catch (IOException e) {
      System.err.println("Server error: " + e.getMessage());
    }
  }

  public HandleRequest(Socket socket, Server server) {
    this.socket = socket;
    this.server = server;
  }
}
