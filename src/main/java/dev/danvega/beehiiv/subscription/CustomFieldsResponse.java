package dev.danvega.beehiiv.subscription;

import java.util.List;

public record CustomFieldsResponse(
    List<CustomField> data,
    Integer totalResults,
    Integer page,
    Integer limit
) {
}