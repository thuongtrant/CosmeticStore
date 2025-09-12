package com.ttt.CosmeticStore.mapper;

import com.ttt.CosmeticStore.dto.response.AiChatResponse;
import com.ttt.CosmeticStore.entity.Product;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AiDataMapper {

    public List<AiChatResponse.ProductRecommendation> extractProductRecommendations(
            String aiResponse,
            List<Product> allProducts) {

        String response = aiResponse.toLowerCase();

        Set<Product> matchedProducts = new LinkedHashSet<>();

        // tìm sp chứa tên
        allProducts.stream()
                .filter(product -> {
                    String productName = product.getName().toLowerCase();
                    return response.contains(productName);
                })
                .forEach(matchedProducts::add);

        // tìm kw có trong ten
        allProducts.stream()
                .filter(product -> checkSpecificProductPatterns(response, product))
                .forEach(matchedProducts::add);

        // khớp tên sp
        if (matchedProducts.size() < 4) {
            allProducts.stream()
                    .filter(product -> checkPartialNameMatch(response, product))
                    .filter(product -> !matchedProducts.contains(product)) // Avoid duplicates
                    .forEach(matchedProducts::add);
        }

        // khớp kw
        if (matchedProducts.size() < 3) {
            allProducts.stream()
                    .filter(product -> checkProductKeywords(response, product) ||
                                     checkCategoryMatch(response, product))
                    .filter(product -> !matchedProducts.contains(product))
                    .limit(3 - matchedProducts.size())
                    .forEach(matchedProducts::add);
        }

        return matchedProducts.stream()
                .limit(4)
                .map(product -> mapToProductRecommendation(product,
                    "Được AI Assistant gợi ý phù hợp với nhu cầu của bạn"))
                .collect(Collectors.toList());
    }

    private boolean checkPartialNameMatch(String response, Product product) {
        String productName = product.getName().toLowerCase();
        String[] productWords = productName.split("\\s+");

        if (productWords.length < 2) {
            return false;
        }

        long matchingWords = Arrays.stream(productWords)
                .filter(word -> word.length() > 3)
                .filter(word -> !isCommonWord(word))
                .mapToLong(word -> response.contains(word) ? 1 : 0)
                .sum();

        return matchingWords >= 2;
    }

    private boolean isCommonWord(String word) {
        Set<String> commonWords = Set.of("serum", "cream", "gel", "lotion", "cleanser",
                                        "toner", "essence", "mask", "oil", "foam", "daily");
        return commonWords.contains(word.toLowerCase());
    }


    private boolean checkProductKeywords(String response, Product product) {
        String productName = product.getName().toLowerCase();
        String[] keywords = productName.split("\\s+");

        return Arrays.stream(keywords)
                .filter(keyword -> keyword.length() > 4)
                .filter(keyword -> !isCommonWord(keyword))
                .anyMatch(response::contains);
    }

    private boolean checkCategoryMatch(String response, Product product) {
        if (product.getCategory() == null) return false;
        String category = product.getCategory().getName().toLowerCase();
        return response.contains(category);
    }


    public AiChatResponse.ProductRecommendation mapToProductRecommendation(Product product, String reason) {
        return new AiChatResponse.ProductRecommendation(
            product.getId(),
            product.getName(),
            reason,
            product.getPrice().doubleValue(),
            product.getMainImageUrl()
        );
    }

    private boolean checkSpecificProductPatterns(String response, Product product) {
        String productName = product.getName().toLowerCase();

        if (productName.contains("biogenic") && response.contains("biogenic")) {
            return true;
        }
        if (productName.contains("succinic") && response.contains("succinic")) {
            return true;
        }
        if (productName.contains("cicaporin") && response.contains("cicaporin")) {
            return true;
        }
        if (productName.contains("porifying") && response.contains("porifying")) {
            return true;
        }
        if (productName.contains("resurfacing") && response.contains("resurfacing")) {
            return true;
        }

        if (productName.contains("s311") && response.contains("s311")) {
            return true;
        }
        if (productName.contains("bha") && response.contains("bha")) {
            return true;
        }
        if (productName.contains("retinol") && response.contains("retinol")) {
            return true;
        }

        return false;
    }
}
