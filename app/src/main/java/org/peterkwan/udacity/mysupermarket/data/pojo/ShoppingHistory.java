package org.peterkwan.udacity.mysupermarket.data.pojo;

import org.peterkwan.udacity.mysupermarket.data.entity.ShoppingCartItem;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ShoppingHistory {

    private String userId;
    private Date purchaseDate;
    private String store;
    private Double totalPrice;
    private List<ShoppingCartItem> itemList;

}
