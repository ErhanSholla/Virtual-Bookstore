package com.example.virtualbookstore.repository;

import com.example.virtualbookstore.entity.Book;
import com.example.virtualbookstore.entity.CartItem;
import com.example.virtualbookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    List<CartItem> findByUser(User user);
    CartItem findByUserAndBook(User user, Book book);

    @Query("UPDATE CartItem c set c.quantity = ?1 where c.book.id = ?2 AND  c.user.id = ?3")
    @Modifying
    void updateQuantity(Integer quantity, Long productId,Long userId);

    void deleteByBookAndUser(Book bookId,User user);
}
