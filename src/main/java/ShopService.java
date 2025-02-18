import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ShopService {
    private ProductRepo productRepo = new ProductRepo();
    private OrderRepo orderRepo = new OrderMapRepo();

    public Order addOrder(List<String> productIds) {
        List<Product> products = new ArrayList<>();
        for (String productId : productIds) {
            Product productToOrder = productRepo.getProductById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product mit der Id: " + productId + " existiert nicht!"));
            products.add(productToOrder);
        }

        Order newOrder = new Order(UUID.randomUUID().toString(), products, OrderStatus.PROCESSING, Instant.now());

        return orderRepo.addOrder(newOrder);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepo.getOrders().stream()
                .filter(order -> order.status().equals(status))
                .collect(Collectors.toList());
    }

    public Order updateOrder(String orderId, OrderStatus newStatus) {
        Order orderToUpdate = orderRepo.getOrderById(orderId);
        if (orderToUpdate == null) {
            throw new IllegalArgumentException("Order mit der Id: " + orderId + " existiert nicht!");
        }
        Order updatedOrder = orderToUpdate.withStatus(newStatus);
        orderRepo.removeOrder(orderId);
        return orderRepo.addOrder(updatedOrder);
    }
}
