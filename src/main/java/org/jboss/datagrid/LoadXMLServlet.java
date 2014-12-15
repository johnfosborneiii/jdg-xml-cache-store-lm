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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 * 
 * @author John Osborne
 * 
 */
@SuppressWarnings("serial")
@WebServlet("/LoadXMLServlet")
public class LoadXMLServlet extends HttpServlet {

    static String PAGE_HEADER = "<html><head><title>Load XMl Records</title></head><body>";
    static String PAGE_FOOTER = "</body></html>";
    
    @Inject
    private Logger logger;

    @Inject
    EmbeddedCacheManager manager;
    
    @Inject
    Cache<String, String> cache;

    public void init(ServletConfig config) throws ServletException {
    	
    	File folder;
    	ServletContext ctx=config.getServletContext();
    	folder = new File(ctx.getRealPath(Resources.xmlDataDir()));
		//folder = new File(Resources.xmlDataDir());
		logger.info("Using absolute path: " + folder.getAbsolutePath());
		File[] listOfFiles = folder.listFiles(new FilenameFilter() {
 		    public boolean accept(File dir, String name) {
 		        return name.toLowerCase().endsWith(".xml");
 		    }
 	   	});
		
		if(listOfFiles == null || listOfFiles.length == 0)
			logger.info("Did not find any XML files in the " + Resources.jdgProperty(Resources.XML_DIRECTORY) + " directory");
		else{
			logger.info("Found " + listOfFiles.length + " files!");

	 	   	for (File file : listOfFiles) {
	 	   		if (file.isFile()) {
	 	   			//System.out.println(file.getName());
	 	   			try {
						String content = FileUtils.readFileToString(file, "UTF-8");
						logger.info("Putting contents from file " + file.getName() + " into the cache, content length="+content.length());
						cache.put(file.getName(), content);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	 	   		}
	 	   	}
		}
		
		Set<String> set = cache.keySet();
		logger.info("Warming up the cache....");
		
        for(String key : set){
        	for(int i = 0; i < 1000; ++i)
        		cache.get(key);
        }
	        
 	    	   	
    }

}
