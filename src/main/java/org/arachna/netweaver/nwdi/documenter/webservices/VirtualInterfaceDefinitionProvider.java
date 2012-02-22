/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webservices;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.arachna.ant.AntHelper;
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
     * ant helper.
     */
    private final AntHelper antHelper;

    private final VirtualInterfaceDefinitionReader videfReader = new VirtualInterfaceDefinitionReader();

    public VirtualInterfaceDefinitionProvider(final AntHelper antHelper) {
        this.antHelper = antHelper;
    }

    public List<VirtualInterfaceDefinition> execute(final DevelopmentComponent component) {
        final List<VirtualInterfaceDefinition> interfaces = new ArrayList<VirtualInterfaceDefinition>();
        // String base = antHelper.getBaseLocation(component);
        if (DevelopmentComponentType.Java.equals(component.getType())) {
            final MethodDocumentationProvider methodDocumentationProvider = new MethodDocumentationProvider("UTF-8");
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
