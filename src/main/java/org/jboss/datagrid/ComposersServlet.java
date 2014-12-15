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
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.Cache;

/**
 * A simple servlet requesting the cache for key "hello".
 * 
 * @author Pete Muir
 * 
 */
@SuppressWarnings("serial")
@WebServlet("/getComposers")
public class ComposersServlet extends HttpServlet {

    private static final String PAGE_HEADER = "<html><head /><body>";
    private static final String PAGE_FOOTER = "</body></html>";

    @Inject
    private Logger logger;

    @Inject
    EmbeddedCacheManager manager;

    @Inject
    Cache<String, String> cache;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
    	long starttime=0;
    	long endtime=0;
    	
    	resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        Set<String> set = cache.keySet();
        
        writer.println("<br/>Printing the composer for each entry....<br/>");
        for(String key : set){
        	
        	XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            InputSource source = new InputSource(new StringReader(cache.get(key)));
            
            try {
				String composer = (String) xpath.evaluate("/score-partwise/identification/creator[@type='composer']", source,XPathConstants.STRING);
				if(composer.length()>2)
					writer.println(composer+ "<br/>");
				else
					writer.println("***Not Found!***<br/>");
				
			} catch (XPathExpressionException e) {
				logger.info("File: "+key);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        	
        }
        
        System.gc();
        try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        writer.println("<br/>Getting each item seperately...<br/>");
        for(String key : set){
        	starttime=System.nanoTime();
     		cache.get(key);
        	endtime=System.nanoTime();
        	long totaltime = (endtime - starttime);
        	float milliseconds=totaltime;
        	milliseconds /= 1000000;
        	writer.println("Item took " + totaltime + " nanoseconds to retrieve    (" + String.format("%.09f", milliseconds) +" milliseconds)<br/>" );
        }
            	
        writer.close();
    }
    
}
