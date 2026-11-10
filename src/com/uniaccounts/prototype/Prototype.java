package com.uniaccounts.prototype;

/**
 * Prototype pattern - role: PROTOTYPE (abstract contract).
 * Any object that knows how to produce an independent copy of itself.
 */
public interface Prototype<T> {
    /** Returns a deep copy: changing the copy never affects the original. */
    T copy();
}
