package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.apache.poi.ss.usermodel.Workbook; // Added this import
import com.example.demo.XlsxSheetGenerator; // Assuming this is the correct package
import com.example.demo.XlsxMetadata; // Assuming this is the correct package


import static org.junit.jupiter.api.Assertions.assertEquals;
// Add other static imports for assertions if needed

@ExtendWith(MockitoExtension.class)
class ComplexAttributeNamesUtilsTest {

    private static final String ATTR_NAME = "attributeName";
    private static final String NESTED_ATTR_NAME = "nestedAttributeName";
    private static final String RELATION_NAME = "relationName";
    private static final String EXPECTED_SHEET_PREFIX = ""; // As getSheetNamePrefix returns ""

    @Mock
    private Workbook mockWorkbook;

    @Mock
    private XlsxMetadata mockMetadata;

    private XlsxSheetGenerator sheetGenerator;

    @BeforeEach
    void setUp() {
        // It seems XlsxSheetGenerator might need workbook and metadata.
        // Based on the previous file content, XlsxSheetGenerator constructor took these.
        // Also, it had a getSheetNamePrefix method. Let's mock that.
        sheetGenerator = new XlsxSheetGenerator(mockWorkbook, mockMetadata);
        // If XlsxSheetGenerator is not mockable or if we need to test its actual getSheetNamePrefix,
        // this might need adjustment. For now, assuming it's a concrete class we instantiate.
        // If getSheetNamePrefix() itself needs mocking, sheetGenerator should be a mock.
        // For now, we are testing ComplexAttributeNamesUtils, so direct instantiation of XlsxSheetGenerator is fine.
        // Let's assume getSheetNamePrefix() on the actual XlsxSheetGenerator returns "" as per EXPECTED_SHEET_PREFIX
        // If XlsxSheetGenerator.getSheetNamePrefix() is not guaranteed to be "", we might need to mock sheetGenerator
        // or use Mockito.when(mockMetadata.getSheetNamePrefix()).thenReturn(EXPECTED_SHEET_PREFIX); if prefix comes from metadata
        // For now, the constant EXPECTED_SHEET_PREFIX = "" suggests a known behavior.
    }

    @Test
    void testComplexAttrName_twoArgs() {
        String expected = ATTR_NAME + ComplexAttributeNamesUtils.COMPLEX_TO_NESTED_DELIMITER + NESTED_ATTR_NAME;
        String actual = ComplexAttributeNamesUtils.complexAttrName(ATTR_NAME, NESTED_ATTR_NAME);
        assertEquals(expected, actual);
    }

    @Test
    void testComplexAttrName_threeArgs_withRelationName() {
        String expected = RELATION_NAME + ComplexAttributeNamesUtils.COMPLEX_TO_NESTED_DELIMITER +
                          ATTR_NAME + ComplexAttributeNamesUtils.COMPLEX_TO_NESTED_DELIMITER + NESTED_ATTR_NAME;
        String actual = ComplexAttributeNamesUtils.complexAttrName(ATTR_NAME, NESTED_ATTR_NAME, RELATION_NAME);
        assertEquals(expected, actual);
    }

    @Test
    void testComplexAttrName_threeArgs_withSheetGenerator() {
        // Assuming sheetGenerator.getSheetNamePrefix() returns EXPECTED_SHEET_PREFIX
        // which is currently ""
        String expected = EXPECTED_SHEET_PREFIX +
                          ATTR_NAME + ComplexAttributeNamesUtils.COMPLEX_TO_NESTED_DELIMITER + NESTED_ATTR_NAME;
        String actual = ComplexAttributeNamesUtils.complexAttrName(ATTR_NAME, NESTED_ATTR_NAME, sheetGenerator);
        assertEquals(expected, actual);
    }
}
