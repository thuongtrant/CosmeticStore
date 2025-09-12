package com.ttt.CosmeticStore.mapper;

import com.ttt.CosmeticStore.dto.response.AiChatResponse;
import com.ttt.CosmeticStore.entity.Product;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AiDataMapper {

    /**
     * Enhanced product recommendation extraction from AI response
     * This is the only method we still need with OpenAI Assistant API
     */
    public List<AiChatResponse.ProductRecommendation> extractProductRecommendations(
            String aiResponse,
            List<Product> allProducts) {

        String response = aiResponse.toLowerCase();

        // Use LinkedHashSet to maintain order and ensure uniqueness
        Set<Product> matchedProducts = new LinkedHashSet<>();

        // Strategy 1: Exact name match (highest priority)
        allProducts.stream()
                .filter(product -> {
                    String productName = product.getName().toLowerCase();
                    return response.contains(productName);
                })
                .forEach(matchedProducts::add);

        // Strategy 2: Specific product patterns (high priority)
        allProducts.stream()
                .filter(product -> checkSpecificProductPatterns(response, product))
                .forEach(matchedProducts::add);

        // Strategy 3: Partial name matching (medium priority)
        if (matchedProducts.size() < 4) { // Only if we need more products
            allProducts.stream()
                    .filter(product -> checkPartialNameMatch(response, product))
                    .filter(product -> !matchedProducts.contains(product)) // Avoid duplicates
                    .forEach(matchedProducts::add);
        }

        // Strategy 4: Keyword and category matching (lower priority)
        if (matchedProducts.size() < 3) { // Only if we still need more products
            allProducts.stream()
                    .filter(product -> checkProductKeywords(response, product) ||
                                     checkCategoryMatch(response, product))
                    .filter(product -> !matchedProducts.contains(product)) // Avoid duplicates
                    .limit(3 - matchedProducts.size()) // Fill up to 3 total
                    .forEach(matchedProducts::add);
        }

        // Convert to list and limit to maximum 4 products to avoid overwhelming user
        return matchedProducts.stream()
                .limit(4)
                .map(product -> mapToProductRecommendation(product,
                    "Được AI Assistant gợi ý phù hợp với nhu cầu của bạn"))
                .collect(Collectors.toList());
    }

    /**
     * Check for partial name matching with improved logic
     */
    private boolean checkPartialNameMatch(String response, Product product) {
        String productName = product.getName().toLowerCase();
        String[] productWords = productName.split("\\s+");

        if (productWords.length < 2) {
            return false;
        }

        // Check if at least 2 significant words from product name appear in response
        long matchingWords = Arrays.stream(productWords)
                .filter(word -> word.length() > 3) // Skip short words like "gel", "for"
                .filter(word -> !isCommonWord(word)) // Skip common skincare words
                .mapToLong(word -> response.contains(word) ? 1 : 0)
                .sum();

        return matchingWords >= 2;
    }

    /**
     * Check if word is a common skincare term that shouldn't be used for matching
     */
    private boolean isCommonWord(String word) {
        Set<String> commonWords = Set.of("serum", "cream", "gel", "lotion", "cleanser",
                                        "toner", "essence", "mask", "oil", "foam", "daily");
        return commonWords.contains(word.toLowerCase());
    }

    /**
     * Check if AI response contains product-related keywords
     */
    private boolean checkProductKeywords(String response, Product product) {
        String productName = product.getName().toLowerCase();
        String[] keywords = productName.split("\\s+");

        return Arrays.stream(keywords)
                .filter(keyword -> keyword.length() > 4) // Increase threshold
                .filter(keyword -> !isCommonWord(keyword)) // Skip common words
                .anyMatch(response::contains);
    }

    /**
     * Check if AI response mentions product category
     */
    private boolean checkCategoryMatch(String response, Product product) {
        if (product.getCategory() == null) return false;
        String category = product.getCategory().getName().toLowerCase();
        return response.contains(category);
    }

    /**
     * Map Product entity to ProductRecommendation DTO with custom reason
     */
    public AiChatResponse.ProductRecommendation mapToProductRecommendation(Product product, String reason) {
        return new AiChatResponse.ProductRecommendation(
            product.getId(),
            product.getName(),
            reason,
            product.getPrice().doubleValue(),
            product.getMainImageUrl()
        );
    }

    /**
     * Check for specific product name patterns that might be mentioned in AI response
     */
    private boolean checkSpecificProductPatterns(String response, Product product) {
        String productName = product.getName().toLowerCase();

        // Check for brand/product line patterns
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

        // Check for product codes or specific identifiers
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
