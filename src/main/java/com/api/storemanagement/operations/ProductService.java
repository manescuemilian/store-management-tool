package com.api.storemanagement.operations;

import com.api.storemanagement.product.Product;
import com.api.storemanagement.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	@Autowired
	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public Product addProduct(Product product) {
		return productRepository.save(product);
	}

	public Product findProduct(Long productId) {
		// Use findById() to fetch a product. Throw an exception if the product is not found.
		return productRepository.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
	}

	public Product updateProduct(Long productId, Product updatedProduct) {
		// Check if the product exists before attempting to update
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

		// Update the necessary fields.
		product.setName(updatedProduct.getName());
		product.setDescription(updatedProduct.getDescription());
		product.setPrice(updatedProduct.getPrice());
		product.setQuantity(updatedProduct.getQuantity());

		// Save the updated product
		return productRepository.save(product);
	}

	public void removeProduct(Long productId) {
		// Check if the product exists before attempting to delete
		if (!productRepository.existsById(productId)) {
			throw new RuntimeException("Product not found with id: " + productId);
		}
		productRepository.deleteById(productId);
	}

	public List<Product> listAllProducts() {
		return productRepository.findAll();
	}
}

