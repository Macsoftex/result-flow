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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MapErrTest {

    private final Result<String, Exception> err = Result.failure(new RuntimeException("foo"));
    private final Result<String, Exception> ok = Result.success("bar");

    @Test
    public void mapFailureShouldCallLambda() {
        final Result<String, String> result = err.mapFailure(Throwable::getMessage);
        assertThat(result.getError().get()).isExactlyInstanceOf(String.class);
        assertThat(result.getError().get()).isEqualTo("foo");
    }

    @Test
    public void mapFailureShouldNotCallLambdaWhenItsAnOk() {
        final Result<String, Object> result = ok.mapFailure(e -> {
            throw new RuntimeException("should not be called!");
        });
    }

    @Test
    public void mapFailureShouldReturnThisIfItsAnOk() {
        final Result<String, String> result = ok.mapFailure(e -> "not called");
        assertThat(result).isSameAs(ok);
    }

}
