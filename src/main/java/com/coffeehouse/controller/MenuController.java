package com.coffeehouse.controller;

import com.coffeehouse.entity.MenuItem;
import com.coffeehouse.entity.Customer;
import com.coffeehouse.service.MenuService;
import com.coffeehouse.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService service;

    @Autowired
    private AdminService adminService;

    private static final String UPLOAD_DIR = "uploads/";


    // Public endpoints
    @GetMapping("/all")
    public List<MenuItem> getAllItems() {
        return service.getAllItems();
    }

    @GetMapping("/category/{category}")
    public List<MenuItem> getByCategory(@PathVariable String category) {
        return service.getItemsByCategory(category);
    }

    @GetMapping("/search")
    public List<MenuItem> search(@RequestParam String name) {
        return service.searchItems(name);
    }

    // Only admin can add items. Client must include "email" field of the acting user in the request body.
    @PostMapping("/add")
    public ResponseEntity<?> addItem(@RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "email is required"));
        }

        Customer admin = adminService.getCustomerByEmail(email).orElse(null);
        if (admin == null || !"admin".equalsIgnoreCase(admin.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied: Only admin can add menu items."));
        }

        try {
            MenuItem item = MenuItem.builder()
                    .name((String) request.get("name"))
                    .description((String) request.get("description"))
                    .price(Double.parseDouble(request.get("price").toString()))
                    .category((String) request.get("category"))
                    .imageUrl((String) request.get("imageUrl"))
                    .build();

            MenuItem saved = service.addMenuItem(item);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid request payload: " + e.getMessage()));
        }
    }

    // Only admin can delete menu items. Provide admin email as query param.
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id, @RequestParam String email) {
        Customer admin = adminService.getCustomerByEmail(email).orElse(null);
        if (admin == null || !"admin".equalsIgnoreCase(admin.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied: Only admin can delete menu items."));
        }

        service.deleteMenuItem(id);
        return ResponseEntity.ok(Map.of("message", "Menu item deleted successfully"));
    }
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Absolute path — this ensures it’s always outside Tomcat temp dirs
            Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");

            // Create folder if missing
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Generate unique filename
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(filename);

            // Save file
            file.transferTo(filePath.toFile());

            // Build public URL
            String imageUrl = "http://localhost:8080/uploads/" + filename;

            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        }
    }
    // Only admin can edit menu items. Provide admin email and fields to update.
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editItem(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "email is required"));
        }

        Customer admin = adminService.getCustomerByEmail(email).orElse(null);
        if (admin == null || !"admin".equalsIgnoreCase(admin.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access denied: Only admin can edit menu items."));
        }

        MenuItem existing = service.getMenuItemById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Menu item not found"));
        }

        try {
            if (request.containsKey("name")) {
                existing.setName((String) request.get("name"));
            }
            if (request.containsKey("description")) {
                existing.setDescription((String) request.get("description"));
            }
            if (request.containsKey("price")) {
                existing.setPrice(Double.parseDouble(request.get("price").toString()));
            }
            if (request.containsKey("category")) {
                existing.setCategory((String) request.get("category"));
            }
            if (request.containsKey("imageUrl")) {
                existing.setImageUrl((String) request.get("imageUrl"));
            }

            MenuItem updated = service.addMenuItem(existing); // reuse save logic
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to update menu item: " + e.getMessage()));
        }
    }

}
