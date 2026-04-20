package com.toy.project.studio.reservation.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VisitPath {
    INSTAGRAM("인스타그램"),
    NAVER("네이버 검색"),
    KAKAO("카카오톡"),
    FRIEND("지인 추천"),
    REVISIT("재방문"),
    OTHER("기타");

    private final String label;

}
