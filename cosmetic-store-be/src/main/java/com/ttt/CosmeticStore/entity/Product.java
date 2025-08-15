package com.ttt.CosmeticStore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @Column(name = "price", precision = 38, scale = 2)
    @NotNull(message = "Giá sản phẩm không được để trống")
    private BigDecimal price;

    @Size(max = 100)
    @Column(name = "type", length = 100)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "main_image_url")
    private String mainImageUrl; // Ảnh đại diện sản phẩm

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "product_ingredient",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<Ingredient> ingredients = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "product_skin_type",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "skin_type_id")
    )
    private List<SkinType> skinTypes = new ArrayList<>();

}