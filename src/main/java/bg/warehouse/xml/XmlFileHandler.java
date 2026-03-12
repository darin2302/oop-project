package bg.warehouse.xml;

import bg.warehouse.model.Warehouse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.IOException;

public class XmlFileHandler {

    private final JAXBContext context;

    public XmlFileHandler() {
        try {
            context = JAXBContext.newInstance(Warehouse.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to initialize JAXB context", e);
        }
    }

    public Warehouse load(String filePath) throws JAXBException, IOException {
        File file = new File(filePath);

        if (!file.exists()) {
            Warehouse empty = new Warehouse();
            save(empty, filePath);
            return empty;
        }

        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (Warehouse) unmarshaller.unmarshal(file);
    }

    public void save(Warehouse warehouse, String filePath) throws JAXBException, IOException {
        File file = new File(filePath);

        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(warehouse, file);
    }
}
