/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webservices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
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

    public VirtualInterfaceDefinitionProvider(final AntHelper antHelper) {
        this.antHelper = antHelper;
    }

    public List<VirtualInterfaceDefinition> execute(DevelopmentComponent component) {
        List<VirtualInterfaceDefinition> interfaces = new ArrayList<VirtualInterfaceDefinition>();
        // String base = antHelper.getBaseLocation(component);
        if (DevelopmentComponentType.Java.equals(component.getType())) {
            VirtualInterfaceDefinitionReader videfReader = new VirtualInterfaceDefinitionReader();

            for (String sourceFolder : component.getSourceFolders()) {
                FileFinder finder = new FileFinder(new File(sourceFolder), ".*\\.videf");

                for (File videf : finder.find()) {
                    try {
                        interfaces.add(videfReader.read(new FileReader(videf)));
                    }
                    catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (SAXException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        return interfaces;
    }
}
