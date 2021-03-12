package models;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.Objects;

public class Game {
    private ObjectId id;

    @BsonProperty(value = "game_name")
    private String gameName;

    @BsonProperty(value = "release_year")
    private Integer releaseYear;

    private Category category;

    @BsonProperty (value = "company_name")
    private String companyName;
    // TODO: enum Category

    private enum Category {
        Sandbox, RTS, FPS, MOBA, RPG, Sports, Puzzle, Survival, Platformer
    }

    public Game setId(ObjectId id) {
        this.id = id;
        return this;
    }

    public Game setGameName(String gameName) {
        this.gameName = gameName;
        return this;
    }

    public Game setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
        return this;
    }

    public Game setCategory(Category category) {
        this.category = category;
        return this;
    }

    public Game setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public ObjectId getId() {
        return id;
    }

    public String getGameName() {
        return gameName;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public Category getCategory() {
        return category;
    }

    public String getCompanyName() {
        return companyName;
    }

    @Override
    public String toString() {
        return gameName + '(' + releaseYear + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(id, game.id) &&
                Objects.equals(gameName, game.gameName) &&
                Objects.equals(releaseYear, game.releaseYear) &&
                category == game.category &&
                Objects.equals(companyName, game.companyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, gameName, releaseYear, category, companyName);
    }
}
