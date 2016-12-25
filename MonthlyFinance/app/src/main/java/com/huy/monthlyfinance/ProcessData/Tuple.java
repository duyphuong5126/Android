package com.huy.monthlyfinance.ProcessData;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by huy nguyen on 12/24/2016.
 */
class Tuple {
    Set<Integer> itemSet;
    float support;

    Tuple() {
        itemSet = new HashSet<>();
        support = -1;
    }

    Tuple(Set<Integer> s) {
        itemSet = s;
        support = -1;
    }

    Tuple(Set<Integer> s, float i) {
        itemSet = s;
        support = i;
    }
}
