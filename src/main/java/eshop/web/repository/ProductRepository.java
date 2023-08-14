package eshop.web.repository;

import eshop.web.model.Category;
import eshop.web.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByCategory(Category category);

    List<Product> findByNameContaining(String name);

    Product findByName(String name);
}
