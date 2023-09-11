package com.example.virtualbookstore.service.servceimpl;

import com.example.virtualbookstore.entity.Book;
import com.example.virtualbookstore.entity.CartItem;
import com.example.virtualbookstore.entity.User;
import com.example.virtualbookstore.repository.BookRepository;
import com.example.virtualbookstore.repository.CartItemRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class ShoppingCartService {
    private  CartItemRepository cartItemRepository;

    private final BookRepository bookRepository;

    public ShoppingCartService(CartItemRepository cartItemRepository,BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public List<CartItem> listCartItems(User user){
        return cartItemRepository.findByUser(user);
    }

    public Integer addBook(Long bookId,Integer quantity,User user){
        Integer addedQuantity = quantity;
        Optional<Book> book = bookRepository.findById(bookId);
        CartItem cartItem = cartItemRepository.findByUserAndBook(user,book.get());
        if(cartItem != null){
            addedQuantity = cartItem.getQuantity() + quantity;
            cartItem.setQuantity(addedQuantity);
        }else {
            cartItem = new CartItem();
            cartItem.setQuantity(quantity);
            cartItem.setUser(user);
            cartItem.setBook(book.get());
        }
        cartItemRepository.save(cartItem);
        return addedQuantity;
    }

    public float updateQuantity(Long bookId,Integer quantity,User user){
        cartItemRepository.updateQuantity(quantity,bookId,user.getId());
        Optional<Book> book = bookRepository.findById(bookId);
        float subtotal = book.get().getPrice()*quantity;
        return subtotal;
    }

    public void removeProduct(Book book,User user){
        cartItemRepository.deleteByBookAndUser(book,user);
    }
}
