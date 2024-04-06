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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

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
				.andExpect(jsonPath("$[0].id", Matchers.is(1)));
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
	}
}
