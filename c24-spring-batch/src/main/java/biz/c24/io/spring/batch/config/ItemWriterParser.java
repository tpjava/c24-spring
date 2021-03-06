/*
 * Copyright 2012 C24 Technologies.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package biz.c24.io.spring.batch.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import biz.c24.io.spring.batch.writer.C24ItemWriter;

/**
 * Parser for the 'C24ItemWriter' element.
 * 
 * @author Andrew Elmore
 */
public class ItemWriterParser extends AbstractSingleBeanDefinitionParser {

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser#getBeanClass(org.w3c.dom.Element)
	 */
	@Override
    protected Class<?> getBeanClass(Element element) {
        return C24ItemWriter.class;
    }

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser#doParse(org.w3c.dom.Element, org.springframework.beans.factory.support.BeanDefinitionBuilder)
	 */
    @Override
    protected void doParse(Element element, BeanDefinitionBuilder bean) {
    
    	// Optional
    	String scope = element.getAttribute("scope");
    	if(StringUtils.hasText(scope)) {
    		bean.setScope(scope);
    	} else {
    		// Default to step scope
    		bean.setScope("step");
    	}
    	
    	// Mandatory
    	String sinkRef = element.getAttribute("sink-ref");
    	bean.addPropertyReference("sink", sinkRef);
    	
    	// Mandatory
    	String writerSourceRef = element.getAttribute("writer-source-ref");
    	bean.addPropertyReference("writerSource", writerSourceRef);

    }    
}
