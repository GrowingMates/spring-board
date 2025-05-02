package com.util.sort;

import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

@Slf4j
public class SortUtils {

    private static final Map<String, Order> SORT_ALIAS_MAP = Map.of(
            "latest", Order.desc("createdAt"),
            "oldest", Order.asc("createdAt"),
            "views", Order.desc("viewCount")
    );

    private static final Set<String> ARTICLE_SORT_FIELDS = Set.of("createdAt", "viewCount");
    private static final Set<String> COMMENT_SORT_FIELDS = Set.of("createdAt");

    public static Sort getArticleSort(String sortParam) {
        return createSort(sortParam, ARTICLE_SORT_FIELDS, Order.desc("createdAt"));
    }

    public static Sort getCommentSort(String sortParam) {
        return createSort(sortParam, COMMENT_SORT_FIELDS, Order.desc("createdAt"));
    }

    private static Sort createSort(String sortParam, Set<String> allowedFields, Order defaultOrder) {
        Order mappedOrder = SORT_ALIAS_MAP.getOrDefault(sortParam, defaultOrder);

        if (!allowedFields.contains(mappedOrder.getProperty())) {
            log.warn("Unsupported sort field '{}'. Falling back to default '{}'", sortParam,
                    defaultOrder.getProperty());
            mappedOrder = defaultOrder;
        }

        return Sort.by(mappedOrder);
    }
}
