package com.api.storemanagement.operations.products;

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
@RequestMapping("/products")
public class ProductController {

	private final ProductService productService;

	@Autowired
	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping("/admin/add")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> addProduct(@RequestBody Product product) {
		Product addedProduct = productService.addProduct(product);
		return ResponseEntity.ok("Product " + addedProduct.getName() + " added successfully, having ID: " + addedProduct.getId());
	}

	@GetMapping("/public/{productId}")
	public ResponseEntity<Product> findProduct(@PathVariable Long productId) {
		Product product = productService.findProduct(productId);
		return ResponseEntity.ok(product);
	}

	@PutMapping("/admin/{productId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> updateProduct(@PathVariable Long productId, @RequestBody Product updatedProduct) {
		productService.updateProduct(productId, updatedProduct);
		return ResponseEntity.ok("Product updated successfully");
	}

	@PatchMapping("/admin/{productId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Product> patchProduct(@PathVariable Long productId, @RequestBody Product incompleteProduct) {
		Product updatedProduct = productService.patchProduct(productId, incompleteProduct);
		return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
	}

	@DeleteMapping("/admin/{productId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> removeProduct(@PathVariable Long productId) {
		productService.removeProduct(productId);
		return ResponseEntity.ok("Product removed successfully");
	}

	@GetMapping("/public/all")
	public ResponseEntity<List<Product>> listAllProducts() {
		List<Product> products = productService.listAllProducts();
		return ResponseEntity.ok(products);
	}

	@GetMapping("/public/expensive-low-stock")
	public ResponseEntity<List<Product>> getExpensiveLowStockProducts(
			@RequestParam("minPrice") double minPrice,
			@RequestParam("maxQuantity") int maxQuantity) {
		List<Product> products = productService.findExpensiveLowStockProducts(minPrice, maxQuantity);
		if (products.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(products);
	}
}
