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

import org.jboss.shrinkwrap.api.ShrinkWrapService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * OSGi Bundle Activator. Registers a {@link ShrinkWrapService} with the OSGi
 * Service Registry.
 *  
 * @author <a href="david@redhat.com">David Bosschaert</a>
 */
public class Activator implements BundleActivator
{
   ServiceRegistration reg;

   public synchronized void start(BundleContext context) throws Exception
   {
      ShrinkWrapService sws = new ShrinkWrapServiceImpl();
      reg = context.registerService(ShrinkWrapService.class.getName(), sws, null);
   }

   public synchronized void stop(BundleContext context) throws Exception
   {
      reg.unregister();
   }
}
