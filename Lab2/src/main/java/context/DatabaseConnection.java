package context;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import models.Company;
import models.Game;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class DatabaseConnection {
    private final MongoCollection<Game> gameCollection;
    private final MongoCollection<Company> companyCollection;
    private final MongoClient mongoClient;

    public static final DatabaseConnection INSTANCE = new DatabaseConnection();

    private DatabaseConnection() {
        ConnectionString connectionString = new ConnectionString(System.getProperty("mongodb.uri"));
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();

        mongoClient = MongoClients.create(clientSettings);
        MongoDatabase db = mongoClient.getDatabase("tema2CC");
        gameCollection = db.getCollection("games", Game.class);
        companyCollection = db.getCollection("companies", Company.class);

    }

    public MongoCollection<Game> getGameCollection() {
        return gameCollection;
    }

    public MongoCollection<Company> getCompanyCollection() {
        return companyCollection;
    }
    public void close() {
        mongoClient.close();
    }
}
