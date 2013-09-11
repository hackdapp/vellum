/*
 * Source https://code.google.com/p/vellum by @evanxsummers

       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements. See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.  
 */
package vellum.util;

import java.util.Properties;

/**
 *
 * @author evan.summers
 */
public class VellumProperties extends Properties {

    public VellumProperties() {
        super();
    }
    
    public VellumProperties(Properties properties) {
        super.putAll(properties);
    }
    
    public String getString(String propertyName) {
        String propertyValue = super.getProperty(propertyName);
        if (propertyValue == null) {
            throw new RuntimeException("Missing -D property: " + propertyName);
        }
        return propertyValue;
    } 

    public String getString(String propertyName, String defaultValue) {
        String propertyValue = super.getProperty(propertyName);
        if (propertyValue == null) {
            return defaultValue;
        }
        return propertyValue;
    } 
    
    public int getInt(String propertyName) {
        String propertyString = super.getProperty(propertyName);
        if (propertyString == null) {
            throw new RuntimeException("Missing -D property: " + propertyName);
        }
        return Integer.parseInt(propertyString);
    }
    
    public int getInt(String propertyName, int defaultValue) {
        String propertyString = super.getProperty(propertyName);
        if (propertyString == null) {
            return defaultValue;
        }
        return Integer.parseInt(propertyString);
    }
    
    public boolean getBoolean(String propertyName, boolean defaultValue) {
        String string = super.getProperty(propertyName);
        if (string == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(string);
    }    
    
    public char[] getPassword(String propertyName, char[] defaultValue) {
        String string = super.getProperty(propertyName);
        if (string == null) {
            return defaultValue;
        }
        return string.toCharArray();
    }
}
