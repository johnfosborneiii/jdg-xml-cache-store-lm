/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.datagrid;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 * Provides various resources including a cache manager.
 * 
 * @author John Osborne
 * 
 */
public class Resources {

    private static EmbeddedCacheManager ecm;
    private static Cache<String, String> cache;
    
    public static final String XML_DIRECTORY = "data.xml.directory";
	public static final String CACHE_NAME = "jdg.cache.name";
	public static final String PROPERTIES_FILE = "jdg.properties";
	public static final String THREAD_COUNT = "jdg.thread.count";

	@Produces
    Logger getLogger(InjectionPoint ip) {
        String category = ip.getMember().getDeclaringClass().getName();
        return Logger.getLogger(category);
    }

    @Produces
    public EmbeddedCacheManager cacheManager() throws IOException {
    	if(ecm == null) {
    		try {
				ecm = new DefaultCacheManager("infinispan.dist.nocs.xml");
			} catch (IOException e) {
				e.printStackTrace();
			}
    		

    	}
    	return ecm;
    }
    
    public void stopCacheManager(@Disposes EmbeddedCacheManager ecm){
		    
    		System.out.println("***********Stopping the EmbeddedCacheManager***********");
    		ecm.stop();
    	    ecm = null;
		  
	 }
    
    @Produces
    public Cache<String, String> cache() throws IOException {
    	
    	if(cache == null) {
     		cache = cacheManager().getCache(jdgProperty(CACHE_NAME));
    	}
    	
    	return cache;
    }
    

	public static String jdgProperty(String name) {
        Properties props = new Properties();
        try {
            props.load(Resources.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return props.getProperty(name);
    }
	
	public static String xmlDataDir() {
        Properties props = new Properties();
        try {
            props.load(Resources.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return props.getProperty(XML_DIRECTORY);
    }

}