package io.quarkus.qute.benchmark.data;

import java.math.BigDecimal;
import java.util.Date;

public class Item {

    private Long id;

    private String name;

    private BigDecimal price;

    private Date created;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean hasPriceDefined() {
        return price != null;
    }
    
    public boolean hasNoPrice() {
        return price == null;
    }

    public Date getCreated() {
        return created;
    }

    public String getDescription() {
        return description;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
