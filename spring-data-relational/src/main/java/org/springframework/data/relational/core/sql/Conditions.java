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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.util.Assert;

/**
 * Factory for common {@link Condition}s.
 *
 * @author Mark Paluch
 * @author Jens Schauder
 * @since 1.1
 * @see SQL
 * @see Expressions
 * @see Functions
 */
public abstract class Conditions {

	private static final String ERROR_MESSAGE = "Comparison column or expression must not be null";
	private static final String ERROR_MESSAGE_EX =  "Expression argument must not be null";



	/**
	 * Creates a plain {@code sql} {@link Condition}.
	 *
	 * @param sql the SQL, must not be {@literal null} or empty.
	 * @return a SQL {@link Expression}.
	 */
	public static Condition just(String sql) {
		return new ConstantCondition(sql);
	}

	/**
	 * Creates a nested {@link Condition} that is enclosed with parentheses. Useful to combine {@code AND} and {@code OR}
	 * statements.
	 *
	 * @param condition the nested condition.
	 * @return a {@link NestedCondition}.
	 * @since 2.0
	 */
	public static Condition nest(Condition condition) {
		return new NestedCondition(condition);
	}

	/**
	 * Creates a {@code IS NULL} condition.
	 *
	 * @param expression the expression to check for nullability, must not be {@literal null}.
	 * @return the {@code IS NULL} condition.
	 */
	public static IsNull isNull(Expression expression) {
		return IsNull.create(expression);
	}

	/**
	 * Creates a {@code =} (equals) {@link Condition}.
	 *
	 * @param leftColumnOrExpression left side of the comparison.
	 * @param rightColumnOrExpression right side of the comparison.
	 * @return the {@link Comparison} condition.
	 */
	public static Comparison isEqual(Expression leftColumnOrExpression, Expression rightColumnOrExpression) {
		return Comparison.create(leftColumnOrExpression, "=", rightColumnOrExpression);
	}

	/**
	 * Creates a {@code !=} (not equals) {@link Condition}.
	 *
	 * @param leftColumnOrExpression left side of the comparison.
	 * @param rightColumnOrExpression right side of the comparison.
	 * @return the {@link Comparison} condition.
	 */
	public static Comparison isNotEqual(Expression leftColumnOrExpression, Expression rightColumnOrExpression) {
		return Comparison.create(leftColumnOrExpression, "!=", rightColumnOrExpression);
	}

	/**
	 * Creates a {@code <} (less) {@link Condition} comparing {@code left} is less than {@code right}.
	 *
	 * @param leftColumnOrExpression left side of the comparison.
	 * @param rightColumnOrExpression right side of the comparison.
	 * @return the {@link Comparison} condition.
	 */
	public static Comparison isLess(Expression leftColumnOrExpression, Expression rightColumnOrExpression) {
		return Comparison.create(leftColumnOrExpression, "<", rightColumnOrExpression);
	}

	/**
	 * Creates a {@code <=} (less or equal to) {@link Condition} comparing {@code left} is less than or equal to
	 * {@code right}.
	 *
	 * @param leftColumnOrExpression left side of the comparison.
	 * @param rightColumnOrExpression right side of the comparison.
	 * @return the {@link Comparison} condition.
	 */
	public static Comparison isLessOrEqualTo(Expression leftColumnOrExpression, Expression rightColumnOrExpression) {
		return Comparison.create(leftColumnOrExpression, "<=", rightColumnOrExpression);
	}

	/**
	 * Creates a {@code <=} (greater ) {@link Condition} comparing {@code left} is greater than {@code right}.
	 *
	 * @param leftColumnOrExpression left side of the comparison.
	 * @param rightColumnOrExpression right side of the comparison.
	 * @return the {@link Comparison} condition.
	 */
	public static Comparison isGreater(Expression leftColumnOrExpression, Expression rightColumnOrExpression) {
		return Comparison.create(leftColumnOrExpression, ">", rightColumnOrExpression);
	}

	/**
	 * Creates a {@code <=} (greater or equal to) {@link Condition} comparing {@code left} is greater than or equal to
	 * {@code right}.
	 *
	 * @param leftColumnOrExpression left side of the comparison.
	 * @param rightColumnOrExpression right side of the comparison.
	 * @return the {@link Comparison} condition.
	 */
	public static Comparison isGreaterOrEqualTo(Expression leftColumnOrExpression, Expression rightColumnOrExpression) {
		return Comparison.create(leftColumnOrExpression, ">=", rightColumnOrExpression);
	}

	/**
	 * Creates a {@code LIKE} {@link Condition}.
	 *
	 * @param leftColumnOrExpression left side of the comparison.
	 * @param rightColumnOrExpression right side of the comparison.
	 * @return the {@link Comparison} condition.
	 */
	public static Like like(Expression leftColumnOrExpression, Expression rightColumnOrExpression) {
		return Like.create(leftColumnOrExpression, rightColumnOrExpression);
	}

	/**
	 * Creates a {@code IN} {@link Condition clause}.
	 *
	 * @param columnOrExpression left side of the comparison.
	 * @param arg IN argument.
	 * @return the {@link In} condition.
	 */
	public static In inn(Expression columnOrExpression, Expression arg) {

		return In.create(columnOrExpression, arg);
	}

	/**
	 * Creates a new {@link In} {@link Condition} given left and right {@link Expression}s.
	 *
	 * @param columnOrExpression left hand side of the {@link Condition} must not be {@literal null}.
	 * @param expressions right hand side (collection {@link Expression}) must not be {@literal null}.
	 * @return the {@link In} {@link Condition}.
	 */
	public static Condition in(Expression columnOrExpression, Collection<? extends Expression> expressions) {

		Assert.notNull(columnOrExpression, ERROR_MESSAGE);
		Assert.notNull(expressions, ERROR_MESSAGE_EX);

		return In.create(columnOrExpression, new ArrayList<>(expressions));
	}

	/**
	 * Creates a new {@link In} {@link Condition} given left and right {@link Expression}s.
	 *
	 * @param columnOrExpression left hand side of the {@link Condition} must not be {@literal null}.
	 * @param expressions right hand side (collection {@link Expression}) must not be {@literal null}.
	 * @return the {@link In} {@link Condition}.
	 */
	public static In in(Expression columnOrExpression, Expression... expressions) {

		Assert.notNull(columnOrExpression, ERROR_MESSAGE);
		Assert.notNull(expressions, ERROR_MESSAGE_EX);

		return In.create(columnOrExpression, Arrays.asList(expressions));
	}

	/**
	 * Creates a {@code IN} {@link Condition clause} for a {@link Select subselect}.
	 *
	 * @param column the column to compare.
	 * @param subselect the subselect.
	 * @return the {@link In} condition.
	 */
	public static In in(Column column, Select subselect) {

		Assert.notNull(column, "Column must not be null");
		Assert.notNull(subselect, "Subselect must not be null");

		return in(column, new SubselectExpression(subselect));
	}

	/**
	 * Creates a {@code NOT IN} {@link Condition clause}.
	 *
	 * @param columnOrExpression left side of the comparison.
	 * @param arg IN argument.
	 * @return the {@link In} condition.
	 */
	public static In notIn(Expression columnOrExpression, Expression arg) {

		Assert.notNull(columnOrExpression, ERROR_MESSAGE);
		Assert.notNull(arg, ERROR_MESSAGE_EX);

		return In.create(columnOrExpression, arg);
	}

	/**
	 * Creates a new {@code NOT IN} {@link Condition} given left and right {@link Expression}s.
	 *
	 * @param columnOrExpression left hand side of the {@link Condition} must not be {@literal null}.
	 * @param expressions right hand side (collection {@link Expression}) must not be {@literal null}.
	 * @return the {@link In} {@link Condition}.
	 */
	public static Condition notIn(Expression columnOrExpression, Collection<? extends Expression> expressions) {

		Assert.notNull(columnOrExpression, ERROR_MESSAGE);
		Assert.notNull(expressions, ERROR_MESSAGE_EX);

		return In.createNotIn(columnOrExpression, new ArrayList<>(expressions));
	}

	/**
	 * Creates a new {@code NOT IN} {@link Condition} given left and right {@link Expression}s.
	 *
	 * @param columnOrExpression left hand side of the {@link Condition} must not be {@literal null}.
	 * @param expressions right hand side (collection {@link Expression}) must not be {@literal null}.
	 * @return the {@link In NOT IN} {@link Condition}.
	 */
	public static In notIn(Expression columnOrExpression, Expression... expressions) {

		Assert.notNull(columnOrExpression, ERROR_MESSAGE);
		Assert.notNull(expressions, ERROR_MESSAGE_EX);

		return In.createNotIn(columnOrExpression, Arrays.asList(expressions));
	}

	/**
	 * Creates a {@code NOT IN} {@link Condition clause} for a {@link Select subselect}.
	 *
	 * @param column the column to compare.
	 * @param subselect the subselect.
	 * @return the {@link In NOT IN} condition.
	 */
	public static In notIn(Column column, Select subselect) {

		Assert.notNull(column, "Column must not be null");
		Assert.notNull(subselect, "Subselect must not be null");

		return notIn(column, new SubselectExpression(subselect));
	}

	static class ConstantCondition extends AbstractSegment implements Condition {

		private final String condition;

		ConstantCondition(String condition) {
			this.condition = condition;
		}

		@Override
		public String toString() {
			return condition;
		}

		@Override
		public boolean equals(Object other) {
			if (other == null || this.getClass() != other.getClass()) {
    		return false;
			}
			ConstantCondition altra = (ConstantCondition) other;
			return condition.equals(altra.condition);
		}

		@Override
		public int hashCode() {
			return condition.hashCode();
		}
	}

	// Utility constructor.
	private Conditions() {}
}
