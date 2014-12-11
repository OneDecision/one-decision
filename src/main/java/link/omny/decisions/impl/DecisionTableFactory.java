package link.omny.decisions.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import link.omny.decisions.model.DecisionTable;
import link.omny.decisions.model.Definitions;

public class DecisionTableFactory {

    public DecisionTableFactory() {
    }

    public void write(Definitions dm, File f) throws IOException,
            JAXBException {
        Writer writer = new FileWriter(f);
        JAXBContext context = JAXBContext.newInstance(DecisionTable.class);
        Marshaller m = context.createMarshaller();
        m.marshal(dm, writer);
    }
}
