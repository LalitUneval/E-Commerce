package org.lalit.ecommercebackend.repository;

import org.lalit.ecommercebackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product>findByCategoryId(Long categoryId);
    //findBy means select  , name select name  , Containing means -> for this value eg(lalit) search in the db
    // IngnoreCase - >case sensitive
    List<Product> findByNameContainingIgnoreCase(String name);
}
