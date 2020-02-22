package com.toomuch2learn.springboot2.crud.catalogue;

import static com.toomuch2learn.springboot2.crud.catalogue.controller.CatalogueControllerAPIPaths.*;

import com.toomuch2learn.springboot2.crud.catalogue.model.CatalogueItem;
import com.toomuch2learn.springboot2.crud.catalogue.model.CatalogueItemList;
import com.toomuch2learn.springboot2.crud.catalogue.model.Category;
import com.toomuch2learn.springboot2.crud.catalogue.model.ResourceIdentity;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CatalogueCRUDIntegrationTest {

	private static TestRestTemplate restTemplate;
	private static HttpHeaders headers;

	@LocalServerPort
	private int port;

	@BeforeAll
	public static void setup() {
		restTemplate = new TestRestTemplate();

		headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + BASE_PATH + uri;
	}

	String skuNumber = "SKUNUMBER-1234";

	@Test
	@Order(10)
	public void testCreateCatalogueItem() {
		HttpEntity<CatalogueItem> entity = new HttpEntity<CatalogueItem>(prepareCatalogueItem(skuNumber), headers);
		ResponseEntity<ResourceIdentity> response
			= restTemplate.exchange(
				createURLWithPort(CREATE),
				HttpMethod.POST,
				entity,
				ResourceIdentity.class);

		ResourceIdentity resourceIdentity = response.getBody();

		Assertions.assertTrue(resourceIdentity != null && resourceIdentity.getId() != null && resourceIdentity.getId() != 0);
	}

	@Test
	@Order(20)
	@SuppressWarnings("unchecked")
	public void testGetCatalogueItems() {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<CatalogueItemList> response
			= restTemplate.exchange(
				createURLWithPort(GET_ITEMS),
				HttpMethod.GET,
				entity,
				CatalogueItemList.class);

		Assertions.assertTrue(!CollectionUtils.isEmpty(Arrays.asList(response.getBody().getData())));
	}

	@Test
	@Order(30)
	public void testUpdateCatalogueItem() {
		CatalogueItem catalogueItem = prepareCatalogueItem(skuNumber);
		catalogueItem.setPrice(100.00);
		catalogueItem.setInventory(10);

		HttpEntity<CatalogueItem> entity = new HttpEntity<CatalogueItem>(catalogueItem, headers);
		ResponseEntity<Void> response
			= restTemplate.exchange(
				createURLWithPort(UPDATE.replaceAll("\\{sku\\}", skuNumber)),
				HttpMethod.PUT,
				entity,
				Void.class);

		Assertions.assertEquals(response.getStatusCode().value(), 200);
	}

	@Test
	@Order(40)
	public void testGetCatalogueItem() {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<CatalogueItem> response
			= restTemplate.exchange(
				createURLWithPort(GET_ITEM.replaceAll("\\{sku\\}", skuNumber)),
				HttpMethod.GET,
				entity,
				CatalogueItem.class);

		Assertions.assertNotNull(response.getBody());
		Assertions.assertEquals(response.getBody().getPrice(), 100.00);
	}

	@Test
	@Order(50)
	public void testDeleteCatalogueItem() {

		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<Void> response
				= restTemplate.exchange(
				createURLWithPort(DELETE.replaceAll("\\{sku\\}", skuNumber)),
				HttpMethod.DELETE,
				entity,
				Void.class);

		Assertions.assertEquals(response.getStatusCode().value(), 204);
	}

	@Test
	@Order(60)
	public void testGetCatalogueItemResourceNotFound() {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<CatalogueItem> response
				= restTemplate.exchange(
				createURLWithPort(GET_ITEM.replaceAll("\\{sku\\}", skuNumber)),
				HttpMethod.GET,
				entity,
				CatalogueItem.class);

		Assertions.assertEquals(response.getStatusCode().value(), 404);
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
