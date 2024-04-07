package com.api.storemanagement.operations.products;

import com.api.storemanagement.exceptions.PatchErrorException;
import com.api.storemanagement.exceptions.ProductNotFoundException;
import com.api.storemanagement.product.Product;
import com.api.storemanagement.product.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service that does operations on a product repository
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
	@Transactional
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
	@Transactional(readOnly = true)
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
	@Transactional
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
	@Transactional
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
	@Transactional
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
	@Transactional(readOnly = true)
	public List<Product> listAllProducts() {
		logger.debug("Listing all products.");
		return productRepository.findAll();
	}

	/**
	 * Return list of products priced above a specified threshold and with a stock quantity below a defined limit
	 * @param minPrice - minimum price to look for
	 * @param maxQuantity - maximum quantity
	 * @return list of products respecting the condition
	 */
	@Transactional(readOnly = true)
	public List<Product> findExpensiveLowStockProducts(double minPrice, int maxQuantity) {
		logger.debug("Looking for products with price higher than " + minPrice +
				"and in lower quantity than " + maxQuantity);
		return productRepository.findExpensiveLowStockProducts(minPrice, maxQuantity);
	}
}