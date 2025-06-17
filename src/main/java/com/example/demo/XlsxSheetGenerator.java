package com.example.demo;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.lang.NonNull;


/**
 * Class used to create Workbook sheets.
 *
 * @author Mikhail Kataranov
 * @since 07.02.2024
 */
public class XlsxSheetGenerator {

    /**
     * Sheet types.
     */
    public enum OBJECT_TYPE implements ObjectType {
        METADATA("mappings"), // Currently used for mappings, but may include other metadata in the future
        ENTITY("E<"),
        NESTED("N<"),
        RELATION("R<"),
        RELATION_NESTED("RN<"),
        COMPLEX_ATTRIBUTE("CA<"),
        MEASURED_UNIT("MU<");

        OBJECT_TYPE(String value) {
            this.value = value;
        }

        private final String value;

        @Override
        public String value() {
            return value;
        }
    }

    /**
     * The Constant H_R_HEADER_IDX.
     */
    public static final int TITLE_ROW_IDX = 0;
    /**
     * The Constant H_R_PATH_DELIMITER.
     */
    public static final String H_R_PATH_DELIMITER = " >> ";

    private static final String METADATA_TYPE_EX_MESSAGE =
            "Sheets generated for the OBJECT_TYPE.METADATA type require special handling. Use class "
                    + "MetadataSheetGenerator to generate sheets of the OBJECT_TYPE.METADATA type instead";

    private static final String RELATION_NESTED_TYPE_EX_MESSAGE =
            "Sheets generated for the OBJECT_TYPE.RELATION_NESTED type require additional parameters. Use class "
                    + "RelationNestedSheetGenerator to generate sheets of the OBJECT_TYPE.RELATION_NESTED type instead";

    private static final short INIT_SHEET_NUMBER = 0;

    /**
     * Number of characters allocated in the xlsx sheet name for the identifying name.
     *
     * @deprecated No longer needed, due to sheet name mappings via metadata
     */
    @Deprecated(forRemoval = true, since = "6.13")
    protected static final short SHEET_NAME_LENGTH = 26;

    protected static final short MAX_SHEET_NAME_LENGTH = 31;

    private static final short MAX_ELEMENT_NAME_LENGTH = 8;

    private static final String ABBREVIATION_MARKER = "~";

    private static final String SHEET_PREFIX = "S";

    /**
     * The Workbook.
     */
    protected final Workbook workbook;

    /**
     * The sheet type. Used when creating a sheet name.
     */
    protected final OBJECT_TYPE sheetType;

    /**
     * Xlsx metadata.
     */
    protected final XlsxMetadata metadata;

    /**
     * The sheet counter. Used when creating a sheet name.
     */
    protected short sheetCounter;

    /**
     * Constructor.
     *
     * @param workbook  the workbook for which worksheets are created
     * @param sheetType the sheet type. Used when creating a sheet name
     * @deprecated use {@link XlsxSheetGenerator#XlsxSheetGenerator(Workbook, OBJECT_TYPE, XlsxMetadata)} instead
     */
    @Deprecated(forRemoval = true, since = "6.13")
    protected XlsxSheetGenerator(Workbook workbook, OBJECT_TYPE sheetType) {
        this.workbook = workbook;
        this.sheetType = sheetType;
        this.metadata = XlsxMetadata.EMPTY;
        this.sheetCounter = INIT_SHEET_NUMBER;
    }

    /**
     * Constructor.
     *
     * @param workbook  the workbook for which worksheets are created
     * @param sheetType the sheet type. Used when creating a sheet name
     * @param metadata  the metadata
     */
    protected XlsxSheetGenerator(Workbook workbook, OBJECT_TYPE sheetType, XlsxMetadata metadata) {
        this.workbook = workbook;
        this.sheetType = sheetType;
        this.metadata = metadata;
        this.sheetCounter = INIT_SHEET_NUMBER;
    }

    /**
     * Fabric method.
     *
     * @param workbook  the workbook for which worksheets are created
     * @param sheetType the sheet type. Used when creating a sheet name
     * @return new instance
     * @throws IllegalArgumentException if sheetType == OBJECT_TYPE.RELATION_NESTED.
     *                                  Use RelationNestedSheetGenerator for this type
     * @deprecated use {@link XlsxSheetGenerator#create(Workbook, OBJECT_TYPE, XlsxMetadata)} instead
     */
    @Deprecated(forRemoval = true, since = "6.13")
    public static XlsxSheetGenerator create(Workbook workbook, OBJECT_TYPE sheetType) {

        if (sheetType == OBJECT_TYPE.METADATA) {
            throw new IllegalArgumentException(METADATA_TYPE_EX_MESSAGE);
        }

        if (sheetType == OBJECT_TYPE.RELATION_NESTED) {
            throw new IllegalArgumentException(RELATION_NESTED_TYPE_EX_MESSAGE);
        }

        return new XlsxSheetGenerator(workbook, sheetType);
    }

    /**
     * Fabric method.
     *
     * @param workbook  the workbook for which worksheets are created
     * @param sheetType the sheet type. Used when creating a sheet name
     * @param metadata  the metadata
     * @return new instance
     * @throws IllegalArgumentException if sheetType == OBJECT_TYPE.RELATION_NESTED.
     *                                  Use RelationNestedSheetGenerator for this type
     */
    @NonNull
    public static XlsxSheetGenerator create(@NonNull Workbook workbook,
                                            @NonNull OBJECT_TYPE sheetType,
                                            @NonNull XlsxMetadata metadata) {

        if (sheetType == OBJECT_TYPE.METADATA) {
            throw new IllegalArgumentException(METADATA_TYPE_EX_MESSAGE);
        }

        if (sheetType == OBJECT_TYPE.RELATION_NESTED) {
            throw new IllegalArgumentException(RELATION_NESTED_TYPE_EX_MESSAGE);
        }

        return new XlsxSheetGenerator(workbook, sheetType, metadata);
    }

    /**
     * @return the workbook for which worksheets are created
     */
    public Workbook getWorkbook() {
        return workbook;
    }

    /**
     * @return the sheet type. Used when creating a sheet name
     */
    public OBJECT_TYPE getSheetType() {
        return sheetType;
    }

    /**
     * Gets metadata.
     *
     * @return metadata.
     */
    public XlsxMetadata getMetadata() {
        return metadata;
    }

    /**
     * @return the sheet counter. Used when creating a sheet name
     */
    public short getSheetCounter() {
        return sheetCounter;
    }

    /**
     * @param sheetCounter the sheet counter. Used when creating a sheet name
     */
    public void setSheetCounter(short sheetCounter) {
        this.sheetCounter = sheetCounter;
    }

    /**
     * Creates new sheet with a special generated name based on the specified name.
     *
     * @param name the specified name from which the sheet name will be generated
     * @return new sheet for workbook with generated name
     * @deprecated use {@link XlsxSheetGenerator#generate(String, String)} instead
     */
    @Deprecated(forRemoval = true, since = "6.13")
    public Sheet generateWithName(String name) {
        sheetCounter++;
        return this.workbook.createSheet(sheetNameFrom(name));
    }

    /**
     * Generates a new sheet in the workbook. Uses an abbreviated version of the display name as the actual sheet name
     * (to comply with XLSX sheet names limitations)
     *
     * @param name        the base name that will be combined with sheet type and sheet counter
     * @param displayName the display name that will be used as actual sheet name after abbreviation
     * @return new sheet
     */
    public Sheet generate(String name, String displayName) {
        sheetCounter++;

        String systemName = generateSystemName(name);
        String fullDisplayName = generateFullDisplayName(displayName);
        String shortDisplayName = generateShortDisplayName(displayName);

        XlsxMetadata.MetadataField metadataField = new XlsxMetadata.MetadataField()
                .withValue(systemName)
                .withDescription(fullDisplayName);

        metadata.put(shortDisplayName, metadataField);

        return workbook.createSheet(shortDisplayName);
    }

    /**
     * Identifies sheet based on the specified name.
     *
     * @param name the specified name
     * @return Optional<Sheet> or Optional.empty() if sheet identification was not successful
     */
    public Optional<Sheet> identifySheetByName(String name) {
        String nameWithPrefix = getSheetNamePrefix() + name;
        return identifySheet(workbook, metadata, sheetType.value(), nameWithPrefix);
    }

    /**
     * Identifies sheet.
     *
     * @param workbook        the workbook
     * @param metadata        the metadata
     * @param sheetNamePrefix the sheet name prefix
     * @param name            the specified name
     * @return {@code Optional<Sheet>} or {@link Optional#empty()} if not found
     */
    @NonNull
    public static Optional<Sheet> identifySheet(@NonNull Workbook workbook,
                                                @NonNull XlsxMetadata metadata,
                                                String sheetNamePrefix,
                                                String name) {

        Objects.requireNonNull(workbook, "Workbook cannot be null");
        Objects.requireNonNull(metadata, "Metadata cannot be null");

        for (Sheet sheet : workbook) {
            if (checkSheetName(sheet, metadata, sheetNamePrefix) && checkSheetTitle(sheet, name)) {
                return Optional.of(sheet);
            }
        }

        return Optional.empty();
    }

    private static boolean checkSheetName(Sheet sheet, XlsxMetadata metadata, String prefix) {

        if (prefix == null) {
            return false;
        }

        String shortName = sheet.getSheetName();
        String fullName = metadata.contains(shortName)
                ? metadata.get(shortName).getValue()
                : null;

        return fullName == null
                ? shortName.startsWith(prefix) // For old format without metadata
                : fullName.startsWith(prefix);
    }

    private static boolean checkSheetTitle(Sheet sheet, String name) {
        String title = sheet.getRow(TITLE_ROW_IDX).getCell(0).getStringCellValue();
        return name != null && name.equals(StringUtils.substringBefore(title, H_R_PATH_DELIMITER));
    }

    /**
     * Identifies sheet.
     *
     * @param workbook        the Workbook
     * @param sheetNamePrefix the sheet name prefix
     * @param name            the specified name
     * @return Optional<Sheet> or Optional.empty() if sheet identification was not successful
     * @deprecated use {@link XlsxSheetGenerator#identifySheet(Workbook, XlsxMetadata, String, String)} instead
     */
    @Deprecated(forRemoval = true, since = "6.13")
    public static Optional<Sheet> identifySheet(Workbook workbook, String sheetNamePrefix, String name) {
        for (Sheet sheet : workbook) {
            if (sheet.getSheetName().startsWith(sheetNamePrefix)
                    && name.equals(StringUtils.substringBefore(
                    sheet.getRow(TITLE_ROW_IDX).getCell(0).getStringCellValue(), H_R_PATH_DELIMITER))) {
                return Optional.of(sheet);
            }
        }

        return Optional.empty();
    }

    /**
     * @return the sheet name prefix for current sheet type
     */
    public String getSheetNamePrefix() {
        return "";
    }

    /**
     * Generates specific xlsx sheet name based on the specified name.
     *
     * @param name the specified name
     * @return the specific xlsx sheet name based on the specified name
     * @deprecated use {@link XlsxSheetGenerator#generateSystemName(String)} instead
     */
    @Deprecated(forRemoval = true, since = "6.13")
    protected String sheetNameFrom(String name) {
        StringBuilder sheetName = new StringBuilder();
        sheetName
                .append(sheetType.value())
                .append(name);

        sheetName.setLength(Math.min(SHEET_NAME_LENGTH - 1, sheetName.length()));

        sheetName
                .append(">")
                .append(sheetCounter);

        return sheetName.toString();
    }

    /**
     * Generates a system name for the sheet
     * by combining sheet type prefix, provided base name and current sheet counter.
     *
     * @param name the base name
     * @return sheet system name
     */
    protected String generateSystemName(String name) {
        return generateSystemName(sheetType.value(), name, sheetCounter);
    }

    /**
     * Generates a system name for the sheet
     * by combining sheet type prefix, provided base name and current sheet counter.
     *
     * @param prefix  the sheet type prefix
     * @param name    the base name
     * @param counter the sheet counter
     * @return sheet system name
     */
    public static String generateSystemName(String prefix, String name, int counter) {
        return new StringBuilder()
                .append(prefix)
                .append(name)
                .append(">")
                .append(counter)
                .toString();
    }

    /**
     * Generates a full display name for the sheet
     * by combining a unique sheet prefix with the original display name.
     *
     * @param displayName the original display name
     * @return sheet display name
     */
    protected String generateFullDisplayName(String displayName) {
        return generateFullDisplayName(workbook, displayName);
    }

    /**
     * Generates a full display name for the sheet
     * by combining a unique sheet prefix with the original display name.
     *
     * @param workbook    the workbook
     * @param displayName the original display name
     * @return sheet display name
     */
    public static String generateFullDisplayName(Workbook workbook, String displayName) {
        return new StringBuilder()
                .append(buildUniqueSheetPrefix(workbook))
                .append(StringUtils.SPACE)
                .append(displayName)
                .toString();
    }

    /**
     * Generates a shortened display name
     * by combining a unique sheet prefix with an abbreviated version of the original display name
     * (to comply with XLSX sheet names limitations).
     *
     * @param displayName the original display name to abbreviate
     * @return the abbreviated and length-limited sheet display name
     */
    protected String generateShortDisplayName(String displayName) {
        return generateShortDisplayName(workbook, displayName);
    }

    /**
     * Generates a shortened display name
     * by combining a unique sheet prefix with an abbreviated version of the original display name
     * (to comply with XLSX sheet names limitations).
     *
     * @param workbook    the workbook
     * @param displayName the original display name to abbreviate
     * @return the abbreviated and length-limited sheet display name
     */
    public static String generateShortDisplayName(Workbook workbook, String displayName) {
        StringBuilder result = new StringBuilder()
                .append(buildUniqueSheetPrefix(workbook))
                .append(StringUtils.SPACE)
                .append(abbreviateElements(displayName));

        result.setLength(Math.min(MAX_SHEET_NAME_LENGTH, result.length()));

        return result.toString();
    }

    private static String buildUniqueSheetPrefix(Workbook workbook) {
        return new StringBuilder()
                .append(SHEET_PREFIX)
                .append(workbook.getNumberOfSheets() + 1)
                .toString();
    }

    private static String abbreviateElements(String displayName) {
        return Arrays.stream(displayName.split("\\."))
                .map(element -> element.length() > MAX_ELEMENT_NAME_LENGTH
                        ? element.substring(0, MAX_ELEMENT_NAME_LENGTH - ABBREVIATION_MARKER.length())
                        + ABBREVIATION_MARKER
                        : element)
                .collect(Collectors.joining("."));
    }
}
