package leibooks.domain.metadatareader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import leibooks.app.AppProperties;

/**
 * Enum implementing the factory design pattern for metadata readers.
 * 
 * Depending on the MIME type of a document, this factory selects and returns
 * the appropriate metadata reader implementation.
 * 
 * @author Simão da Luz, fc61816
 */
public enum MetadataReaderFactory {
    INSTANCE;

    // For each MIME type (String) there's a subclass of AMetadataReader.
    private final Map<String, Class<? extends AMetadataReader>> metadataReaders;

    private MetadataReaderFactory() {
        metadataReaders = new HashMap<>();
        metadataReaders.put("application/pdf", PDFMetadataReaderAdapter.class);
    }

    /**
     * Creates an instance of an {@link IMetadataReader} based on the MIME type of
     * the file.
     * 
     * First checks the file to determine its MIME type. If the MIME
     * type is found, it then attempts to find the appropriate
     * {@link AMetadataReader} subclass from a predefined set of readers based on
     * the MIME type. If no matching subclass is found, it tries to create a dynamic
     * metadata reader for the specific MIME type. If that also fails, a generic
     * metadata reader is returned.
     * 
     * @param pathToDocFile The path to the document file for which the metadata reader is to be created.
     * @return An instance of {@link IMetadataReader} associated to the MIME type of
     *         the file, or a {@link GenericMetadataReader} if no specific reader is
     *         found.
     * @throws FileNotFoundException If the specified file cannot be found at the
     *                               provided path.
     */
    public IMetadataReader createMetadataReader(String pathToDocFile) throws FileNotFoundException {
        Path path = Path.of(pathToDocFile);
        String mimeType;

        try {
            mimeType = Files.probeContentType(path); // What's the MIME type file?
        }

        catch (IOException e) {
            return new GenericMetadataReader(pathToDocFile); // If there's an error, a GenericMetadataReader is
                                                             // instantly returned.
        }

        // Searches for an AMetadataReader subclass based on the MIME type.
        Class<? extends AMetadataReader> subclass = metadataReaders.get(mimeType);

        // If nothing is found in the hashmap, a dynamic metadata reader is attempted.
        if (subclass == null) {
            subclass = dynamicMetadataReaderClass(mimeType);
        }

        // If still not found it is given a GenericMetadataReader.
        if (subclass == null) {
            subclass = GenericMetadataReader.class;
        }

        AMetadataReader reader;
        try {
            reader = subclass.getConstructor(String.class).newInstance(pathToDocFile);
        }

        catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException("Failed to instantiate the metadata reader: " + subclass.getName(), e);
        }

        return reader;
    }

    /**
     * Retrieves the class corresponding to the provided MIME type, assuming there's
     * a class that implements a metadata reader for that specific MIME type. The
     * class is dynamically loaded based on the generated class name, and then it is
     * checked if it is a subclass of {@link AMetadataReader}. If it is, the class
     * is returned. Otherwise, it returns {@code null}.
     * 
     * @param mimeType The MIME type for which the metadata reader class will be
     *                 searched.
     * @return The class corresponding to the MIME type, which is a subclass of
     *         {@link AMetadataReader}, or {@code null} if the class is not found or
     *         is not a subclass of {@link AMetadataReader}.
     */
    private Class<? extends AMetadataReader> dynamicMetadataReaderClass(String mimeType) {
        String className = toReaderClassName(mimeType);

        try {
            // Obtains the class corresponding to className.
            Class<?> clazz = Class.forName(className);

            // Yet it still needs to be checked as a subclass of AMetadataReader!;
            if (AMetadataReader.class.isAssignableFrom(clazz)) {
                return clazz.asSubclass(AMetadataReader.class);
            }
        }

        catch (ClassNotFoundException e) {
            // Let the error pass and return null (subclass of AMetadataReader not found).
        }
        return null;
    }

    /**
     * Converts a MIME to the corresponding reader class name.
     *
     * @param mimeType The MIME type to convert.
     * @return String representing the reader class.
     */
    private String toReaderClassName(String mimeType) {
        String[] parts = mimeType.split("/"); // e.g., image/png -> ["image", "png"]
        StringBuilder sb = new StringBuilder();

        for (String part : parts) {
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)); 
        }

        sb.append("MetadataReader");
        return sb.toString();
    }

    /**
     * Retrieves a list of metadata readers from Java files in a specific folder. It
     * scans the folder specified in the application properties for files with the
     * ".java" extension, attempts to load each class dynamically, and checks if the
     * class is a subclass of {@link AMetadataReader}. For each valid class, it adds
     * the corresponding MIME type and class name to the list of metadata readers.
     * 
     * @return A list of strings where each string represents a MIME type and the
     *         corresponding metadata reader class.
     */
    private List<String> getMetadataReaders() {
        List<String> metadataReadersList = new ArrayList<>();
        File folder = new File(AppProperties.INSTANCE.FOLDER_EXTRA_VIEWERS_AND_READERS);

        if (!folder.exists()) {
            return metadataReadersList;
        }

        // Let's go from file to file in the folder.
        for (File file : folder.listFiles()) {

            if (file.getName().endsWith(".java")) {
                String className = file.getName().replace(".java", "");

                try {
                    Class<?> clazz = Class.forName(className);

                    // Is it a subclass of AMetadataReader ?
                    if (AMetadataReader.class.isAssignableFrom(clazz)) {
                        String mimeType = getMimeType(className);
                        metadataReadersList.add(mimeType + "=class " + className);
                    }
                }

                catch (ClassNotFoundException e) {
                    // Let it pass...;
                }
            }
        }

        return metadataReadersList;
    }

    /**
     * Converts a metadata reader class name into its corresponding MIME type.
     * 
     * @param className The class name of the metadata reader in the format
     *                  "AbcXyzMetadataReader".
     * @return The MIME type in the format "abc/xyz"
     */
    private String getMimeType(String className) {
        className = className.replace("MetadataReader", "");

        // Split to divide the class name at the first uppercase letter;
        String[] parts = className.split("(?=[A-Z])");

        return parts[0].toLowerCase() + "/" + parts[1].toLowerCase();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        // First, add the items from the map of known readers
        for (Map.Entry<String, Class<? extends AMetadataReader>> entry : metadataReaders.entrySet()) {

            sb.append(entry.getKey()) // MIME type
                    .append("=class ").append(entry.getValue().getCanonicalName()) // package + className
                    .append(", ");

            // Final: e.g, abc/xyz=class
            // package.domain.metadatareader.AbcXyzMetadataReaderAdapter,
        }

        // Now, the dynamic readers...
        for (String dynamicReader : getMetadataReaders()) {
            sb.append(dynamicReader).append(", ");
        }

        sb.setLength(sb.length() - 2); // Remove the last comma and space.
        sb.append("}");

        return sb.toString();
    }
}
