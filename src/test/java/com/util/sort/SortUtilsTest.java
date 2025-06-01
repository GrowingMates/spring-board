package com.util.sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

class SortUtilsTest {

    @Nested
    @DisplayName("게시글 정렬 테스트")
    class ArticleSortTest {

        @Test
        @DisplayName("정상적인_정렬_파라미터_입력_시_정상_반환")
        void 정상적인_정렬_파라미터_입력_시_정상_반환() {
            Sort latestSort = SortUtils.getArticleSort("latest");
            Sort viewsSort = SortUtils.getArticleSort("views");

            assertAll(
                    () -> {
                        Order order = latestSort.getOrderFor("createdAt");
                        assertThat(order).isNotNull();
                        assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
                    },
                    () -> {
                        Order order = viewsSort.getOrderFor("viewCount");
                        assertThat(order).isNotNull();
                        assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
                    }
            );
        }

        @Test
        @DisplayName("잘못된_정렬_파라미터_입력_시_createdAt_DESC_기본값_반환")
        void 잘못된_정렬_파라미터_입력_시_default_반환() {
            Sort sort = SortUtils.getArticleSort("invalidKey");

            Order order = sort.getOrderFor("createdAt");

            assertThat(order).isNotNull();
            assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
        }
    }

    @Nested
    @DisplayName("댓글 정렬 테스트")
    class CommentSortTest {

        @Test
        @DisplayName("최신순_정렬_입력_시_createdAt_DESC_반환")
        void 최신순_정렬_입력_시_createdAt_DESC_반환() {
            Sort sort = SortUtils.getCommentSort("latest");
            Order order = sort.getOrderFor("createdAt");

            assertThat(order).isNotNull();
            assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
        }

        @Test
        @DisplayName("지원되지_않는_정렬_입력_시_createdAt_DESC_기본값_반환")
        void 지원되지_않는_정렬_입력_시_createdAt_DESC_반환() {
            Sort sort = SortUtils.getCommentSort("views"); // 댓글은 viewCount 허용 안됨

            Order order = sort.getOrderFor("createdAt");

            assertThat(order).isNotNull();
            assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
        }
    }
}
