package com.toomuch2learn.springboot2.crud.catalogue.service;

import com.toomuch2learn.springboot2.crud.catalogue.exception.ResourceNotFoundException;
import com.toomuch2learn.springboot2.crud.catalogue.model.CatalogueItem;
import com.toomuch2learn.springboot2.crud.catalogue.model.Category;
import com.toomuch2learn.springboot2.crud.catalogue.repository.CatalogueRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.mockito.BDDMockito.*;

public class CatalogueCrudServiceTest {

    @InjectMocks
    private CatalogueCrudService catalogueCrudService;

    @Mock
    private CatalogueRepository catalogueRepository;

    private final String skuNumber = "SKUNUMBER-1234";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddCatalogueItem() {
        try {
            CatalogueItem item = prepareCatalogueItem(skuNumber);
            item.setId(Long.valueOf(1));

            given(catalogueRepository.save(any())).willReturn(item);

            Assertions.assertEquals(
                catalogueCrudService.addCatalogItem(prepareCatalogueItem(skuNumber)),
                Long.valueOf(1)
            );
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while creating catalogue item", e);
        }
    }

    @Test
    public void testGetCatalogueItems() {
        try {
            CatalogueItem catalogueItem = prepareCatalogueItem(skuNumber);
            given(catalogueRepository.findAll(any(Sort.class))).willReturn(Arrays.asList(catalogueItem));

            Assertions.assertTrue(!CollectionUtils.isEmpty(catalogueCrudService.getCatalogueItems()));
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while getting catalogue items", e);
        }
    }

    @Test
    public void testGetCatalogueItem() {
        try {
            CatalogueItem catalogueItem = prepareCatalogueItem(skuNumber);
            given(catalogueRepository.findBySku(skuNumber)).willReturn(Optional.ofNullable(catalogueItem));

            Assertions.assertTrue(catalogueCrudService.getCatalogueItem(skuNumber) != null);
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while getting catalogue item", e);
        }
    }

    @Test
    public void testGetCatalogueItemResourceNotFound() {
        try {
            CatalogueItem catalogueItem = prepareCatalogueItem(skuNumber);
            given(catalogueRepository.findBySku(skuNumber)).willReturn(Optional.empty());

            catalogueCrudService.getCatalogueItem(skuNumber);

            Assertions.fail("Resource not found exception is not thrown");
        }
        catch(ResourceNotFoundException e) {
            Assertions.assertTrue(true, "Test is successful as Resource not found exception is thrown for sku number which is not available");
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while verifying catalogue not found ", e);
        }
    }

    @Test
    public void testUpdateCatalogueItem() {
        try {
            CatalogueItem catalogueItem = prepareCatalogueItem(skuNumber);
            given(catalogueRepository.findBySku(skuNumber)).willReturn(Optional.ofNullable(catalogueItem));

            catalogueCrudService.updateCatalogueItem(catalogueItem);
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while updating catalogue item", e);
        }
    }

    @Test
    public void testDeleteCatalogueItem() {
        try {
            CatalogueItem catalogueItem = prepareCatalogueItem(skuNumber);
            doNothing().when(catalogueRepository).delete(any());

            catalogueCrudService.deleteCatalogueItem(catalogueItem);
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while deleting catalogue item ", e);
        }
    }

    private CatalogueItem prepareCatalogueItem(String skuNumber) {
        CatalogueItem item
            = CatalogueItem.of(
                skuNumber,
                "Catalog Item -"+skuNumber,
                "Catalog Desc - "+skuNumber,
                Category.BOOKS.getValue(),
                10.00,
                10,
                new Date()
        );
        return item;
    }
}
