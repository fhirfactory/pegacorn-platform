/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.fhirfactory.pegacorn.common.model;

import static org.junit.jupiter.api.Assertions.*;

import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.common.model.generalid.RDN;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mark A. Hunter
 * @since 2020-06-16
 *
 */
class FDNTest {
	private static final Logger LOG = LoggerFactory.getLogger(FDNTest.class);

	/**
	 * @throws java.lang.Exception
	 */

	FDN testFDN1;
	FDN testFDN2;

	@BeforeEach
	void setUp() throws Exception {
		LOG.debug(".setUp(): Entry...");
		String testFDNType1 = "TestType1";
		String testFDNType2 = "*#($ Per Crapus";
		String testFDNType3 = "Some Information";
		String testFDNValue1 = "o h n o";
		String testFDNValue2 = "sre43#$*%&";
		String testFDNValue3 = "OtherStuff";

		LOG.debug(".setUp(): Creating testFDN1 using default Constructor!");
		testFDN1 = new FDN();
		LOG.debug(".setUp(): Adding 1st RDN using appendRDN()!");
		testFDN1.appendRDN(new RDN(testFDNType1, testFDNValue1));
		LOG.debug(".setUp(): Adding 2nd RDN using appendRDN()!");
		testFDN1.appendRDN(new RDN(testFDNType2, testFDNValue2));
		LOG.debug(".setUp(): Adding 3rd RDN using appendRDN()!");
		testFDN1.appendRDN(new RDN(testFDNType3, testFDNValue3));
		LOG.debug(".setUp(): testFDN1 --> {}", testFDN1);
		LOG.debug(".setUp(): testFDN1 FDNToken --> {}", testFDN1.getToken());
	}

	@Test
	void basicEmptyFDNTest() {
		LOG.debug(".basicEmptyFDNTest(): Entry...");
		boolean testPassed = true;
		FDN emptyFDN = new FDN();
		LOG.trace(".basicEmptyFDNTest(): check .isEmpty() works.");
		if (!emptyFDN.isEmpty()) {
			fail("basicEmptyFDNTest: FDN should be emtpy, but .isEmpty() returns -true-");
		}
		LOG.debug(".basicEmptyFDNTest(): check .toString().isEmpty() returns -true-");
		if (!emptyFDN.toString().isEmpty()) {
			fail("basicEmptyFDNTest: FDN should be emtpy, but .toString().isEmpty() returns false");
		}
		LOG.debug(".basicEmptyFDNTest(): check .getUnqualifiedRDN() returns null.");
		if (emptyFDN.getUnqualifiedRDN() != null) {
			fail("basicEmptyFDNTest: FDN should be emtpy, but .getUnqualifiedRDN() doesn't return null");
		}
	}
	
	@Test
	void basicConstructureSetTest() {
		LOG.debug(".basicConstructureSetTest(): Entry...");
		// Default Constructor Test 
		LOG.trace(".basicConstructureSetTest(): Default Constructor Test");
		FDN emptyFDN = new FDN();
		LOG.trace(".basicConstructureSetTest(): check .isEmpty() works.");
		if (!emptyFDN.isEmpty()) {
			fail("basicConstructureSetTest: FDN should be emtpy, but .isEmpty() returns -true-");
		}		
		RDN toBeAppendedRDN = new RDN("Level1", "TestValue1");
		emptyFDN.appendRDN(toBeAppendedRDN);
		if(emptyFDN.getRDNCount() != 1) {
			fail("basicConstructureSetTest: there should only be a single RDN, but there is --> " + emptyFDN.getRDNCount());
		}
		// Copy Constructor
		LOG.trace(".basicConstructureSetTest(): Copy Constructor Test");
		FDN originalFDN = new FDN();
		originalFDN.appendRDN(new RDN("TestType0","TestValue0"));
		originalFDN.appendRDN(new RDN("TestType1","TestValue1"));
		originalFDN.appendRDN(new RDN("TestType2","TestValue2"));
		originalFDN.appendRDN(new RDN("TestType3","TestValue3"));
		FDN copiedFDN = new FDN(originalFDN);
		if(copiedFDN.getRDNCount() != 4) {
			fail("basicConstructureSetTest: Failed Copy Constructor Test - wrong number of RDNs, should be 4, but there is --> " + copiedFDN.getRDNCount());
		}
		RDN unqualifiedRDN = copiedFDN.getUnqualifiedRDN();
		if(!unqualifiedRDN.getQualifier().contentEquals("TestType3") || !unqualifiedRDN.getValue().contentEquals("TestValue3")) {
			fail("basicConstructureSetTest: Failed Copy Constructor Test - wrong content in RDN");
		}
		// FDNToken Constructor
		LOG.trace("basicConstructureSetTest(): FDNToken Constructor Test");
		String fdnTokenString0 = "{FDNToken:{\"0\":\"{\\\"Qualifier\\\":\\\"TestType0\\\",\\\"Value\\\":\\\"TestValue0\\\"}\"}}";
		String fdnTokenString1 = "{FDNToken:{\"0\":\"{\\\"Qualifier\\\":\\\"TestType0\\\",\\\"Value\\\":\\\"TestValue0\\\"}\","
				+ "\"1\":\"{\\\"Qualifier\\\":\\\"TestType1\\\",\\\"Value\\\":\\\"TestValue1\\\"}\","
				+ "\"2\":\"{\\\"Qualifier\\\":\\\"TestType2\\\",\\\"Value\\\":\\\"TestValue2\\\"}\"}}";
		FDNToken fdnToken0 = new FDNToken(fdnTokenString0);
		FDNToken fdnToken1 = new FDNToken(fdnTokenString1);
		LOG.trace(".basicConstructureSetTest(): fdnToken1 --> {}", fdnToken1);
		FDN testFDN = new FDN(fdnToken1);
		if (testFDN.getRDNCount() != 3) {
			fail("basicConstructureSetTest: Wrong number of RDNs!");
		}
		RDN testRDN = testFDN.getUnqualifiedRDN();
		LOG.trace(".basicConstructureSetTest(): testRDN --> {}", testRDN);
		if (!((testRDN.getQualifier().contentEquals("TestType2"))
				&& (testRDN.getValue().contentEquals("TestValue2")))) {
			fail("basicConstructureSetTest: Wrong RDN was returned!");
		}
	}

	/**
	 * Test method for
	 * {@link FDN#appendRDN(RDN)}.
	 */
	@Test
	void testAppendRDN() {
		LOG.debug(".testAppendRDN(): Entry...");
		FDN referenceFDN = new FDN(new FDNToken("{FDNToken:{\"0\":\"{\\\"Qualifier\\\":\\\"Level0\\\",\\\"Value\\\":\\\"TestValue0\\\"}\"}}"));
		RDN testRDN = referenceFDN.getUnqualifiedRDN();
		if (!(testRDN.getQualifier().contentEquals("Level0"))
				|| !(testRDN.getValue().contentEquals("TestValue0"))) {
			fail("Simple re-test of the getNameType()/getNameValue() failed");
		}
		RDN toBeAppendedRDN = new RDN("Level1", "TestValue1");
		referenceFDN.appendRDN(toBeAppendedRDN);
		RDN testRDN2 = referenceFDN.getUnqualifiedRDN();
		if (!(testRDN2.getQualifier().contentEquals("Level1"))
				|| !(testRDN2.getValue().contentEquals("TestValue1"))) {
			fail("Comparison failed of the expected appended RDN");
		}		
	}


	/**
	 * Test method for
	 * {@link FDN#getParentFDN()}.
	 */
	@Test
	void testGetParentFDN() {
		LOG.debug(".testGetParentFDN(): Entry...");
		FDN referenceFDN = new FDN(new FDNToken(
				"{FDNToken:{\"0\":\"{\\\"Qualifier\\\":\\\"Level0\\\",\\\"Value\\\":\\\"TestValue0\\\"}\"}}"));
		RDN testRDN = referenceFDN.getUnqualifiedRDN();
		if (!(testRDN.getQualifier().contentEquals("Level0"))
				|| !(testRDN.getValue().contentEquals("TestValue0"))) {
			fail("Simple re-test of the getNameType()/getNameValue() failed");
		}
		RDN toBeAppendedRDN = new RDN("Level1", "TestValue1");
		referenceFDN.appendRDN(toBeAppendedRDN);
		RDN testRDN2 = referenceFDN.getUnqualifiedRDN();
		if (!(testRDN2.getQualifier().contentEquals("Level1"))
				|| !(testRDN2.getValue().contentEquals("TestValue1"))) {
			fail("Comparison failed of the expected appended RDN");
		}		
		FDN testFDN2 = referenceFDN.getParentFDN();
		if (!(testRDN.getQualifier().contentEquals("Level0"))
				|| !(testRDN.getValue().contentEquals("TestValue0"))) {
			fail("Failed to handle removing FDN (i.e. getting Parent FDN)");
		}
	}


	/**
	 * Test method for
	 * {@link FDN#equals(FDN)}.
	 */
	@Test
	void testEqualsFDN() {
		LOG.debug(".testEqualsFDN(): Entry...");
		FDN firstFDN = new FDN();
		firstFDN.appendRDN(new RDN("TestType0","TestValue0"));
		firstFDN.appendRDN(new RDN("TestType1","TestValue1"));
		firstFDN.appendRDN(new RDN("TestType2","TestValue2"));
		firstFDN.appendRDN(new RDN("TestType3","TestValue3"));
		FDN secondFDN = new FDN();
		secondFDN.appendRDN(new RDN("TestType0","TestValue0"));
		secondFDN.appendRDN(new RDN("TestType1","TestValue1"));
		secondFDN.appendRDN(new RDN("TestType2","TestValue2"));
		secondFDN.appendRDN(new RDN("TestType3","TestValue3"));
		if(!firstFDN.equals(secondFDN)){
			fail("FDN::equals failed");
		}
	}

}
