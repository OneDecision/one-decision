package io.onedecision.engine.decisions.impl;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformUtil {
	private static final TransformerFactory factory = TransformerFactory
			.newInstance();
    protected static final Logger LOGGER = LoggerFactory
            .getLogger(TransformUtil.class);

	public static final String ERROR_KEY = "ERROR";
	public static final String IGNORED_KEY = "IGNORED";
	public static final String PASS_KEY = "PASS";
	private Transformer[] transformers;

	public TransformUtil() {
		super();
		factory.setURIResolver(new ClasspathResourceResolver());
	}

    /**
     * 
     * @param xsltResources
     *            Comma separated list of XSLT classpath resources.
     * @throws TransformerConfigurationException
     */
    public void setXsltResources(String xsltResources)
			throws TransformerConfigurationException {
        LOGGER.info(String.format("Setting up pre-processors %1$s",
                xsltResources));
        String[] resources = xsltResources.split(",");
		transformers = new Transformer[resources.length];
		for (int i = 0; i < resources.length; i++) {
			InputStream is = null;
			try {
				is = getClass().getResourceAsStream(resources[i]);
				transformers[i] = factory.newTransformer(new StreamSource(is));
			} finally {
				try {
					is.close();
				} catch (Exception e) {
					;
				}
			}
		}
	}

    /**
     * @param xml
     *            String serialization of XML.
     * @return output of XSL Transformation.
     */
	public String transform(String xml) {
		return transform(xml, new HashMap<String, String>());
	}

	public String transform(String xml, Map<String, String> params) {
		try {
			for (Transformer t : transformers) {
				Source xmlSource = new StreamSource(
						new StringReader(xml.trim()));
				xml = transformOnce(t, xmlSource, params);
			}
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("transform result: " + xml);
		return xml;
	}

	public String transform(Source xmlSource, Map<String, String> params) {
		String xml = null;
		try {
			for (Transformer t : transformers) {
				xml = transformOnce(t, xmlSource, params);
				xmlSource = new StreamSource(new StringReader(xml.trim()));
			}
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("transform result: " + xml);
		return xml;
	}

	private String transformOnce(Transformer t, Source xmlSource,
			Map<String, String> params) throws TransformerException {
		StringWriter out = new StringWriter();
		Result outputTarget = new StreamResult(out);

		for (Entry<String, String> entry : params.entrySet()) {
			t.setParameter(entry.getKey(), entry.getValue());
		}
		t.transform(xmlSource, outputTarget);
		return out.getBuffer().toString();
	}

	public Map<String, List<String>> parseResults(String result) {
		String[] messages = result.split("\\n", 0);
		System.out.println("messages found: " + messages.length);
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> ignored = new ArrayList<String>();
		List<String> passed = new ArrayList<String>();
		List<String> errors = new ArrayList<String>();
		map.put(ERROR_KEY, errors);
		map.put(IGNORED_KEY, ignored);
		map.put(PASS_KEY, passed);
		for (String string : messages) {
			String msg = string.trim();
			if (msg.length() > 0
					&& msg.toUpperCase().startsWith(TransformUtil.ERROR_KEY)) {
				errors.add(msg);
			} else if (msg.length() > 0
					&& msg.toUpperCase().startsWith(TransformUtil.PASS_KEY)) {
				passed.add(msg);
			} else if (msg.length() > 0
					&& msg.toUpperCase().startsWith(TransformUtil.IGNORED_KEY)) {
				ignored.add(msg);
			}
		}
		System.err.println("ERRORS:" + errors.size());
		System.err.println("IGNORED:" + ignored.size());
		System.err.println("PASSED:" + passed.size());
		return map;
	}

    class ClasspathResourceResolver implements URIResolver {

        @Override
        public Source resolve(String href, String base)
                throws TransformerException {
            System.out.println("Attempt to resolve " + base + href);
            Source s = null;
            try {
                s = new StreamSource(getClass().getResourceAsStream(href));
            } catch (Exception e) {
                // TODO
                e.printStackTrace();
            }
            return s;
        }

    }
}
