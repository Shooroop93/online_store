package com.example.order_management_system.e2e;

import com.example.order_management_system.constants.OrdersStatus;
import com.example.order_management_system.dto.items.request.ItemRequest;
import com.example.order_management_system.dto.items.response.Item;
import com.example.order_management_system.dto.items.response.ItemsResponse;
import com.example.order_management_system.dto.order.response.OrderResponse;
import com.example.order_management_system.dto.registration.request.RegistrationRequest;
import com.example.order_management_system.dto.registration.response.RegistrationResponse;
import com.example.order_management_system.dto.shopping_cart.request.ShoppingCartItem;
import com.example.order_management_system.dto.shopping_cart.request.ShoppingCartRequest;
import com.example.order_management_system.dto.shopping_cart.response.ItemShoppingCart;
import com.example.order_management_system.dto.shopping_cart.response.ShoppingCartResponse;
import com.example.order_management_system.model.Customer;
import com.example.order_management_system.model.Product;
import com.example.order_management_system.repository.ProductRepository;
import com.example.order_management_system.service.CustomerService;
import com.example.order_management_system.service.ProductService;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ImportAutoConfiguration(exclude = KafkaAutoConfiguration.class)
public class OnlineStoreE2ETest {

    @Autowired
    private SpringLiquibase liquibase;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductService productService;

    @LocalServerPort
    int randomServerPort;

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");
    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    public void cleanDB() throws LiquibaseException {
        liquibase.afterPropertiesSet();
    }

    public RestClient restClientInstance() {
        return RestClient.create();
    }

    public String getUrl() {
        return "http://localhost:" + randomServerPort;
    }

    @Test
    public void e2e_valid() {
        RegistrationRequest registrationClientOne = new RegistrationRequest(
                "test1", "testov1", "1234@test.test", "test1 456 789", "+7900000000111");
        RegistrationRequest registrationClientTwo = new RegistrationRequest(
                "test2", "testov2", "5678@test.test", "test2 123 456", "+12300000000321");
        ResponseEntity<RegistrationResponse> client1 = createClient(registrationClientOne);
        ResponseEntity<RegistrationResponse> client2 = createClient(registrationClientTwo);

        checkStatusCode(client1.getStatusCode().value(), 201);
        checkStatusCode(client2.getStatusCode().value(), 201);

        checkThatTheClientsDatIsSavedWhenRegistering(client1, registrationClientOne);
        checkThatTheClientsDatIsSavedWhenRegistering(client2, registrationClientTwo);

        ItemRequest itemOneForClientOne = new ItemRequest(client1.getBody().getId(), "Phone", "very cool phone", BigDecimal.valueOf(123.323), 1);
        ItemRequest itemTwoForClientOne = new ItemRequest(client1.getBody().getId(), "CPU", "CPU", BigDecimal.valueOf(0.1), 999999999);
        ItemRequest itemThreeForClientOne = new ItemRequest(client1.getBody().getId(), "Monitor", "m", BigDecimal.valueOf(123.323), 5);

        ItemRequest itemOneForClientTwo = new ItemRequest(client2.getBody().getId(), "notebook", "very cool notebook", BigDecimal.valueOf(123456), 8);

        ResponseEntity<ItemsResponse> addedItemForClient1 = addedItemForClient(itemOneForClientOne);
        ResponseEntity<ItemsResponse> addedItemForClient2 = addedItemForClient(itemTwoForClientOne);
        ResponseEntity<ItemsResponse> addedItemForClient3 = addedItemForClient(itemThreeForClientOne);
        ResponseEntity<ItemsResponse> addedItemForClient4 = addedItemForClient(itemOneForClientTwo);

        checkStatusCode(addedItemForClient1.getStatusCode().value(), 201);
        checkStatusCode(addedItemForClient2.getStatusCode().value(), 201);
        checkStatusCode(addedItemForClient3.getStatusCode().value(), 201);
        checkStatusCode(addedItemForClient4.getStatusCode().value(), 201);

        List<ItemRequest> itemsForClientOneList = new ArrayList<>();
        itemsForClientOneList.add(itemOneForClientOne);
        itemsForClientOneList.add(itemTwoForClientOne);
        itemsForClientOneList.add(itemThreeForClientOne);

        List<ItemRequest> itemsForClientTwoList = new ArrayList<>();
        itemsForClientTwoList.add(itemOneForClientTwo);

        checkTheRetentionOfProductsWithTheCustomer(client1.getBody().getId(), itemsForClientOneList);
        checkTheRetentionOfProductsWithTheCustomer(client2.getBody().getId(), itemsForClientTwoList);

        ShoppingCartRequest shoppingCartRequestClientTwo = new ShoppingCartRequest();
        shoppingCartRequestClientTwo.setIdOwnerShoppingCart(client2.getBody().getId());
        shoppingCartRequestClientTwo.addItem(new ShoppingCartItem(client1.getBody().getId(), 1, 1));
        shoppingCartRequestClientTwo.addItem(new ShoppingCartItem(client1.getBody().getId(), 2, 999999998));
        shoppingCartRequestClientTwo.addItem(new ShoppingCartItem(client1.getBody().getId(), 3, 3));

        ResponseEntity<ShoppingCartResponse> addedItemInShoppingCart = addedItemInShoppingCart(shoppingCartRequestClientTwo);
        checkStatusCode(addedItemInShoppingCart.getStatusCode().value(), 201);

        ResponseEntity<ShoppingCartResponse> itemsFromShoppingCart = getItemsFromShoppingCart(client2.getBody().getId());
        checkStatusCode(itemsFromShoppingCart.getStatusCode().value(), 200);
        checkItemFromShoppingCartResponse(itemsFromShoppingCart, itemsForClientOneList);

        ResponseEntity<OrderResponse> orderResponse = createOrder(itemsFromShoppingCart.getBody());
        checkStatusCode(orderResponse.getStatusCode().value(), 201);

        checkOrderResponse(itemsFromShoppingCart.getBody(), shoppingCartRequestClientTwo, orderResponse, OrdersStatus.PENDING);
        checkCountProductInDBPostDeal(itemsFromShoppingCart, itemsForClientOneList);

        RestClient.RequestHeadersSpec<?> requestHeadersSpec = cancelledOrder(orderResponse.getBody().getOrderNumber());
        checkStatusCode(requestHeadersSpec.retrieve().toBodilessEntity().getStatusCode().value(), 200);
        checkCountProductInDBPostCancelled(itemsFromShoppingCart, itemsForClientOneList);
    }

    private void checkCountProductInDBPostCancelled(ResponseEntity<ShoppingCartResponse> itemsFromShoppingCart, List<ItemRequest> itemsForClientOneList) {
        itemsFromShoppingCart.getBody().getItemList().sort(Comparator.comparing(ItemShoppingCart::getItemName));
        itemsForClientOneList.sort(Comparator.comparing(ItemRequest::getItemName));

        for (int i = 0; i < itemsFromShoppingCart.getBody().getItemList().size(); i++) {
            Product product = productRepository.findById(itemsFromShoppingCart.getBody().getItemList().get(i).getArticle()).get();
            int expectedCountProduct = itemsForClientOneList.get(i).getQuantity();
            assertEquals(product.getQuantity(), expectedCountProduct);
        }
    }

    private RestClient.RequestHeadersSpec<?> cancelledOrder(int idOwnerShoppingCart) {
        return restClientInstance().delete()
                .uri(getUrl() + "/api/v1/store/order/remove/{id}", idOwnerShoppingCart);
    }

    private void checkCountProductInDBPostDeal(ResponseEntity<ShoppingCartResponse> itemsFromShoppingCart, List<ItemRequest> itemsForClientOneList) {
        itemsFromShoppingCart.getBody().getItemList().sort(Comparator.comparing(ItemShoppingCart::getItemName));
        itemsForClientOneList.sort(Comparator.comparing(ItemRequest::getItemName));

        for (int i = 0; i < itemsFromShoppingCart.getBody().getItemList().size(); i++) {
            Product product = productRepository.findById(itemsFromShoppingCart.getBody().getItemList().get(i).getArticle()).get();
            int expectedCountProduct = itemsForClientOneList.get(i).getQuantity() - itemsFromShoppingCart.getBody().getItemList().get(i).getQuantity();
            assertEquals(product.getQuantity(), expectedCountProduct);
        }
    }


    private void checkOrderResponse(ShoppingCartResponse shoppingCartResponse, ShoppingCartRequest shoppingCartRequestClientTwo, ResponseEntity<OrderResponse> orderResponse, OrdersStatus ordersStatus) {
        OrderResponse bodyResponse = orderResponse.getBody();
        assertEquals(bodyResponse.getIdOwnerShoppingCart(), shoppingCartResponse.getIdOwnerShoppingCart());

        BigDecimal totalAmountExpected = BigDecimal.ZERO;
        List<ShoppingCartItem> itemsList = shoppingCartRequestClientTwo.getItemsList();

        itemsList.sort(Comparator.comparing(ShoppingCartItem::getArticle));
        shoppingCartResponse.getItemList().sort(Comparator.comparing(ItemShoppingCart::getArticle));

        for (int i = 0; i < itemsList.size(); i++) {
            totalAmountExpected = totalAmountExpected.add(shoppingCartResponse.getItemList().get(i).getPrice().multiply(BigDecimal.valueOf(itemsList.get(i).getQuantity())));
        }

        assertEquals(bodyResponse.getTotalAmount(), totalAmountExpected);
        assertEquals(bodyResponse.getStatus(), ordersStatus.name());
    }

    private ResponseEntity<OrderResponse> createOrder(ShoppingCartResponse body) {
        return restClientInstance().post()
                .uri(getUrl() + "/api/v1/store/order/create")
                .contentType(APPLICATION_JSON)
                .body(body).retrieve().toEntity(OrderResponse.class);
    }

    private void checkItemFromShoppingCartResponse(ResponseEntity<ShoppingCartResponse> itemsFromShoppingCart, List<ItemRequest> itemsForClientOneList) {
        itemsFromShoppingCart.getBody().getItemList().sort(Comparator.comparing(ItemShoppingCart::getItemName));
        itemsForClientOneList.sort(Comparator.comparing(ItemRequest::getItemName));

        for (int i = 0; i < itemsForClientOneList.size(); i++) {
            assertEquals(itemsFromShoppingCart.getBody().getItemList().get(i).getItemName(), itemsForClientOneList.get(i).getItemName());
            assertEquals(itemsFromShoppingCart.getBody().getItemList().get(i).getPrice(), itemsForClientOneList.get(i).getPrice());
            assertEquals(itemsFromShoppingCart.getBody().getItemList().get(i).getDescription(), itemsForClientOneList.get(i).getDescription());
            assertEquals(itemsFromShoppingCart.getBody().getItemList().get(i).getOwnerId(), itemsForClientOneList.get(i).getOwnerId());
            assertEquals(itemsFromShoppingCart.getBody().getItemList().get(i).getOwnerId(), itemsForClientOneList.get(i).getOwnerId());
        }
    }

    private ResponseEntity<ShoppingCartResponse> getItemsFromShoppingCart(int id) {
        return restClientInstance().get()
                .uri(getUrl() + "/api/v1/store/items/shopping_cart/getAllItem/{id}", id)
                .retrieve().toEntity(ShoppingCartResponse.class);
    }

    private ResponseEntity<ShoppingCartResponse> addedItemInShoppingCart(ShoppingCartRequest shoppingCartRequestClientTwo) {
        return restClientInstance().post()
                .uri(getUrl() + "/api/v1/store/items/shopping_cart/addItem")
                .contentType(APPLICATION_JSON)
                .body(shoppingCartRequestClientTwo).retrieve().toEntity(ShoppingCartResponse.class);
    }


    private void checkStatusCode(int actualStatusCode, int expectedStatusCode) {
        assertEquals(actualStatusCode, expectedStatusCode);
    }

    private void checkTheRetentionOfProductsWithTheCustomer(int ownerID, List<ItemRequest> itemRequestList) {
        ItemsResponse itemsResponseDB = productService.findById(ownerID, Locale.ENGLISH);
        assertTrue(Objects.isNull(itemsResponseDB.getError()));

        itemRequestList.sort(Comparator.comparing(ItemRequest::getItemName));
        itemsResponseDB.getItemList().sort(Comparator.comparing(Item::getItemName));

        for (int i = 0; i < itemsResponseDB.getItemList().size(); i++) {
            assertEquals(itemRequestList.get(i).getItemName(), itemsResponseDB.getItemList().get(i).getItemName());
            assertEquals(itemRequestList.get(i).getQuantity(), itemsResponseDB.getItemList().get(i).getQuantity());
            assertEquals(itemRequestList.get(i).getPrice(), itemsResponseDB.getItemList().get(i).getPrice());
            assertEquals(itemRequestList.get(i).getDescription(), itemsResponseDB.getItemList().get(i).getDescription());
            assertEquals(itemRequestList.get(i).getOwnerId(), itemsResponseDB.getItemList().get(i).getOwnerId());
        }
    }

    private ResponseEntity<ItemsResponse> addedItemForClient(ItemRequest itemRequest) {
        return restClientInstance().post()
                .uri(getUrl() + "/api/v1/store/items/addItem")
                .contentType(APPLICATION_JSON)
                .body(itemRequest)
                .retrieve()
                .toEntity(ItemsResponse.class);
    }

    private ResponseEntity<RegistrationResponse> createClient(RegistrationRequest registrationRequest) {
        return restClientInstance().post()
                .uri(getUrl() + "/api/v1/store/client/reg")
                .contentType(APPLICATION_JSON)
                .body(registrationRequest).retrieve().toEntity(RegistrationResponse.class);
    }

    private void checkThatTheClientsDatIsSavedWhenRegistering(ResponseEntity<RegistrationResponse> client, RegistrationRequest registrationRequest) {
        assertNotNull(client.getBody());
        assertTrue(client.getBody().getId() != -1);

        Optional<Customer> customerDB = customerService.findById(client.getBody().getId());

        assertTrue(customerDB.isPresent());
        assertEquals(customerDB.get().getFirstName(), registrationRequest.getName());
        assertEquals(customerDB.get().getLastName(), registrationRequest.getSurname());
        assertEquals(customerDB.get().getPhone(), registrationRequest.getPhone());
        assertEquals(customerDB.get().getEmail(), registrationRequest.getEmail());
        assertEquals(customerDB.get().getAddress(), registrationRequest.getAddress());
    }
}