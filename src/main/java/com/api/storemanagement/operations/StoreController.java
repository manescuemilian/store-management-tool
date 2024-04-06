package com.api.storemanagement.operations;

import com.api.storemanagement.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for the store management tool
 */
@RestController
@RequestMapping("/api/products")
public class StoreController {

	private final ProductService productService;

	@Autowired
	public StoreController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping("/add")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> addProduct(@RequestBody Product product) {
		Product addedProduct = productService.addProduct(product);
		return ResponseEntity.ok("Product " + addedProduct.getName() + " added successfully, having ID: " + addedProduct.getId());
	}

	@GetMapping("/{productId}")
	public ResponseEntity<Product> findProduct(@PathVariable Long productId) {
		Product product = productService.findProduct(productId);
		return ResponseEntity.ok(product);
	}

	@PutMapping("/{productId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> updateProduct(@PathVariable Long productId, @RequestBody Product updatedProduct) {
		productService.updateProduct(productId, updatedProduct);
		return ResponseEntity.ok("Product updated successfully");
	}

	@PatchMapping("/{productId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Product> patchProduct(@PathVariable Long productId, @RequestBody Product incompleteProduct) {
		Product updatedProduct = productService.patchProduct(productId, incompleteProduct);
		return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
	}

	@DeleteMapping("/{productId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> removeProduct(@PathVariable Long productId) {
		productService.removeProduct(productId);
		return ResponseEntity.ok("Product removed successfully");
	}

	@GetMapping
	public ResponseEntity<List<Product>> listAllProducts() {
		List<Product> products = productService.listAllProducts();
		return ResponseEntity.ok(products);
	}
}
