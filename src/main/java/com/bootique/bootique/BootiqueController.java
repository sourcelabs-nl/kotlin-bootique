package com.bootique.bootique;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BootiqueController {

    private ProductRepository productRepository;
    private BasketRepository basketRepository;

    public BootiqueController(ProductRepository productRepository, BasketRepository basketRepository) {
        this.productRepository = productRepository;
        this.basketRepository = basketRepository;
    }

    @GetMapping({"/", "/products"})
    public List<Product> products() {
        return productRepository.getProducts();
    }

    @GetMapping("/baskets/{id}")
    public Basket getBasket(@PathVariable("id") String id) {
        return basketRepository.getBasketById(id);
    }

    @PostMapping(path = "/baskets/{id}/items", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Basket addToBasket(@PathVariable("id") String id, @RequestBody OrderItem orderItem) {
        Product productById = productRepository.getProductById(orderItem.getProductId());
        Basket basket = basketRepository.getBasketById(id);
        basket.addOrderItem(new OrderItem(orderItem.getProductId(), orderItem.getQuantity(), productById.getListPrice()));
        return basket;
    }
}
