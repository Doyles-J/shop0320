package shop.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import shop.OrderServiceApplication;
import shop.domain.OrderCancelled;
import shop.domain.OrderPlaced;

@Entity
@Table(name = "Order_table")
@Data
//<<< DDD / Aggregate Root
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String customerId;

    private String productId;

    private Integer price;

    private Integer qty;

    private String address;

    private String status;

    @PostPersist
    public void onPostPersist() {
        OrderPlaced orderPlaced = new OrderPlaced(this);
        orderPlaced.publishAfterCommit();

        OrderCancelled orderCancelled = new OrderCancelled(this);
        orderCancelled.publishAfterCommit();
    }

    public static OrderRepository repository() {
        OrderRepository orderRepository = OrderServiceApplication.applicationContext.getBean(
            OrderRepository.class
        );
        return orderRepository;
    }

    //<<< Clean Arch / Port Method
    public static void sendMail(InventoryIncreased inventoryIncreased) {
        repository().findById(inventoryIncreased.getId()).ifPresent(order->{
            order.setStatus("입고됨");
            repository().save(order);

         });

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void updateStatus(DeliveryStarted deliveryStarted) {
        //implement business logic here:

        repository().findById(Long.valueOf(deliveryStarted.getOrderId())).ifPresent(order->{
            
            order.setStatus("배송시작"); // do something
            repository().save(order);


         });
        

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void updateStatus(DeliveryCancelled deliveryCancelled) {
        repository().findById(Long.valueOf(deliveryCancelled.getOrderId())).ifPresent(order->{
            
            order.setStatus("배송취소"); // do something
            repository().save(order);


         });

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
