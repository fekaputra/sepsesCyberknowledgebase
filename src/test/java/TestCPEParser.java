import ac.at.tuwien.ifs.sepses.parser.Parser;
import ac.at.tuwien.ifs.sepses.parser.impl.CPEParser;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.validation.ValidationUtil;
import org.topbraid.shacl.vocabulary.SH;

import java.io.*;
import java.util.Properties;

public class TestCPEParser {

    private static final Logger log = LoggerFactory.getLogger(TestCPEParser.class);

    private static Properties properties = new Properties();
    private static String endpoint;
    private static Parser parser;
    private static Model constraints = ModelFactory.createDefaultModel();
    private static String outputDir;
    private static String shaclResult;

    @BeforeClass public static void beforeClass() throws IOException {
        FileInputStream ip = new FileInputStream("config.properties");
        properties.load(ip);
        endpoint = properties.getProperty("SparqlEndpoint");
        outputDir = properties.getProperty("OutputDir") + "/cpe/";
        shaclResult = outputDir + "cpe-shacl-result.ttl";
        File file = new File(outputDir);
        file.mkdirs();
        parser = new CPEParser(properties);
        InputStream is = TestCPEParser.class.getClassLoader().getResourceAsStream("shacl/cpe.ttl");
        RDFDataMgr.read(constraints, is, Lang.TURTLE);
    }

    @Test public void testCPEConfig() {
        ParameterizedSparqlString query = new ParameterizedSparqlString("ASK WHERE { ?s ?p ?o }");
        QueryExecution queryExecution = QueryExecutionFactory.sparqlService(endpoint, query.asQuery());
        queryExecution.execAsk();
    }

    @Test public void testCPEParse() throws IOException {
        Long start = System.currentTimeMillis() / 1000;
        log.info("CPE constraint check starts");

        Model model = parser.getModelFromLastUpdate();
        parser.saveModelToFile(model);
        Resource result = ValidationUtil.validateModel(model, constraints, false);
        RDFDataMgr.write(new FileOutputStream(shaclResult), result.getModel(), Lang.TURTLE);

        Long end = System.currentTimeMillis() / 1000;
        log.info("CPE constraint check finished in " + (end - start) + " seconds");
        Assert.assertTrue(result.getModel().contains(null, SH.conforms, ResourceFactory.createTypedLiteral(true)));
    }

}