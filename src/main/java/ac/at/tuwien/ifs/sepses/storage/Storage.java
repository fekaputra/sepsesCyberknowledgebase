package ac.at.tuwien.ifs.sepses.storage;

import org.apache.jena.rdf.model.Model;

public interface Storage {

    /**
     * store data within selected triplestore (append into existing data)
     */
    public void storeData(String file, String endpoint, String namegraph, Boolean isUseAuth, String user,
            String pass);

    /**
     * store data within selected triplestore
     */
    public void replaceData(String file, String endpoint, String namegraph, Boolean isUseAuth, String user,
            String pass);

    /**
     * delete data from a named graph
     */
    public void deleteData(String endpoint, String namegraph, Boolean isUseAuth, String user, String pass);

}
