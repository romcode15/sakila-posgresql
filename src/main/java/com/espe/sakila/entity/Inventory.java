package com.espe.sakila.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inventory")
@Data
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Integer inventoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    /**
     * store_id existe en pagila como NOT NULL.
     * Este sistema no gestiona tiendas, pero el campo debe estar
     * presente para pasar la validación del schema (ddl-auto=validate).
     */
    @Column(name = "store_id")
    private Integer storeId;
}
