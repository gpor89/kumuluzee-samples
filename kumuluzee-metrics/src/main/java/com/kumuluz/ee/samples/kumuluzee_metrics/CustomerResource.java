/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
*/
package com.kumuluz.ee.samples.kumuluzee_metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.annotation.Gauge;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Metric;
import com.codahale.metrics.annotation.Timed;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("customers")
@ApplicationScoped
public class CustomerResource {

    @Inject
    @Metric(name = "customer_counter")
    private Counter customerCounter;

    @Inject
    @Metric(name = "first_name_length_histogram")
    private Histogram nameLength;

    @Inject
    @Metric(name = "customer_adding_meter")
    private Meter addMeter;

    @GET
    public Response getAllCustomers() {
        List<Customer> customers = Database.getCustomers();
        getCustomerCount();
        return Response.ok(customers).build();
    }

    @GET
    @Path("{customerId}")
    public Response getCustomer(@PathParam("customerId") int customerId) {
        Customer customer = Database.getCustomer(customerId);
        nameLength.update(customer.getFirstName().length());
        return customer != null
                ? Response.ok(customer).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("add-sample-names")
    @Timed(name = "add-sample-names-timer")
    public Response addSampleNames() {
        Database.addCustomer(new Customer(Database.getCustomers().size(), "Daniel", "Ornelas"));
        Database.addCustomer(new Customer(Database.getCustomers().size(), "Dennis", "McBride"));
        Database.addCustomer(new Customer(Database.getCustomers().size(), "Walter", "Wright"));
        Database.addCustomer(new Customer(Database.getCustomers().size(), "Mitchell", "Kish"));
        Database.addCustomer(new Customer(Database.getCustomers().size(), "Tracy", "Edwards"));
        addMeter.mark(5);
        customerCounter.inc(5);

        return Response.noContent().build();
    }

    @POST
    public Response addNewCustomer(Customer customer) {
        addMeter.mark();
        customerCounter.inc();
        Database.addCustomer(customer);
        return Response.noContent().build();
    }

    @Gauge(name = "customer_count_gauge")
    private int getCustomerCount() {
        return Database.getCustomers().size();
    }

    @DELETE
    @Path("{customerId}")
    @Metered(name = "customer_deleting_meter")
    public Response deleteCustomer(@PathParam("customerId") int customerId) {
        customerCounter.dec();
        Database.deleteCustomer(customerId);
        return Response.noContent().build();
    }
}
