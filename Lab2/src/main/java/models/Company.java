package models;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.Objects;

public class Company {

    private ObjectId id;

    @BsonProperty(value = "company_name")
    private String companyName;

    private String country;

    @BsonProperty(value = "foundation_year")
    private Integer foundationYear;

    public ObjectId getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCountry() {
        return country;
    }

    public Integer getFoundationYear() {
        return foundationYear;
    }

    public Company setId(ObjectId id) {
        this.id = id;
        return this;
    }

    public Company setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;

    }

    public Company setCountry(String country) {
        this.country = country;
        return this;

    }

    public Company setFoundationYear(Integer foundationYear) {
        this.foundationYear = foundationYear;
        return this;

    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", country='" + country + '\'' +
                ", foundationYear=" + foundationYear +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(id, company.id) &&
                Objects.equals(companyName, company.companyName) &&
                Objects.equals(country, company.country) &&
                Objects.equals(foundationYear, company.foundationYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, companyName, country, foundationYear);
    }
}
