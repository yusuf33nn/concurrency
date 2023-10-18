package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId){
        Executor executor = Executors.newFixedThreadPool(51);
        List<Double> prices = new CopyOnWriteArrayList<>();
        shopIds.forEach(e -> {
            CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(itemId, e), executor)
                    .orTimeout(3, TimeUnit.SECONDS)
                    .thenApply(prices::add);
        });
        try {
            Thread.sleep(2950);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return !prices.isEmpty() ? Collections.min(prices) : 0/0d;
    }
}
