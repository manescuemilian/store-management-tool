package com.api.storemanagement;

import com.api.storemanagement.operations.ProductService;
import com.api.storemanagement.operations.StoreController;
import com.api.storemanagement.product.Product;
import com.api.storemanagement.security.ProductsSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StoreController.class)
@Import(ProductsSecurityConfig.class)
@ContextConfiguration
public class TestStoreController {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductService service;

	@Autowired
	ObjectMapper mapper;

	@Test
	@WithMockUser(value = "user", password = "password")
	public void testFindAll() throws Exception {
		Product product = new Product(1L, "test_product", 10, 10);
		List<Product> products = Arrays.asList(product);

		Mockito.when(service.listAllProducts()).thenReturn(products);

		mockMvc.perform(get("/products/public/all"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(1)))
				.andExpect(jsonPath("$[0].name", Matchers.is("test_product")))
				.andExpect(jsonPath("$[0].price", Matchers.is(10.0)))
				.andExpect(jsonPath("$[0].quantity", Matchers.is(10)))
				.andExpect(jsonPath("$[0].id", Matchers.is(1)))
				.andExpect(jsonPath("$").isArray());
		Mockito.verify(this.service, Mockito.times(1)).listAllProducts();
	}

	@Test
	@WithMockUser(value = "admin", password = "admin", roles = "ADMIN")
	public void testAddProductOk() throws Exception {
		Product product = new Product("test_product", 10, 10);
		Mockito.when(service.addProduct(Mockito.any(Product.class))).thenReturn(product);
		mockMvc.perform(post("/products/admin/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(product)))
				.andExpect(status().isOk());
		Mockito.verify(this.service, Mockito.times(1))
				.addProduct(Mockito.any(Product.class));
	}

	@Test
	@WithMockUser(value = "user", password = "password", roles = "USER")
	public void testAddProductForbidden() throws Exception {
		Product product = new Product("test_product", 10, 10);
		Mockito.when(service.addProduct(Mockito.any(Product.class))).thenReturn(product);
		mockMvc.perform(post("/products/admin/add")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(product)))
				.andExpect(status().isForbidden());
		Mockito.verify(this.service, Mockito.times(0))
				.addProduct(Mockito.any(Product.class));
	}

	@Test
	@WithMockUser(value = "admin", password = "admin", roles = "ADMIN")
	public void testUpdateProductOk() throws Exception {
		Long productIdToUpdate = 1L;
		Product updatedProduct = new Product(productIdToUpdate, "test_product_new",
								"New description", 15, 15);
		mockMvc.perform(put("/products/admin/" + productIdToUpdate.toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(updatedProduct)))
				.andExpect(status().isOk())
				.andExpect(content().string("Product updated successfully"));
		Mockito.verify(this.service, Mockito.times(1))
				.updateProduct(Mockito.any(Long.class), Mockito.any(Product.class));
	}

	@Test
	@WithMockUser(value = "admin", password = "admin", roles = "ADMIN")
	public void testRemoveProductOk() throws Exception {
		long productIdToRemove = 1L;
		mockMvc.perform(delete("/products/admin/" + productIdToRemove))
				.andExpect(status().isOk())
				.andExpect(content().string("Product removed successfully"));
		Mockito.verify(this.service, Mockito.times(1))
				.removeProduct(Mockito.any(Long.class));
	}

	@Test
	@WithMockUser(value = "user", password = "password", roles = "USER")
	public void testRemoveProductForbidden() throws Exception {
		long productIdToRemove = 1L;
		mockMvc.perform(delete("/products/admin/" + productIdToRemove))
				.andExpect(status().isForbidden());
		Mockito.verify(this.service, Mockito.times(0))
				.removeProduct(Mockito.any(Long.class));
	}

	@Test
	@WithMockUser(value = "user", password = "password", roles = "USER")
	public void testGetExpensiveLowStock() throws Exception {
		double minPrice = 20.0;
		int maxQuantity = 10;
		Product mockProduct = new Product(1L, "Expensive Product", "High-quality item", 25.0, 5);
		List<Product> mockProducts = Arrays.asList(mockProduct);

		Mockito.when(service.findExpensiveLowStockProducts(minPrice, maxQuantity))
				.thenReturn(mockProducts);

		mockMvc.perform(get("/products/public/expensive-low-stock")
						.param("minPrice", String.valueOf(minPrice))
						.param("maxQuantity", String.valueOf(maxQuantity)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].name", Matchers.is("Expensive Product")))
				.andExpect(jsonPath("$[0].description", Matchers.is("High-quality item")))
				.andExpect(jsonPath("$[0].price", Matchers.is(25.0)))
				.andExpect(jsonPath("$[0].quantity", Matchers.is(5)));
		Mockito.verify(this.service, Mockito.times(1))
				.findExpensiveLowStockProducts(Mockito.any(Double.class), Mockito.any(Integer.class));
	}
}
