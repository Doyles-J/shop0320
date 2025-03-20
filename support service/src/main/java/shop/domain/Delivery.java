package shop.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import shop.SupportServiceApplication;
import shop.domain.DeliveryCancelled;
import shop.domain.DeliveryStarted;

@Entity
@Table(name = "Delivery_table")
@Data
//<<< DDD / Aggregate Root
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String orderId;

    private String address;

    private String customerId;

    private String productId;

    private Integer qty;

    private String status;

    public static DeliveryRepository repository() {
        DeliveryRepository deliveryRepository = SupportServiceApplication.applicationContext.getBean(
            DeliveryRepository.class
        );
        return deliveryRepository;
    }

    //<<< Clean Arch / Port Method
    public static void startDelivery(OrderPlaced orderPlaced) {
        Delivery delivery = new Delivery();
        repository().save(delivery);

        DeliveryStarted deliveryStarted = new DeliveryStarted(delivery);
        deliveryStarted.publishAfterCommit();
        

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void cancelDelivery(OrderCancelled orderCancelled) {
        repository().findById(Long.valueOf(orderCancelled.getId())).ifPresent(delivery->{
            
            delivery.setStatus("배송 취소"); // do something
            repository().save(delivery);

            DeliveryCancelled deliveryCancelled = new DeliveryCancelled(delivery);
            deliveryCancelled.publishAfterCommit();

         });

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
