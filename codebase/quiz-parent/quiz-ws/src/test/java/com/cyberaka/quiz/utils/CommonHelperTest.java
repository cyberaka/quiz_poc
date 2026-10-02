package com.cyberaka.quiz.utils;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommonHelperTest {

    private final CommonHelper helper = CommonHelper.getInstance();

    private List<Integer> listOf(int size) {
        return IntStream.range(0, size).boxed().collect(Collectors.toList());
    }

    @Test
    void emptyListStaysEmpty() {
        assertEquals(Collections.emptyList(), helper.getTwentyPercentOfResults(listOf(0)));
    }

    @Test
    void smallListReturnsAtLeastOne() {
        assertEquals(1, helper.getTwentyPercentOfResults(listOf(1)).size());
        assertEquals(1, helper.getTwentyPercentOfResults(listOf(3)).size());
    }

    @Test
    void returnsTwentyPercentRoundedUp() {
        assertEquals(2, helper.getTwentyPercentOfResults(listOf(10)).size());
        assertEquals(7, helper.getTwentyPercentOfResults(listOf(33)).size());
    }
}
