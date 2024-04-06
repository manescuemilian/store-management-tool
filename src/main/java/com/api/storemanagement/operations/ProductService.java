package com.api.storemanagement.operations;

import com.api.storemanagement.exceptions.PatchErrorException;
import com.api.storemanagement.exceptions.ProductNotFoundException;
import com.api.storemanagement.product.Product;
import com.api.storemanagement.product.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	@Autowired
	ProductPatcher patcher;

	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

	@Autowired
	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
		logger.info("ProductService initialized");
	}

	public Product addProduct(Product product) {
		logger.info("Adding a new product: {}", product.getName());

		if (productRepository.existsByName(product.getName())) {
			logger.warn("Attempting to add a product that already exists with name: {}", product.getName());
		}
		return productRepository.save(product);
	}

	public Product findProduct(Long productId) {
		logger.info("Retrieving product with ID: {}", productId);
		return productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
	}

	public Product updateProduct(Long productId, Product updatedProduct) {
		logger.info("Updating product with ID: {}", productId);
		Product existingProduct = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

		existingProduct.setName(updatedProduct.getName());
		existingProduct.setDescription(updatedProduct.getDescription());
		existingProduct.setPrice(updatedProduct.getPrice());
		existingProduct.setQuantity(updatedProduct.getQuantity());

		return productRepository.save(existingProduct);
	}

	public Product patchProduct(Long productId, Product incompleteProduct) {
		logger.info("Patching product with ID: {}", productId);

		// Find product in the repository
		Product existingProduct = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

		// Perform patch
		try {
			patcher.internPatch(existingProduct, incompleteProduct);
		} catch (IllegalAccessException e) {
			throw new PatchErrorException("Failed to patch product with id: " + productId);
		}

		// Save the updated product
		productRepository.save(existingProduct);
		return existingProduct;
	}

	public void removeProduct(Long productId) {
		logger.info("Removing product with ID: {}", productId);
		if (!productRepository.existsById(productId)) {
			logger.warn("Trying to delete a non-existent product with ID: {}", productId);
			throw new ProductNotFoundException("Product not found with id: " + productId);
		}
		productRepository.deleteById(productId);
	}

	public List<Product> listAllProducts() {
		logger.debug("Listing all products");
		return productRepository.findAll();
	}
}