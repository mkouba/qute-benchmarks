package io.quarkus.qute.benchmark.data;

public class Origin {

    public Origin(String country) {
        super();
        this.country = country;
    }

    private String country;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
