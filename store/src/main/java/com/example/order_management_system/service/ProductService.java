package com.example.order_management_system.service;

import com.schemes.dto.items.request.ItemRequest;
import com.schemes.dto.items.response.Item;
import com.schemes.dto.items.response.ItemsResponse;
import com.example.order_management_system.model.Customer;
import com.example.order_management_system.model.Product;
import com.example.order_management_system.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CustomerService customerService;
    private final MessageSource messageSource;

    @Transactional(readOnly = true)
    public ItemsResponse findAll(Locale locale) {
        List<Customer> allCustomerList = customerService.findAll();
        ItemsResponse itemsResponse = new ItemsResponse();

        if (Objects.nonNull(allCustomerList)) {
            for (Customer customer : allCustomerList) {
                if (Objects.nonNull(customer.getProductsList()) && !customer.getProductsList().isEmpty()) {
                    for (Product product : customer.getProductsList()) {
                        Item item = createItem(customer, product);
                        itemsResponse.addItem(item);
                    }
                }
            }
        } else {
            String message = messageSource.getMessage("application.controller.db.customer.message.get.all.error", new Object[0], locale);
            itemsResponse.addError(message);
        }

        return itemsResponse;
    }

    @Transactional(readOnly = true)
    public ItemsResponse findById(int id, Locale locale) {
        ItemsResponse itemsResponse = new ItemsResponse();
        Optional<Customer> customer = customerService.findById(id);

        if (customer.isPresent() && Objects.nonNull(customer.get().getProductsList()) && !customer.get().getProductsList().isEmpty()) {
            Customer customerDB = customer.get();
            for (Product product : customerDB.getProductsList()) {
                Item item = createItem(customerDB, product);
                itemsResponse.addItem(item);
            }
        } else {
            String message = messageSource.getMessage("application.controller.db.product.message.get", new Object[0], locale);
            itemsResponse.addError(format(message, id));
        }
        return itemsResponse;
    }

    @Transactional
    public void save(ItemRequest product) {
        Customer customerServiceById = customerService.findById(product.getOwnerId()).get();

        Product resultProduct = new Product();
        resultProduct.setOwnerProduct(customerServiceById);
        resultProduct.setItemName(product.getItemName());
        resultProduct.setDescription(product.getDescription());
        resultProduct.setPrice(product.getPrice());
        resultProduct.setQuantity(product.getQuantity());

        if (Objects.isNull(customerServiceById.getProductsList())) {
            customerServiceById.setProductsList(new ArrayList<>());
        }
        customerServiceById.getProductsList().add(resultProduct);

        productRepository.save(resultProduct);
    }

    @Transactional
    public void update(int id, Product updateProduct) {
        updateProduct.setId(id);
        productRepository.save(updateProduct);
    }

    @Transactional
    public void removeById(int id) {
        productRepository.deleteById(id);
    }

    private Item createItem(Customer customerDB, Product product) {
        Item item = new Item();
        item.setOwnerId(customerDB.getId());
        item.setArticle(product.getId());
        item.setItemName(product.getItemName());
        item.setDescription(product.getDescription());
        item.setPrice(product.getPrice());
        item.setQuantity(product.getQuantity());
        return item;
    }
}
