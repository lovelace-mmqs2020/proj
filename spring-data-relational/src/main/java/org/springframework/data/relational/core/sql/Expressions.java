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
package org.springframework.data.relational.core.sql;

/**
 * Factory for common {@link Expression}s.
 *
 * @author Mark Paluch
 * @author Jens Schauder
 * @since 1.1
 * @see SQL
 * @see Conditions
 * @see Functions
 */
public abstract class Expressions {

	private static Expression asterisk = new SimpleExpression("*");

	/**
	 * @return a new asterisk {@code *} expression.
	 */
	public static Expression asterisk() {
		return asterisk;
	}

	/**
	 * Creates a plain {@code sql} {@link Expression}.
	 *
	 * @param sql the SQL, must not be {@literal null} or empty.
	 * @return a SQL {@link Expression}.
	 */
	public static Expression just(String sql) {
		return new SimpleExpression(sql);
	}

	/**
	 * @return a new {@link Table}.scoped asterisk {@code <table>.*} expression.
	 */
	public static Expression asterisk(Table table) {
		return table.asterisk();
	}

	@Override
	public boolean equals(Object obj) {
    if (obj == null || this.getClass() != obj.getClass()) return false;
    return true;
  }

	@Override
	public int hashCode() {
		return 1;
	}

	// Utility constructor.
	private Expressions() {}

	public static class SimpleExpression extends AbstractSegment implements Expression {

		private final String expression;

		SimpleExpression(String expression) {
			this.expression = expression;
		}

		@Override
		public String toString() {
			return expression;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
    	if (obj == null) return false;
    	if (this.getClass() != obj.getClass()) return false;

    	SimpleExpression other = (SimpleExpression) obj;
    	return expression.equals(other.expression);
  	}

		@Override
		public int hashCode() {
			return expression.hashCode();
		}
	}
}
