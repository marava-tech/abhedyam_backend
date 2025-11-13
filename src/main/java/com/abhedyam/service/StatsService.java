package com.abhedyam.service;

import com.abhedyam.dto.StatsRequest;
import com.abhedyam.dto.StatsResponse;
import com.abhedyam.model.DailyStats;
import com.abhedyam.model.Product;
import com.abhedyam.model.SaleItem;
import com.abhedyam.model.TopProduct;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.DailyStatsRepository;
import com.abhedyam.repository.ProductRepository;
import com.abhedyam.repository.SaleItemRepository;
import com.abhedyam.repository.TopProductRepository;
import com.abhedyam.service.interfaces.IStatsService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsService implements IStatsService {
    
    private final DailyStatsRepository dailyStatsRepository;
    private final TopProductRepository topProductRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String STATS_CACHE_PREFIX = "stats:daily:";
    private static final int CACHE_TTL_HOURS = 24;
    
    @Override
    @Transactional
    public void aggregateDailyStats(LocalDate date) {
        List<UUID> ownerIds = saleItemRepository.findAll().stream()
            .map(SaleItem::getOwnerId)
            .distinct()
            .toList();
        
        for (UUID ownerId : ownerIds) {
            aggregateDailyStatsForOwner(ownerId, date);
        }
        
        log.info("Daily stats aggregated for date: {}", date);
    }
    
    @Override
    @Transactional
    public void aggregateDailyStatsForDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            aggregateDailyStats(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        log.info("Stats aggregated for date range: {} to {}", startDate, endDate);
    }
    
    @Override
    public List<StatsResponse> getStats(StatsRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now().minusDays(30);
        LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : LocalDate.now();
        
        List<StatsResponse> responses = new ArrayList<>();
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            final LocalDate dateForLambda = currentDate;
            String cacheKey = STATS_CACHE_PREFIX + ownerId + ":" + dateForLambda;
            
            DailyStats dailyStats = dailyStatsRepository.findByOwnerIdAndStatDate(ownerId, dateForLambda)
                .orElseGet(() -> {
                    String cached = redisTemplate.opsForValue().get(cacheKey);
                    if (cached == null) {
                        aggregateDailyStatsForOwner(ownerId, dateForLambda);
                        redisTemplate.opsForValue().set(cacheKey, "cached", CACHE_TTL_HOURS, TimeUnit.HOURS);
                    }
                    return dailyStatsRepository.findByOwnerIdAndStatDate(ownerId, dateForLambda)
                        .orElse(null);
                });
            
            if (dailyStats != null) {
                List<TopProduct> topProducts = topProductRepository
                    .findByOwnerIdAndStatDateOrderByRankAsc(ownerId, dateForLambda);
                
                List<StatsResponse.TopProductStats> topProductStats = topProducts.stream()
                    .limit(request.getTopProductsLimit())
                    .map(tp -> new StatsResponse.TopProductStats(
                        tp.getProductId().toString(),
                        tp.getProductName(),
                        tp.getTotalSales(),
                        tp.getTotalQuantity(),
                        tp.getOrderCount(),
                        tp.getRank()
                    ))
                    .toList();
                
                StatsResponse response = new StatsResponse(
                    dailyStats.getStatDate(),
                    dailyStats.getTotalSales(),
                    dailyStats.getTotalOrders(),
                    dailyStats.getTotalCustomers(),
                    dailyStats.getTotalProductsSold(),
                    topProductStats
                );
                
                responses.add(response);
            }
            
            currentDate = currentDate.plusDays(1);
        }
        
        return responses;
    }
    
    @Override
    @Transactional
    public void recomputeStats(LocalDate startDate, LocalDate endDate) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            String cacheKey = STATS_CACHE_PREFIX + ownerId + ":" + currentDate;
            redisTemplate.delete(cacheKey);
            
            dailyStatsRepository.findByOwnerIdAndStatDate(ownerId, currentDate)
                .ifPresent(dailyStatsRepository::delete);
            
            topProductRepository.deleteByOwnerIdAndStatDate(ownerId, currentDate);
            
            aggregateDailyStatsForOwner(ownerId, currentDate);
            
            currentDate = currentDate.plusDays(1);
        }
        
        log.info("Stats recomputed for owner {} from {} to {}", ownerId, startDate, endDate);
    }
    
    @Transactional
    private void aggregateDailyStatsForOwner(UUID ownerId, LocalDate date) {
        Instant startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        
        List<SaleItem> saleItems = saleItemRepository.findByOwnerId(ownerId).stream()
            .filter(item -> {
                Instant createdAt = item.getCreatedAt();
                return createdAt != null && createdAt.isAfter(startOfDay) && createdAt.isBefore(endOfDay);
            })
            .filter(item -> item.getIsActive() != null && item.getIsActive())
            .toList();
        
        if (saleItems.isEmpty()) {
            return;
        }
        
        List<String> transactionIds = saleItems.stream()
            .map(SaleItem::getTransactionId)
            .filter(tid -> tid != null)
            .distinct()
            .toList();
        
        int totalOrders = transactionIds.size();
        
        BigDecimal totalSales = saleItems.stream()
            .map(item -> item.getPrice().multiply(item.getQuantity() != null ? item.getQuantity() : BigDecimal.ONE))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long totalCustomers = saleItems.stream()
            .map(SaleItem::getCustomerId)
            .distinct()
            .count();
        
        int totalProductsSold = saleItems.stream()
            .mapToInt(item -> item.getQuantity() != null ? item.getQuantity().intValue() : 1)
            .sum();
        
        DailyStats dailyStats = dailyStatsRepository.findByOwnerIdAndStatDate(ownerId, date)
            .orElse(new DailyStats());
        
        dailyStats.setOwnerId(ownerId);
        dailyStats.setStatDate(date);
        dailyStats.setTotalSales(totalSales);
        dailyStats.setTotalOrders(totalOrders);
        dailyStats.setTotalCustomers((int) totalCustomers);
        dailyStats.setTotalProductsSold(totalProductsSold);
        
        dailyStatsRepository.save(dailyStats);
        
        Map<UUID, List<SaleItem>> productGroups = saleItems.stream()
            .collect(Collectors.groupingBy(SaleItem::getProductId));
        
        List<TopProduct> topProducts = new ArrayList<>();
        int rank = 1;
        
        List<Map.Entry<UUID, List<SaleItem>>> sortedProducts = productGroups.entrySet().stream()
            .sorted((e1, e2) -> {
                BigDecimal sales1 = e1.getValue().stream()
                    .map(item -> item.getPrice().multiply(item.getQuantity() != null ? item.getQuantity() : BigDecimal.ONE))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal sales2 = e2.getValue().stream()
                    .map(item -> item.getPrice().multiply(item.getQuantity() != null ? item.getQuantity() : BigDecimal.ONE))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                return sales2.compareTo(sales1);
            })
            .limit(20)
            .toList();
        
        for (Map.Entry<UUID, List<SaleItem>> entry : sortedProducts) {
            UUID productId = entry.getKey();
            List<SaleItem> items = entry.getValue();
            
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                continue;
            }
            
            BigDecimal productSales = items.stream()
                .map(item -> item.getPrice().multiply(item.getQuantity() != null ? item.getQuantity() : BigDecimal.ONE))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal productQuantity = items.stream()
                .map(item -> item.getQuantity() != null ? item.getQuantity() : BigDecimal.ONE)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            int orderCount = (int) items.stream()
                .map(SaleItem::getTransactionId)
                .distinct()
                .count();
            
            TopProduct topProduct = new TopProduct();
            topProduct.setOwnerId(ownerId);
            topProduct.setStatDate(date);
            topProduct.setProductId(productId);
            topProduct.setProductName(product.getName());
            topProduct.setTotalSales(productSales);
            topProduct.setTotalQuantity(productQuantity);
            topProduct.setOrderCount(orderCount);
            topProduct.setRank(rank++);
            
            topProducts.add(topProduct);
        }
        
        topProductRepository.saveAll(topProducts);
    }
}

