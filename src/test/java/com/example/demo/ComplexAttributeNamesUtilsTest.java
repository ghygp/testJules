package com.example.demo;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// These imports were missing in the prompt's template but are necessary
import com.example.demo.XlsxSheetGenerator;
import com.example.demo.XlsxMetadata;
// import static com.example.demo.XlsxSheetGenerator.OBJECT_TYPE; // Not strictly needed if using FQN

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ComplexAttributeNamesUtilsTest {

    private static final String ATTR_NAME = "attributeName";
    private static final String NESTED_ATTR_NAME = "nestedAttributeName";
    private static final String RELATION_NAME = "relationName";
    private static final String EXPECTED_SHEET_PREFIX = "";

    @Mock
    private Workbook mockWorkbook;

    @Mock
    private XlsxMetadata mockMetadata;

    private XlsxSheetGenerator sheetGenerator;

    @BeforeEach
    void setUp() {
        // Ensure OBJECT_TYPE.ENTITY is correctly referenced.
        // It might require an import for XlsxSheetGenerator.OBJECT_TYPE
        // or using the fully qualified name.
        sheetGenerator = XlsxSheetGenerator.create(mockWorkbook, XlsxSheetGenerator.OBJECT_TYPE.ENTITY, mockMetadata);
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
        String expected = EXPECTED_SHEET_PREFIX + ATTR_NAME + ComplexAttributeNamesUtils.COMPLEX_TO_NESTED_DELIMITER + NESTED_ATTR_NAME;
        String actual = ComplexAttributeNamesUtils.complexAttrName(ATTR_NAME, NESTED_ATTR_NAME, sheetGenerator);
        assertEquals(expected, actual);
    }
}
