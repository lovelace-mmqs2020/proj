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
package org.springframework.data.jdbc.core;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.convert.BasicJdbcConverter;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.RelationResolver;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.data.relational.core.conversion.AggregateChange;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.event.AfterDeleteCallback;
import org.springframework.data.relational.core.mapping.event.AfterLoadCallback;
import org.springframework.data.relational.core.mapping.event.AfterSaveCallback;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.mapping.event.BeforeDeleteCallback;
import org.springframework.data.relational.core.mapping.event.BeforeSaveCallback;

/**
 * Unit tests for {@link JdbcAggregateTemplate}.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @author Milan Milanov
 */
@RunWith(MockitoJUnitRunner.class)
public class JdbcAggregateTemplateUnitTests {

	JdbcAggregateOperations template;

	@Mock DataAccessStrategy dataAccessStrategy;
	@Mock ApplicationEventPublisher eventPublisher;
	@Mock RelationResolver relationResolver;
	@Mock EntityCallbacks callbacks;

	@Before
	public void setUp() {

		RelationalMappingContext mappingContext = new RelationalMappingContext(NamingStrategy.INSTANCE);
		JdbcConverter converter = new BasicJdbcConverter(mappingContext, relationResolver);

		template = new JdbcAggregateTemplate(eventPublisher, mappingContext, converter, dataAccessStrategy);
		((JdbcAggregateTemplate) template).setEntityCallbacks(callbacks);

	}

	@Test // DATAJDBC-378
	public void findAllByIdMustNotAcceptNullArgumentForType() {
		assertThatThrownBy(() -> template.findAllById(singleton(23L), null)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test // DATAJDBC-378
	public void findAllByIdMustNotAcceptNullArgumentForIds() {

		assertThatThrownBy(() -> template.findAllById(null, SampleEntity.class))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test // DATAJDBC-378
	public void findAllByIdWithEmpthListMustReturnEmptyResult() {
		assertThat(template.findAllById(emptyList(), SampleEntity.class)).isEmpty();
	}

	@Test // DATAJDBC-393
	public void callbackOnSave() {

		SampleEntity first = new SampleEntity(null, "Alfred");
		SampleEntity second = new SampleEntity(23L, "Alfred E.");
		SampleEntity third = new SampleEntity(23L, "Neumann");

		when(callbacks.callback(any(Class.class), any(), any())).thenReturn(second, third);

		SampleEntity last = template.save(first);

		verify(callbacks).callback(BeforeConvertCallback.class, first);
		verify(callbacks).callback(eq(BeforeSaveCallback.class), eq(second), any(AggregateChange.class));
		verify(callbacks).callback(AfterSaveCallback.class, third);
		assertThat(last).isEqualTo(third);
	}

	@Test // DATAJDBC-393
	public void callbackOnDelete() {

		SampleEntity first = new SampleEntity(23L, "Alfred");
		SampleEntity second = new SampleEntity(23L, "Alfred E.");

		when(callbacks.callback(any(Class.class), any(), any())).thenReturn(second);

		template.delete(first, SampleEntity.class);

		verify(callbacks).callback(eq(BeforeDeleteCallback.class), eq(first), any(AggregateChange.class));
		verify(callbacks).callback(AfterDeleteCallback.class, second);
	}

	@Test // DATAJDBC-393
	public void callbackOnLoad() {

		SampleEntity alfred1 = new SampleEntity(23L, "Alfred");
		SampleEntity alfred2 = new SampleEntity(23L, "Alfred E.");

		SampleEntity neumann1 = new SampleEntity(42L, "Neumann");
		SampleEntity neumann2 = new SampleEntity(42L, "Alfred E. Neumann");

		when(dataAccessStrategy.findAll(SampleEntity.class)).thenReturn(asList(alfred1, neumann1));

		when(callbacks.callback(any(Class.class), eq(alfred1), any())).thenReturn(alfred2);
		when(callbacks.callback(any(Class.class), eq(neumann1), any())).thenReturn(neumann2);

		Iterable<SampleEntity> all = template.findAll(SampleEntity.class);

		verify(callbacks).callback(AfterLoadCallback.class, alfred1);
		verify(callbacks).callback(AfterLoadCallback.class, neumann1);

		assertThat(all).containsExactly(alfred2, neumann2);
	}

	@Test // DATAJDBC-101
	public void callbackOnLoadSorted() {

		SampleEntity alfred1 = new SampleEntity(23L, "Alfred");
		SampleEntity alfred2 = new SampleEntity(23L, "Alfred E.");

		SampleEntity neumann1 = new SampleEntity(42L, "Neumann");
		SampleEntity neumann2 = new SampleEntity(42L, "Alfred E. Neumann");

		when(dataAccessStrategy.findAll(SampleEntity.class, Sort.by("name"))).thenReturn(asList(alfred1, neumann1));

		when(callbacks.callback(any(Class.class), eq(alfred1), any())).thenReturn(alfred2);
		when(callbacks.callback(any(Class.class), eq(neumann1), any())).thenReturn(neumann2);

		Iterable<SampleEntity> all = template.findAll(SampleEntity.class, Sort.by("name"));

		verify(callbacks).callback(AfterLoadCallback.class, alfred1);
		verify(callbacks).callback(AfterLoadCallback.class, neumann1);

		assertThat(all).containsExactly(alfred2, neumann2);
	}

	@Test // DATAJDBC-101
	public void callbackOnLoadPaged() {

		SampleEntity alfred1 = new SampleEntity(23L, "Alfred");
		SampleEntity alfred2 = new SampleEntity(23L, "Alfred E.");

		SampleEntity neumann1 = new SampleEntity(42L, "Neumann");
		SampleEntity neumann2 = new SampleEntity(42L, "Alfred E. Neumann");

		when(dataAccessStrategy.findAll(SampleEntity.class, PageRequest.of(0, 20))).thenReturn(asList(alfred1, neumann1));

		when(callbacks.callback(any(Class.class), eq(alfred1), any())).thenReturn(alfred2);
		when(callbacks.callback(any(Class.class), eq(neumann1), any())).thenReturn(neumann2);

		Iterable<SampleEntity> all = template.findAll(SampleEntity.class, PageRequest.of(0, 20));

		verify(callbacks).callback(AfterLoadCallback.class, alfred1);
		verify(callbacks).callback(AfterLoadCallback.class, neumann1);

		assertThat(all).containsExactly(alfred2, neumann2);
	}

	@Data
	@AllArgsConstructor
	private static class SampleEntity {

		@Column("id1") @Id private Long id;

		private String name;
	}
}
