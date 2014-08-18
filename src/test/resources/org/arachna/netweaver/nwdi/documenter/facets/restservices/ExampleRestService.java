/**
 * 
 */
package de.gisa.customer.retention.scheme.rest.api;

import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import de.gisa.customer.retention.control.dto.GenericResponse;
import de.gisa.customer.retention.control.filter.InactiveStepFilter;
import de.gisa.customer.retention.control.interfaces.ICustomer;
import de.gisa.customer.retention.control.interfaces.ICustomerRetentionControl;
import de.gisa.customer.retention.control.interfaces.IProcess;
import de.gisa.customer.retention.control.types.Client;
import de.gisa.customer.retention.control.types.ResponseCode;
import de.gisa.customer.retention.control.types.SchemeType;
import de.gisa.customer.retention.scheme.rest.api.dto.CustomerDTO;
import de.gisa.customer.retention.scheme.rest.api.dto.Environment;
import de.gisa.lib.ejb.servicelocator.ServiceLocator;

/**
 * This is an example REST service to testing purposes.
 * 
 * @author weigo
 */
@Path("/rest")
public class ProcessControl {
    /**
     * This is an example method using a mix of path and query parameters as well as consumes and produces tags.
     * 
     * @param client
     *            the example 'client' parameter.
     * @param process
     *            the example 'process' parameter.
     * @return This comment should detail the return value one should expect from calling this method.
     */
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{client}/{process}")
    public Response getCustomerProcess(@PathParam("client") String client, @PathParam("process") final String process,
        @QueryParam("hashCode") String hashCode) {
        return Response.status(Status.OK).build();
    }
}
