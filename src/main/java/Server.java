import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
  private int port;
  private Response httpResponse;
  private Request httpRequest;
  private HashMap<String, BiConsumer<Response, Request>> routes;

  public Server(int port) {
    this.port = port;
    this.httpResponse = new Response();
    this.httpRequest = new Request();
    this.routes = new HashMap<>();
  }

  public boolean matchRoute(String key) {

    for (Map.Entry<String, BiConsumer<Response, Request>> entry :
         this.routes.entrySet()) {
      String routePath = entry.getKey();
      Pattern pattern = Pattern.compile("^" + routePath.replaceAll(":([a-zA-Z0-9]+)", "([^/]+)") + "$");
      Matcher matcher = pattern.matcher(key);
      if (matcher.matches()) {
        this.getHttpRequest().setURLParams(new HashMap<>());
        // Extract values for named groups
        HashMap<String, String> params = new HashMap<>();
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    params.put(routePath.substring(matcher.start(i) + 1), matcher.group(i));
                }
        this.getHttpRequest().setURLParams(params);
        
        var func = entry.getValue();
        func.accept(this.httpResponse, this.httpRequest);
        return true;
      }
    }
    return false;
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

  public static String convertToRegex(String route) {
    String escapedRoute = Pattern.quote(route);

    String regex = escapedRoute.replaceAll(":(\\w+)", "([^/]+)");

    return "^" + regex + "$";
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
