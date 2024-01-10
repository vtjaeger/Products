package com.example.springboot.repositories;

import com.example.springboot.models.BrandModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<BrandModel, UUID> {
    BrandModel findByName(String name);
    Page<BrandModel> findByActiveTrue(Pageable pageable);
}
