/*
 * Copyright 2012 C24 Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *			http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package biz.c24.io.spring.batch.reader;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.core.io.ClassPathResource;

import biz.c24.io.api.ParserException;
import biz.c24.io.api.data.ComplexDataObject;
import biz.c24.io.api.data.ValidationException;
import biz.c24.io.examples.models.basic.EmployeeElement;
import biz.c24.io.spring.batch.reader.source.BufferedReaderSource;
import biz.c24.io.spring.batch.reader.source.FileSource;
import biz.c24.io.spring.batch.reader.source.ZipFileSource;
import biz.c24.io.spring.core.C24Model;
import biz.c24.io.spring.source.SourceFactory;
import biz.c24.io.spring.source.TextualSourceFactory;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Validate the C24ItemReader
 * 
 * @author Andrew Elmore
 */
public class C24BatchItemReaderTests {
	
	private C24Model employeesXmlModel = new C24Model(biz.c24.io.examples.models.xml.EmployeesElement.getInstance());
	private C24Model employeeXmlModel = new C24Model(biz.c24.io.examples.models.xml.EmployeeElement.getInstance());

	@Test
	public void testValidXmlRead() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException, IOException, ValidationException {
		FileSource source = new FileSource();
		String filename = "employees-3-valid.xml";
		
		Collection<ComplexDataObject> objs = readFile(employeesXmlModel, employeeXmlModel, true, source, filename);
		assertThat(objs.size(), is(3));		
	}
	
	@Test
	public void testSemanticallyInvalidXmlRead() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException, IOException, ValidationException {
		FileSource source = new FileSource();
		String filename = "employees-3-semanticallyinvalid.xml";
		
		try {
			readFile(employeesXmlModel, employeeXmlModel, true, source, filename);
			fail("Semantically invalid file did not generate a C24ValidationException");
		} catch(C24ValidationException ex) {
			// Expected behaviour
		}	
	}
	
	@Test
	public void testStructurallyInvalidXmlRead() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException, IOException, ValidationException {
		FileSource source = new FileSource();
		String filename = "employees-3-structurallyinvalid.xml";
		
		try {
			readFile(employeesXmlModel, employeeXmlModel, true, source, filename);
			fail("Semantically invalid file did not generate a ParseException");
		} catch(ParseException ex) {
			// Expected behaviour
		}	
	}
	

	private Collection<ComplexDataObject> readFile(C24Model batchModel, C24Model batchEntryModel, boolean validate, BufferedReaderSource source, String filename) throws Exception, IOException, UnexpectedInputException, ParseException, NonTransientResourceException, ValidationException { 
		C24BatchItemReader reader = new C24BatchItemReader();
		reader.setModel(batchModel);		
		reader.setSource(source);
		reader.setValidate(validate);
		
		StepExecution stepExecution = getStepExecution(filename);
		
		reader.setup(stepExecution);

		ComplexDataObject obj = null;
		Collection<ComplexDataObject> objs = new LinkedList<ComplexDataObject>();
		
		while((obj = reader.read()) != null) {
			assertThat(obj.getDefiningElementDecl(), is(batchEntryModel.getRootElement()));
			objs.add(obj);
		}
		
		reader.cleanup();
		
		return objs;
	}
		
	private StepExecution getStepExecution(String classPathResourceName) throws IOException {
		
		ClassPathResource sourceFile = new ClassPathResource(classPathResourceName);
		
		JobParameters jobParams = mock(JobParameters.class);
		when(jobParams.getString("input.file")).thenReturn(sourceFile.getFile().getAbsolutePath());

		StepExecution stepExecution = mock(StepExecution.class);
		when(stepExecution.getJobParameters()).thenReturn(jobParams);
		
		return stepExecution;
		
	}
}
