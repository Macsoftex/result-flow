/*
 * Copyright 2016 René Perschon <rperschon85@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.perschon.resultflow;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The Result type is an alternative way of chaining together functions in a
 * functional programming style while hiding away error handling structures such as
 * try-catch-blocks and conditionals.<br/>
 * 
 * Instead of adding a throws declaration to a function, the return type of the function
 * is instead set to Result<V, E> where V is the original return type, i.e. the
 * "happy case" and E is the error type, usually the Exception type or a String
 * if an error explanation is sufficient.<br/><br/>
 * 
 * Example:
 * <pre>
 * public Result<Float, String> divide(int a, int b) {
 *     if (b == 0) {
 *         return Result.failure("Can't divide by zero!");
 *     } else {
 *         return Result.success(a / b);
 *     }
 * }
 * </pre>
 *   
 * @param <V> The value type of the Result.
 * @param <E> The error type of the Result.
 */
public interface Result<V, E> {

	/**
	 * Returns a new Ok instance containing the given value.
	 *
	 * @param value the value
	 * @param <V> The type of the value
	 * @param <E> The type of the error
	 * @return see above
	 */
	static <V, E> Result<V, E> success(final V value) {
		return new Success<V, E>(value);
	}

	/**
	 * Returns a new Failure instance containing the given error.
	 *
	 * @param error the error
	 * @param <V> The type of the value
	 * @param <E> The type of the error
	 * @return see above
	 */
	static <V, E> Result<V, E> failure(final E error) {
		return new Failure<V, E>(error);
	}

	/**
	 * Returns the value of this instance as an {@link Optional}. Returns Optional.empty()
	 * if this is an Failure instance.
	 * @return see above.
	 */
	Optional<V> getValue();
	
	/**
	 * Returns the error of this instance as an {@link Optional}. Returns Optional.empty()
	 * if this is an Ok instance.
	 * @return see above.
	 */
	Optional<E> getError();
	
	/**
	 * Returns <code>true</code> if this instance represents an Ok value, false otherwise.
	 * @return see above.
	 */
	boolean isSuccess();
	
	/**
	 * Returns <code>true</code> if this instance represents an Failure value, false otherwise.
	 * @return see above
	 */
	boolean isFailure();

	/**
	 * Returns the contained value if this is an Ok value, otherwise throws a ResultException.
	 * @return the contained value
	 * @throws ResultException in case unwrap() is called on an Failure value
	 */
	V unwrap() throws ResultException;

	/**
	 * Express the expectation that this object is an Ok value. If it's an Failure value
	 * instead, throw a ResultException with the given message.
	 * @param message the message to pass to a potential ResultException
	 * @throws ResultException if unwrap() is called on an Failure value
	 */
	void expect(String message) throws ResultException;
	
	/**
	 * If this is an Ok value, andThen() returns the result of the given {@link Function}.
	 * Otherwise returns this.
	 * 
	 * @param lambda The {@link Function} to be called with the value of this.
	 * @param <U> The new value type.
	 * @return see above.
	 */
	default <U> Result<U, E> andThen(final Function<V, Result<U, E>> lambda) {
		return getValue()
			.map(lambda::apply)
			.orElseGet(() -> {
				@SuppressWarnings("unchecked")
				final Result<U, E> ret = (Result<U, E>) this;
				return ret;
			});
	}
	
	/**
	 * If this is an Ok value, map() returns the result of the given {@link Function}, wrapped
	 * in a new Ok Result instance. Otherwise returns this.
	 * 
	 * @param lambda The {@link Function} to call with the value of this.
	 * @param <U> The new value type.
	 * @return see above.
	 */
	default <U> Result<U, E> map(final Function<V, U> lambda) {
		return getValue()
			.map(v -> Result.<U, E>success(lambda.apply(v)))
			.orElseGet(() -> {
				@SuppressWarnings("unchecked")
				final Result<U, E> ret = (Result<U, E>) this;
				return ret;
			});
	}

	/**
	 * If this is an Failure value, mapFailure() returns the result of the given @{link Function}, wrapped
	 * in a new Failure Result instance. Otherwise returns this.
	 *
	 * @param lambda The {@link Function} to call with the error of this.
	 * @param <F>  The new error type.
     * @return see above
     */
	default <F> Result<V, F> mapFailure(final Function<E, F> lambda) {
		return getError()
			.map(e -> Result.<V, F>failure(lambda.apply(e)))
			.orElseGet(() -> {
				@SuppressWarnings("unchecked")
				final Result<V, F> ret = (Result<V, F>) this;
				return ret;
			});
	}

	/**
	 * Apply effects of the given @{link Consumer} on value and error
	 *
	 * @param onSuccess The {@link Consumer} to call with the value of this.
	 * @param onFailure The {@link Consumer} to call with the error of this.
	 */
	default void apply(final Consumer<V> onSuccess, final Consumer<E> onFailure) {
		this.getValue().ifPresent(onSuccess);
		this.getError().ifPresent(onFailure);
	}

}
