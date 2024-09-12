public class Main {

  public static void handleEmptyPath(Response response, Request request) {}

  public static void handleWithWildCards(Response response, Request request) {
    response.setBody(request.getParam("str"));
  }

  public static void main(String[] args) {
    Server server = new Server(4221);

    server.registerRoute(
        "GET /", (response, request) -> handleEmptyPath(response, request));
    server.registerRoute(
        "GET /echo/:str", (response, request) -> handleWithWildCards(response, request));


    server.ListenAndServe();
  }
}
