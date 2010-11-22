/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.shrinkwrap.api;

import java.io.File;

import org.jboss.shrinkwrap.api.importer.ArchiveImportException;

/**
 * A service API into the ShrinkWrap system. This API can be used by
 * module systems and service containers such as OSGi and MSC to access
 * ShrinkWrap. The API follows the API provided in {@link ShrinkWrap}.
 *  
 * @author <a href="david@redhat.com">David Bosschaert</a>
 */
public interface ShrinkWrapService
{
   /**
    * See {@link ShrinkWrap#createDomain()}.
    */
   Domain createDomain();

   /**
    * See {@link ShrinkWrap#createDomain(ConfigurationBuilder)}.
    */
   Domain createDomain(final ConfigurationBuilder builder) throws IllegalArgumentException;

   /**
    * See {@link ShrinkWrap#createDomain(Configuration)}. 
    */
   Domain createDomain(final Configuration configuration) throws IllegalArgumentException;

   /**
    * See {@link ShrinkWrap#getDefaultDomain()}.
    */
   Domain getDefaultDomain();

   /**
    * See {@link ShrinkWrap#create(Class)}.
    */
   <T extends Assignable> T create(final Class<T> type)
         throws IllegalArgumentException, UnknownExtensionTypeException;

   /**
    * See {@link ShrinkWrap#create(Class, String)}.
    */
   <T extends Assignable> T create(final Class<T> type, final String archiveName)
         throws IllegalArgumentException, UnknownExtensionTypeException;

   /**
    * See {@link ShrinkWrap#createFromZipFile(Class, File)}.
    */
   <T extends Assignable> T createFromZipFile(final Class<T> type, final File archiveFile)
         throws IllegalArgumentException, ArchiveImportException;
}
