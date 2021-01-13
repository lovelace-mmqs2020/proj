/*
 * Copyright 2019-2020 the original author or authors.
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
package org.springframework.data.relational.core.dialect;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

/**
 * Unit tests for {@link SqlServerDialect}.
 *
 * @author Mark Paluch
 */
public class SqlServerDialectUnitTests {

	@Test // DATAJDBC-278
	public void shouldNotSupportArrays() {

		ArrayColumns arrayColumns = SqlServerDialect.INSTANCE.getArraySupport();

		assertThat(arrayColumns.isSupported()).isFalse();
		assertThatThrownBy(() -> arrayColumns.getArrayType(String.class)).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test // DATAJDBC-278
	public void shouldRenderLimit() {

		LimitClause limit = SqlServerDialect.INSTANCE.limit();

		assertThat(limit.getClausePosition()).isEqualTo(LimitClause.Position.AFTER_ORDER_BY);
		assertThat(limit.getLimit(10)).isEqualTo("OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY");
	}

	@Test // DATAJDBC-278
	public void shouldRenderOffset() {

		LimitClause limit = SqlServerDialect.INSTANCE.limit();

		assertThat(limit.getOffset(10)).isEqualTo("OFFSET 10 ROWS");
	}

	@Test // DATAJDBC-278
	public void shouldRenderLimitOffset() {

		LimitClause limit = SqlServerDialect.INSTANCE.limit();

		assertThat(limit.getLimitOffset(20, 10)).isEqualTo("OFFSET 10 ROWS FETCH NEXT 20 ROWS ONLY");
	}
}
