package com.mycompany.anosgibundle;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

    private ServiceTracker httpTracker;
    
    public void start(BundleContext context) throws Exception {
        System.out.println("An OSGi Bundle started");
        
        httpTracker = new ServiceTracker(context, HttpService.class.getName(), null) {
            public void removedService(ServiceReference reference, Object service) {
                // HTTP service is no longer available, unregister our servlet...
                try {
                    ((HttpService) service).unregister("/hello");
                } catch (IllegalArgumentException exception) {
                    // Ignore; servlet registration probably failed earlier on...
                }
            }

            public Object addingService(ServiceReference reference) {
                // HTTP service is available, register our servlet...
                System.out.println("Registering hello servlet");
                HttpService httpService = (HttpService) this.context.getService(reference);
                try {
                    httpService.registerServlet("/hello", new HelloWorld(), null, null);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return httpService;
            }
        };
        // start tracking all HTTP services...
        httpTracker.open();
    }

    public void stop(BundleContext context) throws Exception {
        // TODO add deactivation code here
    }

}
