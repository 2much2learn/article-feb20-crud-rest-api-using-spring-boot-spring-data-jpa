package com.toomuch2learn.springboot2.crud.catalogue.repository;

import com.toomuch2learn.springboot2.crud.catalogue.model.CatalogueItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatalogueRepository extends MongoRepository<CatalogueItem, Long> {

    Optional<CatalogueItem> findBySku(String sku);
}
