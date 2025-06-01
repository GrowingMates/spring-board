package com.exception.custom;

import com.exception.CustomException;
import com.exception.ErrorCodeType;
import java.util.Map;

public class NotIncludeBoardException extends CustomException {

    private final Long articleId;

    private NotIncludeBoardException(Long articleId) {
        super(ErrorCodeType.MISMATCHED_DATA);
        this.articleId = articleId;
    }

    @Override
    public Map<String, Object> getAdditionalDetails() {
        return Map.of("errorMessage", "해당 댓글은 게시글 " + articleId + "에 속하지 않습니다.");
    }

    public static NotIncludeBoardException from(Long articleId) {
        return new NotIncludeBoardException(articleId);
    }
}
