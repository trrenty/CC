package handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.MongoCollection;
import com.sun.net.httpserver.HttpExchange;
import models.Company;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;


public final class CompaniesHandler extends BaseHandler {
    @Override
    protected void handlePatch(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestURI().getQuery() != null) {
            sendBody(httpExchange, "Unsupported", 400);
            httpExchange.close();
        } else if (httpExchange.getRequestURI().getPath().chars().filter(ch -> ch == '/').count() > 1) {
            patchCompany(httpExchange);
        } else {
            sendBody(httpExchange, "Not allowed", 405);
            httpExchange.close();
        }
    }

    private void patchCompany(HttpExchange httpExchange) throws IOException {
        MongoCollection<Company> companyCollection = getContext().getCompanyCollection();
        String name;
        try {
            name = httpExchange.getRequestURI().getPath().split("/")[2];

        } catch (IndexOutOfBoundsException e) {
            sendBody(httpExchange, "Not allowed", 405);
            httpExchange.close();
            return;
        }
        Company oldComp = companyCollection.find(eq("company_name", name)).first();
        if (oldComp == null) {
            sendBody(httpExchange, "Not found", 404);
            httpExchange.close();
        } else {
            byte[] bodyBytes = httpExchange.getRequestBody().readAllBytes();

            String body = new String(bodyBytes, StandardCharsets.UTF_8);

            try {
                Company company = objectMapper.readValue(body, Company.class);
                System.out.println(company);
                company.setCompanyName(company.getCompanyName()== null ? oldComp.getCompanyName() : company.getCompanyName())
                .setFoundationYear(company.getFoundationYear() == null ? oldComp.getFoundationYear() : company.getFoundationYear())
                .setCountry(company.getCountry() == null     ? oldComp.getCountry() : company.getCountry());

                companyCollection.replaceOne(eq("company_name", name), company);
                sendBody(httpExchange, body, 200);
            } catch (JsonProcessingException e) {
                sendBody(httpExchange, "Bad json", 400);
            } finally {
                httpExchange.close();
            }
        }

    }

    @Override
    protected void handleDelete(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestURI().getQuery() != null) {
            sendBody(httpExchange, "Unsupported", 400);
            httpExchange.close();
        } else if (httpExchange.getRequestURI().getPath().chars().filter(ch -> ch == '/').count() > 1) {
            deleteCompany(httpExchange);
        } else {
            sendBody(httpExchange, "Not allowed", 405);
            httpExchange.close();
        }
    }

    private void deleteCompany(HttpExchange httpExchange) throws IOException {
        MongoCollection<Company> companyCollection = getContext().getCompanyCollection();
        String name;
        try {
            name = httpExchange.getRequestURI().getPath().split("/")[2];

        } catch (IndexOutOfBoundsException e) {
            sendBody(httpExchange, "Not allowed", 405);
            httpExchange.close();
            return;
        }

        if (companyCollection.find(eq("company_name", name)).first() == null) {
            sendBody(httpExchange, "Not found", 404);
            httpExchange.close();
        }

        companyCollection.deleteOne(eq("company_name", name));
        sendBody(httpExchange, "Deleted", 200);
        httpExchange.close();
    }

    @Override
    protected void handlePut(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestURI().getQuery() != null) {
            sendBody(httpExchange, "Unsupported", 400);
            httpExchange.close();
        } else if (httpExchange.getRequestURI().getPath().chars().filter(ch -> ch == '/').count() > 1) {
            putCompany(httpExchange);
        } else {
            sendBody(httpExchange, "Not allowed", 405);
            httpExchange.close();
        }
    }

    private void putCompany(HttpExchange httpExchange) throws IOException {
        MongoCollection<Company> companyCollection = getContext().getCompanyCollection();
        String name;
        try {
            name = httpExchange.getRequestURI().getPath().split("/")[2];

        } catch (IndexOutOfBoundsException e) {
            sendBody(httpExchange, "Not allowed", 405);
            httpExchange.close();
            return;
        }

        if (companyCollection.find(eq("company_name", name)).first() == null) {
            sendBody(httpExchange, "Not found", 404);
            httpExchange.close();
        } else {
            byte[] bodyBytes = httpExchange.getRequestBody().readAllBytes();

            String body = new String(bodyBytes, StandardCharsets.UTF_8);

            try {
                Company company = objectMapper.readValue(body, Company.class);
                if (company.hasNullValues()) {
                    sendBody(httpExchange, "Bad request", 400);
                } else {
                    companyCollection.replaceOne(eq("company_name", name), company);
                    sendBody(httpExchange, body, 200);
                }
            } catch (Exception e) {
                sendBody(httpExchange, "Bad json", 400);
            } finally {
                httpExchange.close();
            }
        }

    }

    @Override
    protected void handlePost(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestURI().getQuery() != null) {
            sendBody(httpExchange, "Unsupported", 400);
            httpExchange.close();
        } else if (httpExchange.getRequestURI().getPath().chars().filter(ch -> ch == '/').count() > 1) {
            sendBody(httpExchange, "Post only on /companies", 400);
            httpExchange.close();
        } else {
            postCompany(httpExchange);
        }
    }

    private void postCompany(HttpExchange httpExchange) throws IOException {
        MongoCollection<Company> companyCollection = getContext().getCompanyCollection();

        byte[] bodyBytes = httpExchange.getRequestBody().readAllBytes();

        String body = new String(bodyBytes, StandardCharsets.UTF_8);

        try {
            Company company = objectMapper.readValue(body, Company.class);
            Company dbCompany = companyCollection.find(eq("company_name", company.getCompanyName())).first();
            if (dbCompany != null) {
                sendBody(httpExchange, objectMapper.writeValueAsString(dbCompany), 409);
            } else if (company.hasNullValues()) {
                sendBody(httpExchange, "Bad request", 400);
            } else {
                companyCollection.insertOne(company);
                sendBody(httpExchange, body, 201);
            }

        } catch (Exception e) {
            sendBody(httpExchange, "Bad json", 400);
        } finally {
            httpExchange.close();
        }


    }

    @Override
    protected void handleGet(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestURI().getQuery() != null) {
            getCompaniesByQueryUrl(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().chars().filter(ch -> ch == '/').count() > 1) {
            getCompaniesByName(httpExchange);
        } else {
            getCompanies(httpExchange);
        }
    }

    private void getCompaniesByQueryUrl(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestURI().getQuery().contains("&")) {
            sendBody(httpExchange, "Unsupported", 400);
            httpExchange.close();
            return;
        }

        String[] query = httpExchange.getRequestURI().getQuery().split("=");

        String key = query[0];
        String value = query[1];

        getCompaniesByQuery(httpExchange, key, value);
    }

    private void getCompanies(HttpExchange httpExchange) throws IOException {
        MongoCollection<Company> companyCollection = getContext().getCompanyCollection();

        List<Company> companies = companyCollection.find().into(new ArrayList<>());
        try {
            if (companies.isEmpty()) {
                sendBody(httpExchange, "No content", 204);
            }
            else {
                String response = objectMapper.writeValueAsString(companies);
//            System.out.println(response);
                sendBody(httpExchange, response, 200);
            }

        } catch (JsonProcessingException e) {
            httpExchange.sendResponseHeaders(500, 0);
        } finally {
            httpExchange.close();
        }

    }

    private void getCompaniesByName(HttpExchange httpExchange) throws IOException {
        String name;
        try {
            name = httpExchange.getRequestURI().getPath().split("/")[2];
        } catch (IndexOutOfBoundsException e) {
            getCompanies(httpExchange);
            return;
        }

        getCompaniesByQuery(httpExchange, "company_name", name);
    }

    private void getCompaniesByQuery(HttpExchange httpExchange, String key, String query) throws IOException {
        MongoCollection<Company> companyCollection = getContext().getCompanyCollection();
        List<Company> companies;

        try {
            companies = companyCollection.find(eq(key, Integer.parseInt(query))).into(new ArrayList<>());
        } catch (NumberFormatException e) {
            companies = companyCollection.find(eq(key, query)).into(new ArrayList<>());
        }
        try {
            String response;
            if (companies.isEmpty()) {
                response = "";
            } else if (companies.size() == 1) {
                response = objectMapper.writeValueAsString(companies.get(0));
            } else
                response = objectMapper.writeValueAsString(companies);
//            System.out.println(response);
            System.out.println(response);
            if (response.isEmpty() || response.equals("null")) {
                sendBody(httpExchange, "Not found", 404);
            } else
                sendBody(httpExchange, response, 200);
        } catch (JsonProcessingException e) {
            httpExchange.sendResponseHeaders(500, 0);
        } finally {
            httpExchange.close();
        }

    }


}
