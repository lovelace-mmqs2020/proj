/*
 * Copyright 2018-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jdbc.core.mapping;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.springframework.lang.Nullable;

/**
 * A reference to the aggregate root of a different aggregate.
 *
 * @param <T> the type of the referenced aggregate root.
 * @param <I> the type of the id of the referenced aggregate root.
 * @author Jens Schauder
 * @author Myeonghyeon Lee
 * @since 1.0
 */
public interface AggregateReference<T, I> {

	static <T, I> AggregateReference<T, I> to(I id) {
		return new IdOnlyAggregateReference<>(id);
	}

	/**
	 * @return the id of the referenced aggregate. May be {@code null}.
	 */
	@Nullable
	I getId();

	/**
	 * An {@link AggregateReference} that only holds the id of the referenced aggregate root. Note that there is no check
	 * that a matching aggregate for this id actually exists.
	 *
	 * @param <T>
	 * @param <ID>
	 */
	@RequiredArgsConstructor
	@EqualsAndHashCode
	@ToString
	class IdOnlyAggregateReference<T, I> implements AggregateReference<T, I> {

		private final I id;

		@Override
		public I getId() {
			return id;
		}
	}
}
