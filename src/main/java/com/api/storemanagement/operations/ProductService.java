package com.api.storemanagement.operations;

import com.api.storemanagement.exceptions.ProductNotFoundException;
import com.api.storemanagement.product.Product;
import com.api.storemanagement.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
		return productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
	}

	public Product updateProduct(Long productId, Product updatedProduct) {
		Product existingProduct = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

		existingProduct.setName(updatedProduct.getName());
		existingProduct.setDescription(updatedProduct.getDescription());
		existingProduct.setPrice(updatedProduct.getPrice());
		existingProduct.setQuantity(updatedProduct.getQuantity());

		return productRepository.save(existingProduct);
	}

	public void removeProduct(Long productId) {
		if (!productRepository.existsById(productId)) {
			throw new ProductNotFoundException("Product not found with id: " + productId);
		}
		productRepository.deleteById(productId);
	}

	public List<Product> listAllProducts() {
		return productRepository.findAll();
	}
}