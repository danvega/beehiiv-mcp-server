package dev.danvega.beehiiv.subscription;

import java.util.List;

public record SubscriptionsResponse(
    List<Subscription> data,
    Integer totalResults,
    Integer page,
    Integer limit
) {
}