package com.toomuch2learn.springboot2.crud.catalogue.service;

import com.toomuch2learn.springboot2.crud.catalogue.exception.ResourceNotFoundException;
import com.toomuch2learn.springboot2.crud.catalogue.model.CatalogueItem;
import com.toomuch2learn.springboot2.crud.catalogue.repository.CatalogueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CatalogueCrudService {

    @Autowired
    private CatalogueRepository catalogueRepository;


    public List<CatalogueItem> getCatalogueItems() {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        return catalogueRepository.findAll(sort);
    }

    public CatalogueItem getCatalogueItem( String skuNumber) throws ResourceNotFoundException {
        return getCatalogueItemBySku(skuNumber);
    }

    public String addCatalogItem(CatalogueItem catalogueItem) {
        catalogueItem.setCreatedOn(new Date());

        return catalogueRepository.save(catalogueItem).getId();
    }

    public void updateCatalogueItem(CatalogueItem catalogueItem) throws ResourceNotFoundException{

        CatalogueItem catalogueItemfromDB = getCatalogueItemBySku(catalogueItem.getSku());

        catalogueItemfromDB.setName(catalogueItem.getName());
        catalogueItemfromDB.setDescription(catalogueItem.getDescription());
        catalogueItemfromDB.setPrice(catalogueItem.getPrice());
        catalogueItemfromDB.setInventory(catalogueItem.getInventory());
        catalogueItemfromDB.setUpdatedOn(new Date());

        catalogueRepository.save(catalogueItemfromDB);
    }

    public void deleteCatalogueItem(CatalogueItem catalogueItem) {
        catalogueRepository.delete(catalogueItem);
    }

    private CatalogueItem getCatalogueItemBySku(String skuNumber) throws ResourceNotFoundException {
        CatalogueItem catalogueItem = catalogueRepository.findBySku(skuNumber)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Catalogue Item not found for the provided SKU :: %s" , skuNumber)));

        return catalogueItem;
    }
}
