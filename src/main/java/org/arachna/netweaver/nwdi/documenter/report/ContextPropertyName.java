/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

/**
 * Global properties for Confluence content generation.
 * 
 * @author Dirk Weigenand
 */
public enum ContextPropertyName {
    WikiSpace("wikiSpace"),
    ProjectUrl("projectUrl");

    private String name;

    ContextPropertyName(String name) {
        this.name = name;
    }

    String getName() {
        return this.name;
    }
}
