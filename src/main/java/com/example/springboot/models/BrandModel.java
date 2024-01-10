package com.example.springboot.models;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "TB_BRANDS")
public class BrandModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idBrand;
    private String name;

    public BrandModel(String name) {
        this.name = name;
    }

    public BrandModel() {
    }

    public UUID getIdBrand() {
        return idBrand;
    }

    public void setIdBrand(UUID idBrand) {
        this.idBrand = idBrand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
