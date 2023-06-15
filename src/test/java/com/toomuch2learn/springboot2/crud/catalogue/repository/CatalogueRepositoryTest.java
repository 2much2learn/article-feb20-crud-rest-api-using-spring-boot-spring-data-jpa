package com.toomuch2learn.springboot2.crud.catalogue.repository;

import com.toomuch2learn.springboot2.crud.catalogue.exception.ResourceNotFoundException;
import com.toomuch2learn.springboot2.crud.catalogue.model.CatalogueItem;
import com.toomuch2learn.springboot2.crud.catalogue.model.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.CollectionUtils;

import java.util.Date;

@AutoConfigureDataMongo
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CatalogueRepositoryTest {

    @Autowired
    private CatalogueRepository catalogueRepository;

    // SKU number we use for testing
    private static final String skuNumber = "SKUNUMBER-1234";

    @Test
    public void testAddCatalogItem() {
        try {
            CatalogueItem item = prepareCatalogItem(skuNumber);

            item = catalogueRepository.save(item);

            Assertions.assertNotNull(item.getId());
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while adding catalogue item", e);
        }
    }

    @Test
    public void testGetCatalogueItems() {
        try {
            CatalogueItem item = prepareCatalogItem(skuNumber);

            catalogueRepository.save(item);

            Assertions.assertTrue(!CollectionUtils.isEmpty(catalogueRepository.findAll()));
        }
        catch(Exception e) {
            Assertions.fail("Error occurred getting adding catalogue items", e);
        }
    }

    @Test
    public void testGetCatalogueItem() {
        try {
            CatalogueItem item = prepareCatalogItem(skuNumber);

            catalogueRepository.save(item);

            Assertions.assertTrue(catalogueRepository.findBySku(skuNumber) != null);
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while getting catalogue item", e);
        }
    }

    @Test
    public void testGetCatalogItemNotFound() {
        try {
            catalogueRepository.findBySku(skuNumber+1)
                .orElseThrow(() -> new ResourceNotFoundException(
                    String.format("Catalogue Item not found for the provided SKU :: %s" , skuNumber)));

            Assertions.fail("Test case failed as this test should throw resource not found exception");
        }
        catch(ResourceNotFoundException e) {
            Assertions.assertTrue(true, "Resource not found caught and hence test is successful");
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while verifying catalogue not found", e);
        }
    }

    @Test
    public void testUpdateCatalogueItem() {
        try {
            CatalogueItem item = prepareCatalogItem(skuNumber);

            item = catalogueRepository.save(item);

            item.setPrice(Double.valueOf(100.00));
            catalogueRepository.save(item);

            item = catalogueRepository.findBySku(skuNumber).get();
            Assertions.assertEquals(Double.valueOf(100.00), item.getPrice());
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while updating catalogue item", e);
        }
    }

    @Test
    public void testDeleteCatalogueItem() {
        try {
            CatalogueItem item = prepareCatalogItem(skuNumber);

            item = catalogueRepository.save(item);

            catalogueRepository.delete(item);

            catalogueRepository.findBySku(skuNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                    String.format("Catalogue Item not found for the provided SKU :: %s" , skuNumber)));

            Assertions.assertTrue(false, "Test case failed as this test should throw resource not found exception");
        }
        catch(ResourceNotFoundException e) {
            Assertions.assertTrue(true, "Resource not found caught and hence test is successful");
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while deleting catalogue item", e);
        }
    }

    private CatalogueItem prepareCatalogItem(String skuNumber) {
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
