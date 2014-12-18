package link.omny.decisions.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import link.omny.decisions.model.Definitions;

public class DecisionModelFactory {

    public DecisionModelFactory() {
    }

    public void write(Definitions dm, File f) throws IOException,
            JAXBException {
        Writer writer = new FileWriter(f);
        JAXBContext context = JAXBContext.newInstance(Definitions.class);
        Marshaller m = context.createMarshaller();
        m.marshal(dm, writer);
    }

    public Definitions load(String resourceName) throws IOException {
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream("/ApplicationRiskRating.dmn");
            return load(is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
        throw new IOException("Unable to load decision model from "
                + resourceName);
    }

    public Definitions load(InputStream inputStream) throws IOException {
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(Definitions.class);
            Unmarshaller um = context.createUnmarshaller();
            JAXBElement<Definitions> dm = (JAXBElement<Definitions>) um
                    .unmarshal(inputStream);
            return dm.getValue();
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        throw new IOException("Unable to load decision model from stream.");
    }
}
