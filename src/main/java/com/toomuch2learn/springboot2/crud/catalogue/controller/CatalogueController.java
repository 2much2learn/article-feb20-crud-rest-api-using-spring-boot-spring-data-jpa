package com.toomuch2learn.springboot2.crud.catalogue.controller;

import com.toomuch2learn.springboot2.crud.catalogue.exception.FileStorageException;
import com.toomuch2learn.springboot2.crud.catalogue.exception.ResourceNotFoundException;
import com.toomuch2learn.springboot2.crud.catalogue.model.CatalogueItem;
import com.toomuch2learn.springboot2.crud.catalogue.model.CatalogueItemList;
import com.toomuch2learn.springboot2.crud.catalogue.model.ResourceIdentity;
import com.toomuch2learn.springboot2.crud.catalogue.service.CatalogueCrudService;
import com.toomuch2learn.springboot2.crud.catalogue.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(CatalogueControllerAPIPaths.BASE_PATH)
public class CatalogueController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CatalogueCrudService catalogueCrudService;

    @GetMapping(CatalogueControllerAPIPaths.GET_ITEMS)
    @ResponseStatus(value = HttpStatus.OK)
    public CatalogueItemList getCatalogueItems() {
        return new CatalogueItemList(catalogueCrudService.getCatalogueItems());
    }

    @GetMapping(CatalogueControllerAPIPaths.GET_ITEM)
    public CatalogueItem
        getCatalogueItemBySKU(@PathVariable(value = "sku") String skuNumber)
            throws ResourceNotFoundException {

        return catalogueCrudService.getCatalogueItem(skuNumber);
    }

    @PostMapping(CatalogueControllerAPIPaths.CREATE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<ResourceIdentity> addCatalogueItem(@Valid @RequestBody CatalogueItem catalogueItem) {

        String id = catalogueCrudService.addCatalogItem(catalogueItem);

        return new ResponseEntity<>(new ResourceIdentity(id), HttpStatus.CREATED) ;
    }

    @PutMapping(CatalogueControllerAPIPaths.UPDATE)
    @ResponseStatus(value = HttpStatus.OK)
    public void updateCatalogueItem(
        @PathVariable(value = "sku") String skuNumber,
        @Valid @RequestBody CatalogueItem catalogueItem) throws ResourceNotFoundException {

        catalogueCrudService.updateCatalogueItem(catalogueItem);
    }

    @DeleteMapping(CatalogueControllerAPIPaths.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeCatalogItem(@PathVariable(value = "sku") String skuNumber)
        throws ResourceNotFoundException {

        catalogueCrudService.deleteCatalogueItem(catalogueCrudService.getCatalogueItem(skuNumber));
    }

    @PostMapping(CatalogueControllerAPIPaths.UPLOAD_IMAGE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public void uploadCatalogueItemImage(
        @PathVariable(value = "sku") String skuNumber,
        @RequestParam("file") MultipartFile file)
            throws ResourceNotFoundException, FileStorageException {

        catalogueCrudService.getCatalogueItem(skuNumber);

        fileStorageService.storeFile(file);
    }
}
