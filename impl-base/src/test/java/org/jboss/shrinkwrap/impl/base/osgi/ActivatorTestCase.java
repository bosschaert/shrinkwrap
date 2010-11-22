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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author <a href="david@redhat.com">David Bosschaert</a>
 */
public class ActivatorTestCase
{

   private StringBuilder regCalls = new StringBuilder();

   @Test
   public void testOSGiActivator() throws Exception
   {
      BundleContext bc = (BundleContext)Proxy.newProxyInstance(getClass().getClassLoader(),
            new Class[] { BundleContext.class }, new BundleContextInvocationHandler());

      Activator activator = new Activator();
      assertNull("Precondition", activator.reg);

      activator.start(bc);
      assertNotNull(activator.reg);

      activator.stop(bc);
      assertEquals("unregister", regCalls.toString());
   }

   /////////////////////////////////////////////////////////////////////////////////////////
   // Mock objects for this test...
   /////////////////////////////////////////////////////////////////////////////////////////
   private class BundleContextInvocationHandler implements InvocationHandler
   {
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
      {
         if ("registerService".equals(method.getName()))
         {
            return Proxy.newProxyInstance(getClass().getClassLoader(),
                  new Class[] { ServiceRegistration.class }, new ServiceRegistrationInvocationHandler());
         }
         return null;
      }
   }

   private class ServiceRegistrationInvocationHandler implements InvocationHandler
   {
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
      {
         regCalls.append(method.getName());
         return null;
      }
   }
}
