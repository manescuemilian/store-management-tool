package com.api.storemanagement.operations;

import com.api.storemanagement.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class StoreController {

	private final ProductService productService;

	@Autowired
	public StoreController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping
	public ResponseEntity<String> addProduct(@RequestBody Product product) {
		productService.addProduct(product);
		return ResponseEntity.ok("Product added successfully");
	}

	@GetMapping("/{productId}")
	public ResponseEntity<Product> findProduct(@PathVariable Long productId) {
		Product product = productService.findProduct(productId);
		return ResponseEntity.ok(product);
	}

	@PutMapping("/{productId}")
	public ResponseEntity<String> updateProduct(@PathVariable Long productId, @RequestBody Product updatedProduct) {
		productService.updateProduct(productId, updatedProduct);
		return ResponseEntity.ok("Product updated successfully");
	}

	@DeleteMapping("/{productId}")
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
