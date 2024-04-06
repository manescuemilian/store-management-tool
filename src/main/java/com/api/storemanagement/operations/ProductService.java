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

/**
 * Service that does operations on a repository
 */
@Service
public class ProductService {

	private final ProductRepository productRepository;

	/**
	 * Patcher class responsible for updating certain fields of products
	 */
	@Autowired
	ProductPatcher patcher;

	/**
	 * Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

	@Autowired
	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
		logger.info("ProductService initialized");
	}

	/**
	 * Method for adding a new product to the repository
	 * @param product - product to add
	 * @return Added product
	 */
	public Product addProduct(Product product) {
		logger.info("Adding a new product: {}.", product.getName());

		if (productRepository.existsByName(product.getName())) {
			logger.warn("Attempting to add a product that already exists with name: {}.", product.getName());
		}
		return productRepository.save(product);
	}

	/**
	 * Method for retrieving a product from the repository
	 * @param productId - ID of product
	 * @return product having the provided ID
	 */
	public Product findProduct(Long productId) {
		logger.info("Retrieving product with ID: {}", productId);
		return productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId + "."));
	}

	/**
	 * Method for updating a product. Whole product definition should be provided
	 * @param productId - ID of product to be updated
	 * @param updatedProduct - new definition of product
	 * @return updated product definition
	 */
	public Product updateProduct(Long productId, Product updatedProduct) {
		logger.info("Updating product with ID: {}", productId);
		Product existingProduct = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId + "."));

		existingProduct.setName(updatedProduct.getName());
		existingProduct.setDescription(updatedProduct.getDescription());
		existingProduct.setPrice(updatedProduct.getPrice());
		existingProduct.setQuantity(updatedProduct.getQuantity());

		return productRepository.save(existingProduct);
	}

	/**
	 * Method for patching a product. Can provide only certain fields of the product.
	 * @param productId - ID of product to be patched
	 * @param incompleteProduct - fields to update in the product
	 * @return new definition of product
	 */
	public Product patchProduct(Long productId, Product incompleteProduct) {
		logger.info("Patching product with ID: {}", productId);

		// Find product in the repository
		Product existingProduct = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId + "."));

		// Perform patch
		try {
			patcher.internPatch(existingProduct, incompleteProduct);
		} catch (IllegalAccessException e) {
			throw new PatchErrorException("Failed to patch product with id: " + productId + ".");
		}

		// Save the updated product
		productRepository.save(existingProduct);
		return existingProduct;
	}

	/**
	 * Remove a product from the repository
	 * @param productId - ID of product to be removed
	 */
	public void removeProduct(Long productId) {
		logger.info("Removing product with ID: {}", productId);
		if (!productRepository.existsById(productId)) {
			logger.warn("Trying to delete a non-existent product with ID: {}.", productId);
			throw new ProductNotFoundException("Product not found with id: " + productId + ".");
		}
		productRepository.deleteById(productId);
	}

	/**
	 * List all products
	 * @return list containing all products in the repository
	 */
	public List<Product> listAllProducts() {
		logger.debug("Listing all products.");
		return productRepository.findAll();
	}
}