package com.logicaldoc.core.conversion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.java.plugin.registry.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentEvent;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Session;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.core.security.UserHistory;
import com.logicaldoc.core.security.dao.TenantDAO;
import com.logicaldoc.core.security.dao.UserHistoryDAO;
import com.logicaldoc.core.store.Storer;
import com.logicaldoc.core.util.DocUtil;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.io.FileUtil;
import com.logicaldoc.util.plugin.PluginRegistry;

/**
 * Manager class used to handle format converters. For each couple
 * inFormat-outFormat you may have different possible converters, they are taken
 * by the <b>FormatConverter</b> extension point. The actual converter uses is
 * the one pointed by the context property names converter.inExt-outExt.
 * 
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.1.3
 */
public class FormatConverterManager {

	/*
	 * The suffix for the document's resource that represents the PDF conversion
	 */
	public static final String PDF_CONVERSION_SUFFIX = "conversion.pdf";

	protected static Logger log = LoggerFactory.getLogger(FormatConverterManager.class);

	private Storer storer;

	private TenantDAO tenantDao;

	private DocumentManager documentManager;

	private ContextProperties config;

	// Key is the src_extension-dst_extension, value is a collection of
	// converters
	private Map<String, List<FormatConverter>> converters = new HashMap<String, List<FormatConverter>>();

	// All the available converters
	private Map<String, FormatConverter> availableConverters = new HashMap<String, FormatConverter>();

	/**
	 * Retrieves the content of the Pdf conversion. If the Pdf conversion is not
	 * available in the store, it is created.
	 * 
	 * @param document The document to be processed
	 * @param fileVersion The file version(optional)
	 * @param sid (optional)
	 * @return The content of the PDF as bytes
	 * @throws IOException
	 */
	public byte[] getPdfContent(Document document, String fileVersion, String sid) throws IOException {
		String resource = storer.getResourceName(document.getId(), getSuitableFileVersion(document, fileVersion),
				PDF_CONVERSION_SUFFIX);
		if ("pdf".equals(FilenameUtils.getExtension(document.getFileName()).toLowerCase()))
			resource = storer.getResourceName(document, getSuitableFileVersion(document, fileVersion), null);
		if (!storer.exists(document.getId(), resource))
			convertToPdf(document, fileVersion, sid);
		return storer.getBytes(document.getId(), resource);
	}

	/**
	 * Write the content of the Pdf conversion into a file. If the Pdf
	 * conversion is not available in the store, it is created.
	 * 
	 * @param document The document to be processed
	 * @param fileVersion The file version(optional)
	 * @param sid (optional)
	 * @throws IOException
	 */
	public void writePdfToFile(Document document, String fileVersion, File out, String sid) throws IOException {
		String resource = storer.getResourceName(document.getId(), getSuitableFileVersion(document, fileVersion),
				PDF_CONVERSION_SUFFIX);
		if ("pdf".equals(FilenameUtils.getExtension(document.getFileName()).toLowerCase()))
			resource = storer.getResourceName(document.getId(), getSuitableFileVersion(document, fileVersion), null);
		if (!storer.exists(document.getId(), resource))
			convertToPdf(document, fileVersion, sid);
		storer.writeToFile(document.getId(), resource, out);
	}

	/**
	 * Creates the pdf for the specified document and file version (suffix
	 * PDF_CONVERSION_SUFFIX). If the Pdf conversion already exists it, nothing
	 * happens.
	 * 
	 * @param document The document to be processed
	 * @param fileVersion The file version(optional)
	 * @param sid (optional)
	 * 
	 * @throws IOException
	 */
	public void convertToPdf(Document document, String fileVersion, String sid) throws IOException {
		String fileName = DocUtil.getFileName(document, fileVersion);

		if ("pdf".equals(FilenameUtils.getExtension(fileName).toLowerCase())) {
			log.debug("Document {} itself is a Pdf", document.getId());
			return;
		}

		String resource = storer.getResourceName(document, getSuitableFileVersion(document, fileVersion),
				PDF_CONVERSION_SUFFIX);

		if (storer.size(document.getId(), resource) > 0L) {
			log.debug("Pdf conversion already available for document {}", document.getId());
			return;
		}

		FormatConverter converter = getConverter(fileName, "pdf");
		if (converter == null)
			return;

		// Prepare I/O files
		File src = null;
		File dest = File.createTempFile("conversion", ".pdf");

		try {
			src = writeToFile(document, fileVersion);
			if (src == null || src.length() == 0)
				throw new IOException(String.format("Unexisting source file,  document: %s - %s", document.getId(),
						fileName));

			converter.convert(sid, document, src, dest);

			if (dest == null || dest.length() == 0)
				throw new IOException(String.format(
						"The converter %s was unable to convert as pdf the document: %s - %s", converter.getClass()
								.getSimpleName(), document.getId(), fileName));

			storer.store(dest, document.getId(), resource);
		} finally {
			// Delete temporary resources
			FileUtil.strongDelete(src);
			FileUtil.strongDelete(dest);
		}
	}

	/**
	 * Shortcut for convertToPdf(document, null, transaction)
	 */
	public void convertToPdf(Document document, String sid) throws IOException {
		convertToPdf(document, null, sid);
	}

	/**
	 * Converts a document into another format and saves the resulting file in
	 * the same folder
	 * 
	 * @param document The document to be processed
	 * @param fileVersion The file version(optional)
	 * @param the target file, the extension of this filename is used to detect
	 *        the output format
	 * @param transaction
	 * @throws Exception
	 */
	public Document convert(Document document, String fileVersion, String format, History transaction) throws Exception {
		String fileName = DocUtil.getFileName(document, fileVersion);
		File out = null;
		FormatConverter converter = null;
		try {
			out = File.createTempFile("conv", "." + format);
			converter = getConverter(fileName, format);

			if (converter == null)
				throw new IOException(String.format("No converter available  for %s into format %s", fileName, format));

			log.debug("Using converter {} for {}", converter.getClass().getSimpleName(), fileName);

			convertToFile(document, fileVersion, out, transaction);
			if (out.exists() && out.length() > 0) {
				Document docVO = new Document();
				docVO.setFileName(FilenameUtils.getBaseName(fileName) + "." + format);
				docVO.setFolder(document.getFolder());
				docVO.setLanguage(document.getLanguage());
				return documentManager.create(out, docVO, transaction);
			} else
				throw new IOException("The conversion was not done");
		} finally {
			// Delete temporary resources
			FileUtil.strongDelete(out);
		}
	}

	/**
	 * Converts a document and writes the content of the conversion into a file.
	 * 
	 * @param document The document to be processed
	 * @param fileVersion The file version(optional)
	 * @param the target file, the extension of this filename is used to detect
	 *        the output format
	 * @param transaction
	 * @throws IOException
	 */
	public void convertToFile(Document document, String fileVersion, File out, History transaction) throws IOException {
		String fileName = DocUtil.getFileName(document, fileVersion);
		FormatConverter converter = getConverter(fileName, out.getName());
		if (converter == null)
			return;

		FileUtil.strongDelete(out);

		// Prepare I/O files
		File src = null;
		try {
			src = writeToFile(document, fileVersion);
			if (src == null || src.length() == 0)
				throw new IOException(String.format("Unexisting source file,  document: %s - %s", document.getId(),
						fileName));

			converter.convert(transaction != null ? transaction.getSessionId() : null, document, src, out);
			if (out.length() <= 0)
				throw new IOException(String.format("The converter %s was unable to convert document: %s", converter
						.getClass().getSimpleName(), document.getId() + " - " + fileName));

			if (transaction != null) {
				transaction.setEvent(DocumentEvent.CONVERTED.toString());
				transaction.setComment("format: " + FilenameUtils.getExtension(out.getName()));
				DocumentDAO dao = (DocumentDAO) Context.get().getBean(DocumentDAO.class);
				dao.saveDocumentHistory(document, transaction);
			}
		} finally {
			// Delete temporary resources
			FileUtil.strongDelete(src);
		}
	}

	/**
	 * Converts a file into a different format
	 * 
	 * @param in Input file (the filename extension determines the source
	 *        format)
	 * @param out Output file (the filename extension determines the output
	 *        format)
	 * @param sid session ID (optional)
	 * 
	 * @throws IOException
	 */
	public void convertFile(File in, String inFileName, File out, String outFormat, String sid) throws IOException {
		FormatConverter converter = getConverter(inFileName, outFormat);
		if (converter == null)
			throw new IOException("Converter not found");

		if (!in.exists() || in.length() == 0)
			throw new IOException(String.format("Unexisting source file %s", in));

		FileUtil.strongDelete(out);

		converter.convert(in, out);

		if (out == null || !out.exists() || out.length() == 0)
			throw new IOException(String.format("Converter %s has not been able to convert file %s", converter
					.getClass().getSimpleName(), in.getPath()));

		// Register this event in the user's history
		if (StringUtils.isNotEmpty(sid)) {
			Session session = SessionManager.get().get(sid);
			if (session != null) {
				UserHistory history = new UserHistory();

				history.setSession(session);
				history.setEvent(UserHistory.EVENT_FILE_CONVERSION);
				history.setFilename(FilenameUtils.getBaseName(inFileName) + "." + outFormat.toLowerCase());
				history.setFilenameOld(inFileName);
				history.setComment(String.format("%s -> %s", history.getFilenameOld(), history.getFilename()));
				history.setIp(session.getClient().getAddress());

				UserHistoryDAO dao = (UserHistoryDAO) Context.get().getBean(UserHistoryDAO.class);
				dao.store(history);
			}
		}
	}

	protected String getTenantName(Document document) {
		String tenantName = "default";
		try {
			Tenant tenant = tenantDao.findById(document.getTenantId());
			tenantName = tenant.getName();
		} catch (Throwable t) {
			log.error(t.getMessage());
		}
		return tenantName;
	}

	/**
	 * Returns the available output formats for the given input format
	 */
	public List<String> getEnabledOutputFormats(String inFileName) {
		String inExt = inFileName;
		if (inFileName.contains("."))
			inExt = FilenameUtils.getExtension(inFileName);
		List<String> formats = new ArrayList<String>();
		for (String key : getConverters().keySet()) {
			String inOut[] = key.split("-");
			FormatConverter assignedConverter = getConverter(inFileName, inOut[1]);
			// The actually assigned converter must be anabled
			if (!formats.contains(inOut[1]) && assignedConverter.isEnabled() && !inExt.equalsIgnoreCase(inOut[1])
					&& inExt.equalsIgnoreCase(inOut[0]))
				formats.add(inOut[1]);
		}
		return formats;
	}

	/**
	 * Returns all the possible output formats for the given input format
	 */
	public List<String> getAllOutputFormats(String inFileName) {
		String inExt = inFileName;
		if (inFileName.contains("."))
			inExt = FilenameUtils.getExtension(inFileName);
		List<String> formats = new ArrayList<String>();
		for (String key : getConverters().keySet()) {
			String inOut[] = key.split("-");
			if (!formats.contains(inOut[1]) && (inExt.equalsIgnoreCase(inOut[0]) || "*".equals(inOut[0]))
					&& !inExt.equalsIgnoreCase(inOut[1]))
				formats.add(inOut[1]);
		}
		return formats;
	}

	/**
	 * Returns all the possible input formats
	 */
	public List<String> getAvailableInputFormats() {
		List<String> formats = new ArrayList<String>();
		for (String key : getConverters().keySet()) {
			String inOut[] = key.split("-");
			if (!formats.contains(inOut[0]) && !"*".equals(inOut[0]))
				formats.add(inOut[0]);
		}
		Collections.sort(formats);
		return formats;
	}

	/**
	 * Returns the list of possible converters for a given in and out format
	 */
	public List<FormatConverter> getAvailableConverters(String inFileName, String outFileName) {
		String key = composeKey(inFileName, outFileName);
		return getConverters().get(key);
	}

	/**
	 * Returns the list of possible converters
	 */
	public Collection<FormatConverter> getAllConverters() {
		return availableConverters.values();
	}

	/**
	 * Loads the proper converter for the passed file names. The right converter
	 * used is defined in the configuration parameter converter.composeKey()
	 */
	public FormatConverter getConverter(String inFileName, String outFileName) {
		if (getExtension(inFileName).equals(getExtension(outFileName)))
			return new NoConversionConverter();

		String inOutkey = composeKey(inFileName, outFileName);

		List<FormatConverter> converters = getConverters().get(inOutkey);
		if (converters == null || converters.isEmpty())
			converters = getConverters().get("*-pdf");
		if (converters == null || converters.isEmpty())
			log.warn("No format converter for file " + inFileName);

		// Get the first available converter
		FormatConverter converter = converters.get(0);

		// Check if a special binding is configured
		String currentConverter = config.getProperty("converter." + inOutkey);
		if (StringUtils.isNotEmpty(currentConverter))
			for (FormatConverter formatConverter : converters) {
				if (formatConverter.getClass().getName().equals(currentConverter)) {
					converter = formatConverter;
				}
			}

		if (converter != null)
			converter.loadParameters();

		return converter;
	}

	private String getExtension(String fileNameOrExtension) {
		String ext = fileNameOrExtension;
		if (fileNameOrExtension.contains("."))
			ext = FilenameUtils.getExtension(fileNameOrExtension.toLowerCase());
		return ext;
	}

	/**
	 * Creates the key as <in_extension>-<out_extension>
	 */
	private String composeKey(String inFileName, String outFileName) {
		String inExt = getExtension(inFileName).toLowerCase();
		String outExt = getExtension(outFileName).toLowerCase();
		return inExt + "-" + outExt;
	}

	/**
	 * Write a document into a temporary file.
	 * 
	 * @throws IOException
	 */
	private File writeToFile(Document document, String fileVersion) throws IOException {
		File target = File.createTempFile("scr", "." + FilenameUtils.getExtension(document.getFileName()));
		String fver = getSuitableFileVersion(document, fileVersion);
		String resource = storer.getResourceName(document, fver, null);
		storer.writeToFile(document.getId(), resource, target);
		return target;
	}

	/**
	 * Returns the fileVersion in case this is not null or
	 * document.getFileVersion() otherwise
	 */
	private String getSuitableFileVersion(Document document, String fileVersion) {
		String fver = fileVersion;
		if (fver == null)
			fver = document.getFileVersion();
		return fver;
	}

	/**
	 * Initializes the converters map
	 */
	private void initConverters() {
		converters.clear();
		// Acquire the 'ThumbnailBuilder' extensions of the core plugin
		PluginRegistry registry = PluginRegistry.getInstance();
		Collection<Extension> exts = registry.getExtensions("logicaldoc-core", "FormatConverter");

		for (Extension ext : exts) {
			String className = ext.getParameter("class").valueAsString();
			String in = ext.getParameter("in").valueAsString().toLowerCase();
			String out = ext.getParameter("out").valueAsString().toLowerCase();
			try {
				Class<?> clazz = Class.forName(className);
				// Try to instantiate the builder
				Object converter = clazz.newInstance();
				if (!(converter instanceof FormatConverter))
					throw new Exception("The specified converter " + className
							+ " doesn't implement FormatConverter interface");

				FormatConverter cnvrt = (FormatConverter) converter;
				for (String name : cnvrt.getParameterNames())
					cnvrt.getParameters().put(name, null);

				String[] ins = in.split(",");
				String[] outs = out.split(",");
				for (String i : ins)
					for (String o : outs) {
						String key = composeKey(i, o);
						if (!converters.containsKey(key))
							converters.put(key, new ArrayList<FormatConverter>());
						if (!converters.get(composeKey(i, o)).contains(cnvrt))
							converters.get(composeKey(i, o)).add(cnvrt);
					}
				log.info("Registered format converter {} for extensions {}", className, in);

				// Save the converter in the list of available converters
				if (!availableConverters.containsKey(cnvrt.getClass().getSimpleName()))
					availableConverters.put(cnvrt.getClass().getSimpleName(), cnvrt);
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	public Map<String, List<FormatConverter>> getConverters() {
		if (converters.isEmpty())
			initConverters();
		return converters;
	}

	public void setStorer(Storer storer) {
		this.storer = storer;
	}

	public void setTenantDao(TenantDAO tenantDao) {
		this.tenantDao = tenantDao;
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public void setConfig(ContextProperties config) {
		this.config = config;
	}
}