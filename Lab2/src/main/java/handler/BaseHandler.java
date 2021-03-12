package handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import context.DatabaseConnection;

import java.io.IOException;

public abstract class BaseHandler implements HttpHandler {
    private DatabaseConnection context = DatabaseConnection.INSTANCE;
    protected static ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        switch (httpExchange.getRequestMethod()) {
            case "GET": handleGet(httpExchange); break;
            case "POST": handlePost(httpExchange); break;
            case "PUT": handlePut(httpExchange); break;
            case "DELETE": handleDelete(httpExchange); break;
            case "PATCH": handlePatch(httpExchange); break;
            default: invalidMethod(httpExchange);
        }
    }

    synchronized protected DatabaseConnection getContext() {
        return context;
    }

    protected abstract void handlePatch(HttpExchange httpExchange) throws IOException;

    protected abstract void handleDelete(HttpExchange httpExchange) throws IOException;

    protected abstract void handlePut(HttpExchange httpExchange) throws IOException;

    protected abstract void handlePost(HttpExchange httpExchange) throws IOException;

    protected abstract void handleGet(HttpExchange httpExchange) throws IOException;

    protected void invalidMethod(HttpExchange httpExchange) {
        try {
            System.out.println(httpExchange.getRequestURI().getQuery());
            System.out.println(httpExchange.getRequestURI().getAuthority());
            System.out.println(httpExchange.getRequestURI().getFragment());
            System.out.println(httpExchange.getRequestURI().getHost());
            System.out.println(httpExchange.getRequestURI().getPath());
            System.out.println(httpExchange.getRequestURI().getPort());
            System.out.println(httpExchange.getRequestURI().getRawAuthority());
            System.out.println(httpExchange.getRequestURI().getRawFragment());
            System.out.println(httpExchange.getRequestURI().getRawPath());
            System.out.println(httpExchange.getRequestURI().getUserInfo());
            System.out.println(httpExchange.getRequestURI().getScheme());




            httpExchange.sendResponseHeaders(405, 0);
        } catch (IOException e) {
            System.out.println("Unable to send request headers");
        }
        finally {
            httpExchange.close();
        }
    }
    protected void sendBody(HttpExchange httpExchange, String response, int responseCode) throws IOException {
        httpExchange.sendResponseHeaders(responseCode, response.getBytes().length);
        httpExchange.getResponseBody().write(response.getBytes());
        httpExchange.getResponseBody().close();
    }
}
