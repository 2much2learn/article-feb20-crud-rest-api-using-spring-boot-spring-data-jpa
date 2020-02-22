package com.toomuch2learn.springboot2.crud.catalogue.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.toomuch2learn.springboot2.crud.catalogue.exception.ResourceNotFoundException;
import com.toomuch2learn.springboot2.crud.catalogue.model.CatalogueItem;
import com.toomuch2learn.springboot2.crud.catalogue.model.CatalogueItemList;
import com.toomuch2learn.springboot2.crud.catalogue.model.Category;
import com.toomuch2learn.springboot2.crud.catalogue.model.ResourceIdentity;
import com.toomuch2learn.springboot2.crud.catalogue.service.CatalogueCrudService;
import com.toomuch2learn.springboot2.crud.catalogue.service.FileStorageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class CatalogueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatalogueCrudService catalogueCrudService;

    @MockBean
    private FileStorageService fileStorageService;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void initAll() {

        // Initialize Jackson mapper to convert response json to object
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    @Test
    void testGetCatalogueItems() {
        try {
            List<CatalogueItem> catalogueItems = prepareCatalogueItems();
            given(catalogueCrudService.getCatalogueItems()).willReturn(catalogueItems);

            MvcResult result
                = this.mockMvc
                    .perform(get(CatalogueControllerAPIPaths.BASE_PATH + CatalogueControllerAPIPaths.GET_ITEMS))
                    .andExpect(status().isOk())
                    .andReturn();

            // Compare size of the list that is returned with the size of list that is expected
            Assertions.assertEquals(
                objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<CatalogueItemList>(){}).getData().size(),
                catalogueItems.size());
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while getting catalogue items", e);
        }
    }

    @Test
    void testGetCatalogueItem() {
        String skuNumber = "SKUNUMBER-1234";
        try {
            CatalogueItem catalogueItem = prepareCatalogueItem(skuNumber);
            given(catalogueCrudService.getCatalogueItem(skuNumber)).willReturn(catalogueItem);

            MvcResult result
                = this.mockMvc
                    .perform(get(CatalogueControllerAPIPaths.BASE_PATH + CatalogueControllerAPIPaths.GET_ITEM, skuNumber))
                    .andExpect(status().isOk())
                    .andReturn();

            // Compare response object skuNumber to the expected one
            Assertions.assertEquals(
                objectMapper.readValue(result.getResponse().getContentAsString(), CatalogueItem.class).getSku(),
                skuNumber
            );
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while getting catalogue items", e);
        }
    }

    @Test
    void testCreateCatalogueItem() {
        String skuNumber = "SKUNUMBER-1234";
        try {
            CatalogueItem catalogueItem = prepareCatalogueItem(skuNumber);
            given(catalogueCrudService.addCatalogItem(catalogueItem)).willReturn(Long.valueOf(1));

            MvcResult result
                = this.mockMvc
                    .perform(
                        post(CatalogueControllerAPIPaths.BASE_PATH + CatalogueControllerAPIPaths.CREATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(catalogueItem))
                    )
                    .andExpect(status().is(201))
                    .andReturn();

            // Compare response object skuNumber to the expected one
            Assertions.assertEquals(
                objectMapper.readValue(result.getResponse().getContentAsString(), ResourceIdentity.class).getId(),
                1
            );
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while creating catalogue item", e);
        }
    }

    @Test
    void testUpdateCatalogueItem() {
        String skuNumber = "SKUNUMBER-1234";
        try {
            CatalogueItem catalogueItem = prepareCatalogueItem(skuNumber);
            doNothing().when(catalogueCrudService).updateCatalogueItem(catalogueItem);

            this.mockMvc
                .perform(
                    put(CatalogueControllerAPIPaths.BASE_PATH + CatalogueControllerAPIPaths.UPDATE, skuNumber)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(catalogueItem))
                )
                .andExpect(status().is(200));
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while updating catalogue item", e);
        }
    }

    @Test
    void testDeleteCatalogueItem() {
        String skuNumber = "SKUNUMBER-1234";
        try {
            CatalogueItem catalogueItem = prepareCatalogueItem(skuNumber);
            doNothing().when(catalogueCrudService).deleteCatalogueItem(catalogueItem);

            this.mockMvc
                .perform(delete(CatalogueControllerAPIPaths.BASE_PATH + CatalogueControllerAPIPaths.UPDATE, skuNumber))
                .andExpect(status().is(204));
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while deleting catalogue item", e);
        }
    }

    @Test
    void testResourceNotFound() {
        String skuNumber = "SKUNUMBER-1234";
        try {
            CatalogueItem catalogueItem = prepareCatalogueItem(skuNumber);
            given(catalogueCrudService.getCatalogueItem(skuNumber)).willThrow(ResourceNotFoundException.class);

            MvcResult result
                = this.mockMvc
                    .perform(get(CatalogueControllerAPIPaths.BASE_PATH + CatalogueControllerAPIPaths.GET_ITEM, skuNumber))
                    .andExpect(status().is(404))
                    .andReturn();
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while getting catalogue item", e);
        }
    }

    @Test
    void testHandlerNotFound() {
        String skuNumber = "SKUNUMBER-1234";
        try {
            CatalogueItem catalogueItem = prepareCatalogueItem(skuNumber);

            MvcResult result
                = this.mockMvc
                    .perform(get(CatalogueControllerAPIPaths.BASE_PATH + CatalogueControllerAPIPaths.GET_ITEM + "/INVALID", skuNumber))
                    .andExpect(status().is(404))
                    .andReturn();
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while getting catalogue item", e);
        }
    }

    @Test
    void testValidationException() {
        String skuNumber = "SKUNUMBER-1234";
        try {
            CatalogueItem catalogueItem = prepareCatalogueItem(skuNumber);

            // Set null for few variables to throw validation exception
            catalogueItem.setDescription("");
            catalogueItem.setName("");

            given(catalogueCrudService.addCatalogItem(catalogueItem)).willReturn(Long.valueOf(1));

            MvcResult result
                = this.mockMvc
                    .perform(
                        post(CatalogueControllerAPIPaths.BASE_PATH + CatalogueControllerAPIPaths.CREATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(catalogueItem))
                    )
                    .andExpect(status().is(400))
                    .andReturn();
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while verifying validation exception", e);
        }
    }

    @Test
    void testRuntimeException() {
        String skuNumber = "SKUNUMBER-1234";
        try {
            CatalogueItem catalogueItem = prepareCatalogueItem(skuNumber);
            given(catalogueCrudService.addCatalogItem(catalogueItem)).willThrow(new NullPointerException());

            MvcResult result
                = this.mockMvc
                    .perform(
                        post(CatalogueControllerAPIPaths.BASE_PATH + CatalogueControllerAPIPaths.CREATE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(catalogueItem))
                    )
                    .andExpect(status().is(500))
                    .andReturn();
        }
        catch(Exception e) {
            Assertions.fail("Error occurred while verifying runtime exception", e);
        }

    }

    private List<CatalogueItem> prepareCatalogueItems() {
        final List<CatalogueItem> catalogueItems = new ArrayList<>();
        final Random random = new Random();

        IntStream
            .rangeClosed(1, 10)
            .forEach(i -> {
                catalogueItems.add(
                    prepareCatalogueItem("SKUNUMBER-"+random.ints(1000, 9999).findFirst().getAsInt())
                );
            });

        return catalogueItems;
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