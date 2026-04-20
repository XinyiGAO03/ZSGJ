package com.zhongshi.backend.product;

import com.zhongshi.backend.common.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public ApiResponse<List<Product>> listProducts() {
        return ApiResponse.success(productRepository.findAll());
    }

    @PostMapping
    public ApiResponse<Product> createProduct(
            @RequestParam String title,
            @RequestParam String price,
            @RequestParam(required = false) String conditionTag,
            @RequestParam String story,
            @RequestParam(required = false, defaultValue = "匿名用户") String sellerNickname,
            @RequestPart(required = false) MultipartFile image
    ) {
        try {
            Product product = new Product();
            product.setTitle(title);
            product.setPrice(new BigDecimal(price));
            product.setConditionTag(conditionTag);
            product.setStory(story);
            product.setSellerNickname(sellerNickname);

            if (image != null && !image.isEmpty()) {
                Files.createDirectories(Paths.get(uploadDir));
                String originalName = image.getOriginalFilename() == null ? "image.jpg" : image.getOriginalFilename();
                String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".")) : ".jpg";
                String fileName = UUID.randomUUID() + ext;
                Path filePath = Paths.get(uploadDir, fileName);
                image.transferTo(filePath.toFile());
                product.setImageUrl("/uploads/" + fileName);
            }

            return ApiResponse.success(productRepository.save(product));
        } catch (Exception e) {
            return ApiResponse.fail("创建商品失败：" + e.getMessage());
        }
    }
}
