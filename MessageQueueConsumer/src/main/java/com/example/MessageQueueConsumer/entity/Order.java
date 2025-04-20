package com.example.MessageQueueConsumer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends Base{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String orderName;

    @Column(nullable = false)
    private BigDecimal orderAmount;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "uid", nullable = false)
    private User customer;

    @Column(nullable = false)
    private Instant orderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private Status status;


    public void softDelete() {
        setStatus(Status.DELETED);
    }
}