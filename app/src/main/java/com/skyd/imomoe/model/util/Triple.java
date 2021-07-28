package com.skyd.imomoe.model.util;

import java.util.Objects;

public class Triple<F, S, T> {
    public final F first;
    public final S second;
    public final T third;

    public Triple(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Triple)) {
            return false;
        }
        Triple<?, ?, ?> p = (Triple<?, ?, ?>) o;
        return Objects.equals(p.first, first) && Objects.equals(p.second, second) &&
                Objects.equals(p.third, third);
    }

    @Override
    public String toString() {
        return "Triple{" + first + " " + second + " " + third + "}";
    }

    public static <A, B, C> Triple<A, B, C> create(A a, B b, C c) {
        return new Triple<>(a, b, c);
    }
}
