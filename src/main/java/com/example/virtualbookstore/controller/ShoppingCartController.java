package com.example.virtualbookstore.controller;

import com.example.virtualbookstore.entity.Book;
import com.example.virtualbookstore.entity.CartItem;
import com.example.virtualbookstore.entity.User;
import com.example.virtualbookstore.repository.BookRepository;
import com.example.virtualbookstore.service.servceimpl.ShoppingCartService;
import com.example.virtualbookstore.service.servceimpl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService service;
    private final UserService userService;
    private final BookRepository bookRepository;

    @GetMapping("/cart")
    public List<CartItem>showShoppinCart(@RequestBody User user){
        return service.listCartItems(user);
    }

    @PostMapping("/cart/add/{bid}/{qty}/{username}")
    public String addBookToCart(@PathVariable("bid") Long bookId,@PathVariable("qty") Integer quantity
            ,@PathVariable("username") String username){
        User user = userService.findByUsername(username).orElseThrow();
        Integer addedQuantity = service.addBook(bookId,quantity,user);
        return addedQuantity + " item(s) of product are added in your shopping cart";
    }
    @PostMapping("/cart/updateQuantity/{bid}/{qty}/{username}")
    public String updateQuantity(@PathVariable("bid") Long bookId,@PathVariable("qty") Integer quantity
            ,@PathVariable("username") String username){
        User user = userService.findByUsername(username).orElseThrow();
        float subtotal = service.updateQuantity(bookId,quantity,user);
        return subtotal + " item(s) of product are added in your shopping cart";
    }
    @PostMapping("/cart/remove/{bid}")
    public String removeProductFromCart(@PathVariable("bid") Long bookId,@PathVariable("qty")@RequestBody String username){
        User user = userService.findByUsername(username).orElseThrow();
        Book book = bookRepository.findById(bookId).orElseThrow();
        service.removeProduct(book,user);
        return "Product has been removed Successfully";
    }

}

