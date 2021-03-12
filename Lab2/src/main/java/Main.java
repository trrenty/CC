import com.sun.net.httpserver.HttpServer;
import handler.CompaniesHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {
    public static final String HOST_NAME = "localhost";
    public static final int PORT = 6969;
    public static void main(String[] args) {

        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(HOST_NAME, PORT), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Executor executor = Executors.newFixedThreadPool(10);
        if (server != null) {
            server.createContext("/companies", new CompaniesHandler());
            server.start();
        }
        else {
            System.out.println("server null");
        }

//
//        try {
//            Company company = new ObjectMapper().readValue("{\n" +
//                    "    \"companyName\" : \"Bethesta\",\n" +
//                    "    \"country\" : \"USA\"\n" +
//                    "}", Company.class);
//            System.out.println(company.getId() == null);
//            System.out.println(company.getCompanyName());
//            System.out.println(company.getFoundationYear());
//            System.out.println(company.getCountry());
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

//        DatabaseConnection.INSTANCE.close();



    }
}
