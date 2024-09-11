import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class Server {
  private int port;
  private Response httpResponse;
  private Request httpRequest;
  private HashMap<String, BiConsumer<Response, Request>> routes;

  public Server(int port) {
    this.port = port;
    this.httpResponse = null;
    this.httpRequest = null;
    this.routes = new HashMap<>();
  }

  public boolean mathRoute(String key) {
    var func = this.routes.get(key);
    if (func == null) {
      return false;
    }
    func.accept(this.httpResponse, this.httpRequest);

    return true;
  }

  public Request getHttpRequest() { return this.httpRequest; }

  public Response getHttpResponse() { return this.httpResponse; }

  public void ListenAndServe() {
    try (ServerSocket serverSocket = new ServerSocket(this.port)) {
      System.out.println("Server is listening on port " + this.port);

      while (true) { // Accept a new client connection
        Socket socket = serverSocket.accept();
        System.out.println("Client connected");

        Runnable handler = new HandleRequest(socket, this);
        new Thread(handler).start();
      }
    } catch (IOException e) {
      System.err.println("Server error: " + e.getMessage());
    }
  }

  public void registerRoute(String route,
                            BiConsumer<Response, Request> handler) {
    this.routes.put(route, handler);
  }

  public void setHttpRequest(String request) {
    this.httpRequest = this.parseHttpRequest(request);
  }

  public Request parseHttpRequest(String request) {
    Request newRequest = new Request();
    int method_offset =
        newRequest.getCharacterUntilWhitespace(request, new SetMethod());
    int path_offset = newRequest.getCharacterUntilWhitespace(
        request.substring(method_offset + 1), new SetPath());
    int headers_offset = request.indexOf("\n");

    return newRequest;
  }
}
