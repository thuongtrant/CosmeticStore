package com.ttt.CosmeticStore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Lob
    @Column(name = "benefits")
    private String benefits;

    @Lob
    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "how_to_use")
    private String howToUse;

    @NotNull
    @Column(name = "inventory", nullable = false)
    private Integer inventory;

    @NotNull
    @Column(name = "is_best_seller", nullable = false)
    private Boolean isBestSeller = false;

    @NotNull
    @Column(name = "is_new", nullable = false)
    private Boolean isNew = false;

    @Size(max = 500)
    @Column(name = "name", length = 500)
    private String name;

    @Column(name = "price", precision = 38, scale = 2)
    private BigDecimal price;

    @Size(max = 100)
    @Column(name = "type", length = 100)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

}