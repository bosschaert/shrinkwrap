/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Configuration;
import org.jboss.shrinkwrap.api.ConfigurationBuilder;
import org.jboss.shrinkwrap.api.Domain;
import org.jboss.shrinkwrap.api.ExtensionLoader;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrapService;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.ServiceExtensionLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="david@redhat.com">David Bosschaert</a>
 */
public class ShrinkWrapServiceImplTestCase
{
   private static final URL aResource = ShrinkWrapServiceImplTestCase.class.getResource(
         "/" + ShrinkWrapServiceImplTestCase.class.getName().replace('.', '/') + ".class");
   private ClassLoader prevTCCL;

   @Before
   public void setUp() 
   {
      prevTCCL = Thread.currentThread().getContextClassLoader();

      // Set the thread context classloader to be some other classloader
      // that can't see the shrinkwrap classes.
      // ShrinkWrap uses the TCCL so this makes sure that the ShrinkWrapService
      // sets the TCCL appropriately.
      ClassLoader dummyClassLoader = new URLClassLoader(new URL[] {}, null);
      Thread.currentThread().setContextClassLoader(dummyClassLoader);
      try
      {
         dummyClassLoader.loadClass(ShrinkWrapServiceImpl.class.getName());
         fail("Precondition");
      }
      catch (ClassNotFoundException cnfe)
      {
         // good
      }
   }
   
   @After
   public void tearDown()
   {
      Thread.currentThread().setContextClassLoader(prevTCCL);
   }

   @Test
   public void testCreateDomain()
   {
      ShrinkWrapService sws = new ShrinkWrapServiceImpl();
      Domain d = sws.createDomain();
      assertNotNull(d);
   }

   @Test
   public void testCreateDomainWithConfigurationBuilder()
   {
      ShrinkWrapService sws = new ShrinkWrapServiceImpl();
      ConfigurationBuilder builder = new ConfigurationBuilder();
      ExtensionLoader el = (ExtensionLoader)Proxy.newProxyInstance(getClass().getClassLoader(),
            new Class[] { ExtensionLoader.class }, new InvocationHandler()
         {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
            {
               return null;
            }
         });
      builder.extensionLoader(el);
      ExecutorService es = Executors.newSingleThreadExecutor();
      builder.executorService(es);
      
      Domain d = sws.createDomain(builder);
      assertSame(el, d.getConfiguration().getExtensionLoader());
      assertSame(es, d.getConfiguration().getExecutorService());
   }

   @Test
   public void testCreateDomainWithConfiguration()
   {
      ShrinkWrapService sws = new ShrinkWrapServiceImpl();
      ConfigurationBuilder builder = new ConfigurationBuilder();
      ExtensionLoader el = (ExtensionLoader)Proxy.newProxyInstance(getClass().getClassLoader(),
            new Class[] { ExtensionLoader.class }, new InvocationHandler()
         {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
            {
               return null;
            }
         });
      builder.extensionLoader(el);
      ExecutorService es = Executors.newSingleThreadExecutor();
      builder.executorService(es);

      Configuration configuration = builder.build();
      Domain d = sws.createDomain(configuration);
      assertSame(el, d.getConfiguration().getExtensionLoader());
      assertSame(es, d.getConfiguration().getExecutorService());
   }

   @Test
   public void testGetDefaultCreateDomain()
   {
      ShrinkWrapService sws = new ShrinkWrapServiceImpl();
      Domain domain = sws.getDefaultDomain();
      assertNotNull("A special executor service should be set which takes into account setting the TCCL its threads.",
            domain.getConfiguration().getExecutorService());
      assertTrue(domain.getConfiguration().getExtensionLoader() instanceof ServiceExtensionLoader);
   }

   @Test
   public void testCreateArchive() throws Exception
   {
      ShrinkWrapService sws = new ShrinkWrapServiceImpl();
      JavaArchive archive = sws.create(JavaArchive.class);
      String resourceName = "/my/target/file.ext";
      archive.addResource(aResource, resourceName);
      ZipExporter ze = archive.as(ZipExporter.class);
      ZipInputStream zis = new ZipInputStream(ze.exportAsInputStream());

      List<String> entries = getZipFileNames(zis);
      assertEquals(1, entries.size());
      assertEquals(resourceName, "/" + entries.get(0));
   }

   @Test
   public void testCreateArchiveWithName() throws Exception
   {
      ShrinkWrapService sws = new ShrinkWrapServiceImpl();
      JavaArchive archive = sws.create(JavaArchive.class, "myarchive");
      String resourceName = "/my/target/somefile.ext";
      archive.addResource(aResource, resourceName);
      ZipExporter ze = archive.as(ZipExporter.class);
      ZipInputStream zis = new ZipInputStream(ze.exportAsInputStream());
      
      List<String> entries = getZipFileNames(zis);
      assertEquals(1, entries.size());
      assertEquals(resourceName, "/" + entries.get(0));
   }

   @Test
   public void testCreateFromZipFile() throws Exception
   {
      URL fileURL = getClass().getResource("/org/jboss/shrinkwrap/impl/base/osgi/test.jar");
      File file = new File(fileURL.getFile());
      ShrinkWrapService sws = new ShrinkWrapServiceImpl();
      JavaArchive archive = sws.createFromZipFile(JavaArchive.class, file);
      
      List<String> files = new ArrayList<String>();
      Map<ArchivePath, Node> content = archive.getContent();
      for (Node value : content.values())
         if (value.getAsset() != null)
            files.add(value.getPath().get());

      assertEquals(1, files.size());
      assertEquals("/a/b/c/d.txt", files.get(0));
   }

   private List<String> getZipFileNames(ZipInputStream zis) throws IOException
   {
      List<String> fileNames = new ArrayList<String>();
      ZipEntry entry = null;
      while ((entry = zis.getNextEntry()) != null)
      {
         String name = entry.getName();
         if (!name.endsWith("/")) // Don't add directories
            fileNames.add(name);
      }
      return fileNames;
   }
}
