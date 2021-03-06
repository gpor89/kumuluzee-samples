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
package com.kumuluz.ee.samples.graphql_security;

import com.kumuluz.ee.graphql.annotations.GraphQLClass;
import com.kumuluz.ee.security.annotations.Secure;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * @author Benjamin Kastelic
 * @since 2.3.0
 */
@RequestScoped
@GraphQLClass
@Secure
public class CustomerResource {

    @Inject
    private CustomerService customerBean;

    @GraphQLQuery
    @PermitAll
    public List<Customer> getAllCustomers() {
       return customerBean.getCustomers();
    }

    @GraphQLQuery
    @RolesAllowed({"user", "admin"})
    public Customer getCustomer(@GraphQLArgument(name="customerId") String customerId) {
        return customerBean.getCustomer(customerId);
    }

    @GraphQLMutation
    @RolesAllowed("admin")
    public Customer addNewCustomer(@GraphQLArgument(name="customer") Customer customer) {
        customerBean.saveCustomer(customer);
        return customer;
    }

    @GraphQLMutation
    @DenyAll
    public void deleteCustomer(@GraphQLArgument(name="customerId") String customerId) {
        customerBean.deleteCustomer(customerId);
    }
}
