package com.github.skjolber.stcsv.column.tri;

@FunctionalInterface
public interface ObjDoubleTriConsumer<T, I> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
	 * @param intermediate intermediate helper / processor
     * @param value the second input argument
     */
    void accept(T t, I intermediate, double value);
}
