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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.Cache;

/**
 * A simple servlet requesting the cache for key "hello".
 * 
 * @author Pete Muir
 * 
 */
@SuppressWarnings("serial")
@WebServlet("/getXML")
public class getXML extends HttpServlet {

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
        
    	resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        writer.println(PAGE_HEADER);
        
        writer.println("<form action=\"\" method=\"post\" >");
        writer.println("Enter the key (file name) to load:<br/>");
        writer.println("<input id=\"filename\" type=\"text\" name=\"filename\"><br/>");
        writer.println("<input type=\"submit\" value=\"Ready Set Go!!!\"/><br/>");
        writer.println("</form>");

        writer.println(PAGE_FOOTER);
        writer.close();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        
    	resp.setContentType("text/xml");
        PrintWriter writer = resp.getWriter();
        
        String filename = request.getParameter("filename");
        
        long starttime=System.nanoTime();
    	String contents = cache.get(filename);
    	long endtime=System.nanoTime();
    	
    	writer.println(contents);
    	logger.info("loading XML file took " + ((endtime - starttime) ) + " nanoseconds to retrieve");
    	
        writer.close();
        
    }

}
