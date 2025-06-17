package com.example.demo;


/**
 * Complex Attribute names util.
 *
 * @author Mikhail Kataranov
 * @since 07.02.2024
 */
public class ComplexAttributeNamesUtils {

    public static final String COMPLEX_TO_NESTED_DELIMITER = ">";

    /**
     * Creates the name of complex attribute. This name can be used as key for map.
     *
     * @param complexAttributeName       the complex attribute name
     * @param complexAttributeNestedName the complex attribute nested name
     * @return the name of complex attribute
     */
    public static String complexAttrName(String complexAttributeName, String complexAttributeNestedName) {
        return new StringBuilder()
                .append(complexAttributeName)
                .append(COMPLEX_TO_NESTED_DELIMITER)
                .append(complexAttributeNestedName)
                .toString();
    }

    /**
     * Creates the name of complex attribute. This name can be used as key for map.
     *
     * @param complexAttributeName       the complex attribute name
     * @param complexAttributeNestedName the complex attribute nested name
     * @param relationName               the name of relation that has a nested attribute
     * @return the name of complex attribute
     */
    public static String complexAttrName(String complexAttributeName, String complexAttributeNestedName,
                                         String relationName) {
        return new StringBuilder()
                .append(relationName)
                .append(COMPLEX_TO_NESTED_DELIMITER)
                .append(complexAttributeName)
                .append(COMPLEX_TO_NESTED_DELIMITER)
                .append(complexAttributeNestedName)
                .toString();
    }

    /**
     * Creates the name of complex attribute. This name can be used as key for map.
     *
     * @param complexAttributeName       the complex attribute name
     * @param complexAttributeNestedName the complex attribute nested name
     * @param sheetGenerator             the {@link XlsxSheetGenerator}
     * @return the name of complex attribute
     */
    public static String complexAttrName(String complexAttributeName, String complexAttributeNestedName,
                                         XlsxSheetGenerator sheetGenerator) {
        return new StringBuilder()
                .append(sheetGenerator.getSheetNamePrefix())
                .append(complexAttributeName)
                .append(COMPLEX_TO_NESTED_DELIMITER)
                .append(complexAttributeNestedName)
                .toString();
    }
}