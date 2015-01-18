/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webapp.restservices;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;

/**
 * A representation of a REST service.
 * 
 * @author Dirk Weigenand
 */
public class RestService {
    private String basePath = "";
    private String description = "";
    private String name = "";

    private final Collection<Method> methods = new LinkedList<Method>();

    void add(final Method method) {
        methods.add(method);
    }

    /**
     * @return the basePath
     */
    public String getBasePath() {
        return basePath;
    }

    /**
     * @param basePath
     *            the basePath to set
     */
    void setBasePath(final String basePath) {
        this.basePath = StringUtils.trimToEmpty(basePath);
    }

    /**
     * @return
     */
    public Collection<Method> getMethods() {
        return Collections.unmodifiableCollection(methods);
    }

    /**
     * @param description
     */
    public void setDescription(final String description) {
        this.description = StringUtils.trimToEmpty(description);
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     */
    @Override
    public String toString() {
        return "RestService [basePath=" + basePath + ", description=" + description + ", methods=" + methods + "]";
    }
}
