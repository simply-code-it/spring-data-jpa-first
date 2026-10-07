package com.example.buy_buddy_backend.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.buy_buddy_backend.exception.ResourceNotFoundException;
import com.example.buy_buddy_backend.model.Product;
import com.example.buy_buddy_backend.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void getAllProducts_shouldReturnAllProducts() {
        Product product1 = createProduct(1L, "Laptop", 1200.00);
        Product product2 = createProduct(2L, "Mouse", 25.50);
        List<Product> products = Arrays.asList(product1, product2);

        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertEquals(products, result);
        verify(productRepository).findAll();
    }

    @Test
    void getProductById_shouldReturnProduct_whenProductExists() {
        Product product = createProduct(1L, "Laptop", 1200.00);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertEquals(product, result);
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_shouldThrowResourceNotFoundException_whenProductDoesNotExist() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(99L));

        assertEquals("Product not found with id: 99", exception.getMessage());
        verify(productRepository).findById(99L);
    }

    @Test
    void createProduct_shouldSaveAndReturnProduct() {
        Product product = createProduct(null, "Keyboard", 75.00);

        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.createProduct(product);

        assertEquals(product, result);
        verify(productRepository).save(product);
    }

    @Test
    void updateProduct_shouldUpdateProductDetailsAndSave() {
        Product existingProduct = createProduct(1L, "Laptop", 1000.00);
        Product productDetails = createProduct(null, "Gaming Laptop", 1500.00);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        Product result = productService.updateProduct(1L, productDetails);

        assertEquals("Gaming Laptop", result.getName());
        assertEquals(1500.00, result.getPrice());
        verify(productRepository).findById(1L);
        verify(productRepository).save(existingProduct);
    }

    @Test
    void updateProduct_shouldThrowResourceNotFoundException_whenProductDoesNotExist() {
        Product productDetails = createProduct(null, "Gaming Laptop", 1500.00);

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(99L, productDetails));

        assertEquals("Product not found with id: 99", exception.getMessage());
        verify(productRepository).findById(99L);
    }

    @Test
    void deleteProduct_shouldDeleteExistingProduct() {
        Product product = createProduct(1L, "Laptop", 1200.00);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).findById(1L);
        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_shouldThrowResourceNotFoundException_whenProductDoesNotExist() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.deleteProduct(99L));

        assertEquals("Product not found with id: 99", exception.getMessage());
        verify(productRepository).findById(99L);
    }

    private Product createProduct(Long id, String name, Double price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}
