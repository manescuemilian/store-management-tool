package com.api.storemanagement;

import com.api.storemanagement.operations.ProductService;
import com.api.storemanagement.operations.StoreController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StoreController.class)
public class TestStoreController {
	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProductService service;
}
