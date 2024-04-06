package com.api.storemanagement.operations;

import com.api.storemanagement.product.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * Component responsible for patching fields of a product
 */
@Component
public class ProductPatcher {
	private static final Logger logger = LoggerFactory.getLogger(ProductPatcher.class);

	public void internPatch(Product existingProduct, Product incompleteProduct) throws IllegalAccessException {
		Class<?> productClass= Product.class;
		Field[] productFields = productClass.getDeclaredFields();

		logger.debug("Product class has {} fields.", productFields.length);

		for (Field field : productFields){
			logger.debug("Checking {} field", field.getName());

			// Can't access if field is private
			field.setAccessible(true);

			// Check if the field value is not null, if not, update existing product
			Object value = field.get(incompleteProduct);
			if (value != null){
				field.set(existingProduct, value);
			}

			// Make field private again
			field.setAccessible(false);
		}
	}
}
