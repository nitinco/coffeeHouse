package com.coffeehouse.service;

import com.coffeehouse.entity.MenuItem;
import com.coffeehouse.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuService {

    @Autowired
    private MenuItemRepository repo;

    public List<MenuItem> getAllItems() {
        return repo.findAll();
    }

    public List<MenuItem> getItemsByCategory(String category) {
        return repo.findByCategory(category);
    }

    public List<MenuItem> searchItems(String name) {
        return repo.findByNameContainingIgnoreCase(name);
    }

    public MenuItem addMenuItem(MenuItem item) {
        return repo.save(item);
    }
    public void deleteMenuItem(Long id){
        repo.deleteById(id);
    }
    public Optional<MenuItem> getMenuItemById(Long id) {
        return repo.findById(id);
    }
}
