/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webservices;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.netweaver.nwdi.documenter.java.MethodDocumentationProvider;
import org.arachna.util.io.FileFinder;
import org.xml.sax.SAXException;

/**
 * Provider for virtual interface definitions for a given development component.
 * 
 * @author Dirk Weigenand
 */
public class VirtualInterfaceDefinitionProvider {
    /**
     * Reader for <code>.videf</code> files (virtual interface definitions).
     */
    private final VirtualInterfaceDefinitionReader videfReader = new VirtualInterfaceDefinitionReader();

    /**
     * Provider for JavaDoc documentation for a virtual interface.
     */
    private final MethodDocumentationProvider methodDocumentationProvider = new MethodDocumentationProvider("UTF-8");

    /**
     * Read all definitions of virtual interfaces contained in the given
     * development component (iff it's a DC of type Java) and return a list of
     * found {@link VirtualInterfaceDefinition}s.
     * 
     * @param component
     *            the development component to inspect for web service interface
     *            definitions.
     * @return list of found {@link VirtualInterfaceDefinition}s.
     */
    public List<VirtualInterfaceDefinition> execute(final DevelopmentComponent component) {
        final List<VirtualInterfaceDefinition> interfaces = new ArrayList<VirtualInterfaceDefinition>();

        if (DevelopmentComponentType.Java.equals(component.getType())) {
            final List<String> sourceFolders = new ArrayList<String>();
            sourceFolders.addAll(component.getSourceFolders());

            for (final String sourceFolder : component.getSourceFolders()) {
                final FileFinder finder = new FileFinder(new File(sourceFolder), ".*\\.videf");

                for (final File videf : finder.find()) {
                    try {
                        final VirtualInterfaceDefinition virtualInterface = videfReader.read(new FileReader(videf));
                        methodDocumentationProvider.execute(sourceFolders, virtualInterface);
                        interfaces.add(virtualInterface);
                    }
                    catch (final IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (final SAXException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }

        return interfaces;
    }
}
