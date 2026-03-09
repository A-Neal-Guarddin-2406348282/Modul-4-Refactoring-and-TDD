package model;

import id.ac.ui.cs.advprog.eshop.model.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Order {
    String id;
    List<Product> products;
    Long orderTime;
    String author;
    @Setter
    String status;

    public Order(String id, List<Product> products, Long orderTime, String author) {

    }

    public Order(String id, List<Product> products, Long orderTime, String author, String status) {

    }
}
