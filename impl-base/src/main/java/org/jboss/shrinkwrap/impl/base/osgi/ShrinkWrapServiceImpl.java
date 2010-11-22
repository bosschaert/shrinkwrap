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
package org.jboss.shrinkwrap.impl.base.osgi;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.Configuration;
import org.jboss.shrinkwrap.api.ConfigurationBuilder;
import org.jboss.shrinkwrap.api.Domain;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.ShrinkWrapService;
import org.jboss.shrinkwrap.api.UnknownExtensionTypeException;
import org.jboss.shrinkwrap.api.importer.ArchiveImportException;
import org.jboss.shrinkwrap.impl.base.ServiceExtensionLoader;

/**
 * An implementation of the {@link ShrinkWrapService} for use in an OSGi
 * environment.
 *  
 * @author <a href="david@redhat.com">David Bosschaert</a>
 */
public class ShrinkWrapServiceImpl implements ShrinkWrapService
{
   public Domain createDomain()
   {
      ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
      try
      {
         Thread.currentThread().setContextClassLoader(ShrinkWrapServiceImpl.class.getClassLoader());
         return ShrinkWrap.createDomain();
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(prevCl);
      }
   }

   public Domain createDomain(ConfigurationBuilder builder) throws IllegalArgumentException
   {
      ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
      try
      {
         Thread.currentThread().setContextClassLoader(ShrinkWrapServiceImpl.class.getClassLoader());
         return ShrinkWrap.createDomain(builder);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(prevCl);
      }
   }

   public Domain createDomain(Configuration configuration) throws IllegalArgumentException
   {
      ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
      try
      {
         Thread.currentThread().setContextClassLoader(ShrinkWrapServiceImpl.class.getClassLoader());
         return ShrinkWrap.createDomain(configuration);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(prevCl);
      }
   }

   public Domain getDefaultDomain()
   {
      ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
      try
      {
         Thread.currentThread().setContextClassLoader(ShrinkWrapServiceImpl.class.getClassLoader());
         return getDefaultDomainImpl();
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(prevCl);
      }
   }

   private Domain getDefaultDomainImpl()
   {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.extensionLoader(new ServiceExtensionLoader(ShrinkWrapServiceImpl.class.getClassLoader()));
      builder.executorService(Executors.newSingleThreadExecutor(new ThreadFactoryWithContext()));
      return ShrinkWrap.createDomain(builder);
   }

   public <T extends Assignable> T create(Class<T> type) throws IllegalArgumentException, UnknownExtensionTypeException
   {
      ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
      try
      {         
         Thread.currentThread().setContextClassLoader(ShrinkWrapServiceImpl.class.getClassLoader());
         return getDefaultDomainImpl().getArchiveFactory().create(type);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(prevCl);
      }
   }

   public <T extends Assignable> T create(Class<T> type, String archiveName) throws IllegalArgumentException, UnknownExtensionTypeException
   {
      ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
      try
      {
         Thread.currentThread().setContextClassLoader(ShrinkWrapServiceImpl.class.getClassLoader());
         return getDefaultDomainImpl().getArchiveFactory().create(type);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(prevCl);
      }
   }

   public <T extends Assignable> T createFromZipFile(Class<T> type, File archiveFile) throws IllegalArgumentException, ArchiveImportException
   {
      ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
      try
      {
         Thread.currentThread().setContextClassLoader(ShrinkWrapServiceImpl.class.getClassLoader());
         return getDefaultDomainImpl().getArchiveFactory().createFromZipFile(type, archiveFile);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(prevCl);
      }
   }

   /** 
    * A ThreadFactory that sets the Thread Context ClassLoader to the classloader that loaded this
    * class.
    */
   private static class ThreadFactoryWithContext implements ThreadFactory
   {
      public Thread newThread(Runnable r)
      {
         Thread thread = new Thread(r);
         thread.setContextClassLoader(ShrinkWrapServiceImpl.class.getClassLoader());
         return thread;
      }
   }
}
