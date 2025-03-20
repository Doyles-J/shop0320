package shop.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import shop.SupportServiceApplication;
import shop.domain.InventoryDecreased;
import shop.domain.InventoryIncreased;

@Entity
@Table(name = "Inventory_table")
@Data
//<<< DDD / Aggregate Root
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String price;

    private Integer qty;

    public static InventoryRepository repository() {
        InventoryRepository inventoryRepository = SupportServiceApplication.applicationContext.getBean(
            InventoryRepository.class
        );
        return inventoryRepository;
    }

    //<<< Clean Arch / Port Method
    public static void decreaseInventory(DeliveryStarted deliveryStarted) {
        repository().findById(Long.valueOf(deliveryStarted.getProductId())).ifPresent(inventory->{
            
            inventory.setQty(inventory.getQty()-deliveryStarted.getQty());
            repository().save(inventory);

            InventoryDecreased inventoryDecreased = new InventoryDecreased(inventory);
            inventoryDecreased.publishAfterCommit();

         });
        

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void increaseInventory(DeliveryCancelled deliveryCancelled) {
        repository().findById(Long.valueOf(deliveryCancelled.getProductId())).ifPresent(inventory->{
            
            inventory.setQty(inventory.getQty()+deliveryCancelled.getQty());
            repository().save(inventory);

            InventoryIncreased inventoryIncreased = new InventoryIncreased(inventory);
            inventoryIncreased.publishAfterCommit();

         });

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
