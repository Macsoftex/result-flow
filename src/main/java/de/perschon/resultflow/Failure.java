/*
 * Copyright 2017 René Perschon <rperschon85@gmail.com>
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
import java.util.function.Function;

/**
 * This class represents the Failure side of @{link Result}.
 *
 * @param <V> The value type
 * @param <E> The error type
 */
public class Failure<V, E> implements Result<V, E> {
	private final E error;

	/**
	 * Constructor.
	 * @param error the error
	 */
	Failure(final E error) {
		super();
		this.error = error;
	}

	@Override
	public Optional<V> getValue() {
		return Optional.empty();
	}

	@Override
	public Optional<E> getError() {
		return Optional.of(error);
	}

	@Override
	public boolean isSuccess() {
		return false;
	}

	@Override
	public boolean isFailure() {
		return true;
	}

	@Override
	public V unwrap() {
		throw new ResultException("Cannot call unwrap() on an Failure value");
	}

	@Override
	public void expect(final String message) throws ResultException {
		throw new ResultException(message);
	}

	@Override
	public Result<V, E> flatMapOrElse(Function<V, Result<V, E>> onSuccess, Function<E, Result<V, E>> onFailure) {
		return onFailure.apply(error);
	}

	@Override
	public String toString() {
		return String.format("Err(%s)", error);
	}
}
