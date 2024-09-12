import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

  public static void addUserAgentToBody(Response response, Request request) {
    response.setBody(request.GetHeader("User-Agent"));
  }

  public static void handleEmptyPath(Response response, Request request) {}

  public static void handleWithWildCards(Response response, Request request) {
    response.setBody(request.getParam("str"));
  }

  public static void sendFile(Response response, Request request) {
    String filename = request.getParam("file");
    try {
      String content = new String(
          Files.readAllBytes(Paths.get(request.getFilespath() + "/" + filename)));
      response.setBody(content);
      response.setContentType("application/octet-stream");

    } catch (IOException e) {
      System.out.println(e);
      response.setStatus("404 Not Found");
      response.setBody("File not found");
    }
  }

  public static void postFile(Response response, Request request) {
    try {
      File file = new File(request.getFilespath() + "/" + request.getParam("file"));
      file.createNewFile();
      FileWriter writer = new FileWriter(file);
      writer.write(request.GetBody());
      writer.close();
      response.setStatus("201 Created");
    } catch (IOException e) {
      response.setStatus("500 Internal Server Error");
      response.setBody("Internal server error");
      System.out.println(e);
    }
  }

  public static void main(String[] args) {
    Server server = new Server(4221);

    if (args.length == 2) {
      server.getHttpRequest().setFilespath(args[1]);
    }

    server.registerRoute(
        "GET /", (response, request) -> handleEmptyPath(response, request));
    server.registerRoute(
        "GET /echo/:str",
        (response, request) -> handleWithWildCards(response, request));
    server.registerRoute(
        "GET /user-agent",
        (response, request) -> addUserAgentToBody(response, request));
    server.registerRoute("GET /files/:file",
                         (response, request) -> sendFile(response, request));
    server.registerRoute("POST /files/:file", (response,request) -> postFile(response,request));

    server.ListenAndServe();
  }
}
