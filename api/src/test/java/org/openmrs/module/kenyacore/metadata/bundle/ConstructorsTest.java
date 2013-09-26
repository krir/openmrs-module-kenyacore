/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyacore.metadata.bundle;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.GlobalProperty;
import org.openmrs.LocationAttributeType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.VisitAttributeType;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.SerializingCustomDatatype;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.patient.IdentifierValidator;
import org.openmrs.patient.UnallowedIdentifierException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link Constructors}
 */
public class ConstructorsTest extends BaseModuleContextSensitiveTest {

	@Test
	public void integration() {
		new Constructors();
	}

	/**
	 * @see Constructors#encounterType(String, String, String)
	 */
	@Test
	public void encounterType() {
		EncounterType obj = Constructors.encounterType("name", "desc", "obj-uuid");

		Assert.assertThat(obj.getName(), is("name"));
		Assert.assertThat(obj.getDescription(), is("desc"));
		Assert.assertThat(obj.getUuid(), is("obj-uuid"));
	}

	/**
	 * @see Constructors#form(String, String, String, String, String)
	 */
	@Test
	public void form() {
		EncounterType encType = Constructors.encounterType("name", "desc", "enctype-uuid");
		Context.getEncounterService().saveEncounterType(encType);

		Form obj = Constructors.form("name", "desc", "enctype-uuid", "1", "obj-uuid");

		Assert.assertThat(obj.getName(), is("name"));
		Assert.assertThat(obj.getDescription(), is("desc"));
		Assert.assertThat(obj.getEncounterType(), is(encType));
		Assert.assertThat(obj.getVersion(), is("1"));
		Assert.assertThat(obj.getUuid(), is("obj-uuid"));
	}

	/**
	 * @see Constructors#globalProperty(String, String, String, String)
	 */
	@Test
	public void globalProperty_withoutCustomDatatype() {
		// Check with non-null value
		GlobalProperty obj = Constructors.globalProperty("property", "desc", "value", "obj-uuid");

		Assert.assertThat(obj.getProperty(), is("property"));
		Assert.assertThat(obj.getDescription(), is("desc"));
		Assert.assertThat(obj.getDatatypeClassname(), is(FreeTextDatatype.class.getName()));
		Assert.assertThat(obj.getDatatypeConfig(), is(nullValue()));
		Assert.assertThat(obj.getPropertyValue(), is(""));
		Assert.assertThat(obj.getValue(), is((Object) "value"));
		Assert.assertThat(obj.getUuid(), is("obj-uuid"));

		// Check with empty string value
		obj = Constructors.globalProperty("property", "desc", "", "obj-uuid");

		Assert.assertThat(obj.getProperty(), is("property"));
		Assert.assertThat(obj.getDescription(), is("desc"));
		Assert.assertThat(obj.getDatatypeClassname(), is(FreeTextDatatype.class.getName()));
		Assert.assertThat(obj.getDatatypeConfig(), is(nullValue()));
		Assert.assertThat(obj.getPropertyValue(), is(""));
		Assert.assertThat(obj.getValue(), is((Object) ""));
		Assert.assertThat(obj.getUuid(), is("obj-uuid"));

		// Check with null value
		obj = Constructors.globalProperty("property", "desc", null, "obj-uuid");

		Assert.assertThat(obj.getProperty(), is("property"));
		Assert.assertThat(obj.getDescription(), is("desc"));
		Assert.assertThat(obj.getDatatypeClassname(), is(FreeTextDatatype.class.getName()));
		Assert.assertThat(obj.getDatatypeConfig(), is(nullValue()));
		Assert.assertThat(obj.getPropertyValue(), is(""));
		Assert.assertThat(obj.getValue(), is((Object) "")); // You'd think this would be null...
		Assert.assertThat(obj.getUuid(), is("obj-uuid"));
	}

	/**
	 * @see Constructors#globalProperty(String, String, Class, String, Object, String)
	 */
	@Test
	public void globalProperty_withCustomDatatype() {
		// Check with non-null value
		GlobalProperty obj = Constructors.globalProperty("property", "desc", TestingDatatype.class, "config", 123, "obj-uuid");

		Assert.assertThat(obj.getProperty(), is("property"));
		Assert.assertThat(obj.getDescription(), is("desc"));
		Assert.assertThat(obj.getDatatypeClassname(), is(TestingDatatype.class.getName()));
		Assert.assertThat(obj.getDatatypeConfig(), is("config"));
		Assert.assertThat(obj.getPropertyValue(), is(""));
		Assert.assertThat(obj.getValue(), is((Object) new Integer(123)));
		Assert.assertThat(obj.getUuid(), is("obj-uuid"));

		// Check with null value
		obj = Constructors.globalProperty("property", "desc", TestingDatatype.class, "config", null, "obj-uuid");

		Assert.assertThat(obj.getProperty(), is("property"));
		Assert.assertThat(obj.getDescription(), is("desc"));
		Assert.assertThat(obj.getDatatypeClassname(), is(TestingDatatype.class.getName()));
		Assert.assertThat(obj.getDatatypeConfig(), is("config"));
		Assert.assertThat(obj.getPropertyValue(), is(""));
		Assert.assertThat(obj.getValue(), is(nullValue()));
		Assert.assertThat(obj.getUuid(), is("obj-uuid"));
	}

	/**
	 * @see Constructors#locationAttributeType(String, String, Class, String, int, int, String)
	 */
	@Test
	public void locationAttributeType() {
		LocationAttributeType obj = Constructors.locationAttributeType("name", "desc", TestingDatatype.class, "config", 0, 1, "obj-uuid");

		Assert.assertThat(obj.getName(), is("name"));
		Assert.assertThat(obj.getDescription(), is("desc"));
		Assert.assertThat(obj.getDatatypeClassname(), is(TestingDatatype.class.getName()));
		Assert.assertThat(obj.getDatatypeConfig(), is("config"));
		Assert.assertThat(obj.getMinOccurs(), is(0));
		Assert.assertThat(obj.getMaxOccurs(), is(1));
		Assert.assertThat(obj.getUuid(), is("obj-uuid"));
	}

	/**
	 * @see Constructors#patientIdentifierType(String, String, String, String, Class, org.openmrs.PatientIdentifierType.LocationBehavior, boolean, String)
	 */
	@Test
	public void patientIdentifierType() {
		PatientIdentifierType obj = Constructors.patientIdentifierType("name", "desc", "\\d+", "format-desc", TestingIdentifierValidator.class,
				PatientIdentifierType.LocationBehavior.NOT_USED, false, "obj-uuid");

		Assert.assertThat(obj.getName(), is("name"));
		Assert.assertThat(obj.getDescription(), is("desc"));
		Assert.assertThat(obj.getFormat(), is("\\d+"));
		Assert.assertThat(obj.getFormatDescription(), is("format-desc"));
		Assert.assertThat(obj.getValidator(), is(TestingIdentifierValidator.class.getName()));
		Assert.assertThat(obj.getLocationBehavior(), is(PatientIdentifierType.LocationBehavior.NOT_USED));
		Assert.assertThat(obj.getRequired(), is(false));
		Assert.assertThat(obj.getUuid(), is("obj-uuid"));
	}

	/**
	 * @see Constructors#personAttributeType(String, String, Class, Integer, boolean, double, String)
	 */
	@Test
	public void personAttributeType() {
		PersonAttributeType obj = Constructors.personAttributeType("name", "desc", String.class, null, false, 1.0, "obj-uuid");

		Assert.assertThat(obj.getName(), is("name"));
		Assert.assertThat(obj.getDescription(), is("desc"));
		Assert.assertThat(obj.getFormat(), is(String.class.getName()));
		Assert.assertThat(obj.isSearchable(), is(false));
		Assert.assertThat(obj.getSortWeight(), is(1.0));
		Assert.assertThat(obj.getUuid(), is("obj-uuid"));
	}

	/**
	 * @see Constructors#program(String, String, String, String)
	 */
	@Test
	public void program() {
		// Existing concepts in test data
		final String HIV_PROGRAM_UUID = "0a9afe04-088b-44ca-9291-0a8c3b5c96fa";

		Program obj = Constructors.program("name", "desc", HIV_PROGRAM_UUID, "obj-uuid");

		Assert.assertThat(obj.getName(), is("name"));
		Assert.assertThat(obj.getDescription(), is("desc"));
		Assert.assertThat(obj.getConcept(), is(Context.getConceptService().getConceptByUuid(HIV_PROGRAM_UUID)));
		Assert.assertThat(obj.getUuid(), is("obj-uuid"));
	}

	/**
	 * @see Constructors#visitType(String, String, String)
	 */
	@Test
	public void visitType() throws Exception {
		VisitType obj = Constructors.visitType("name", "desc", "obj-uuid");

		Assert.assertThat(obj.getName(), is("name"));
		Assert.assertThat(obj.getDescription(), is("desc"));
		Assert.assertThat(obj.getUuid(), is("obj-uuid"));
	}

	/**
	 * @see Constructors#visitAttributeType(String, String, Class, String, int, int, String)
	 */
	@Test
	public void visitAttributeType() {
		VisitAttributeType obj = Constructors.visitAttributeType("name", "desc", TestingDatatype.class, "config", 0, 1, "obj-uuid");

		Assert.assertThat(obj.getName(), is("name"));
		Assert.assertThat(obj.getDescription(), is("desc"));
		Assert.assertThat(obj.getDatatypeClassname(), is(TestingDatatype.class.getName()));
		Assert.assertThat(obj.getDatatypeConfig(), is("config"));
		Assert.assertThat(obj.getMinOccurs(), is(0));
		Assert.assertThat(obj.getMaxOccurs(), is(1));
		Assert.assertThat(obj.getUuid(), is("obj-uuid"));
	}

	/**
	 * Simple integer data type class for testing
	 */
	public static class TestingDatatype extends SerializingCustomDatatype<Integer> {

		@Override
		public String serialize(Integer typedValue) {
			return typedValue != null ? String.valueOf(typedValue) : "";
		}

		@Override
		public Integer deserialize(String serializedValue) {
			return StringUtils.isNotEmpty(serializedValue) ? Integer.valueOf(serializedValue) : null;
		}
	}

	/**
	 * Custom identifier validator for testing
	 */
	public static class TestingIdentifierValidator implements IdentifierValidator {

		@Override
		public String getName() {
			return "Test validator";
		}

		@Override
		public boolean isValid(String identifier) throws UnallowedIdentifierException {
			return true;
		}

		@Override
		public String getValidIdentifier(String undecoratedIdentifier) throws UnallowedIdentifierException {
			return null;
		}

		@Override
		public String getAllowedCharacters() {
			return null;
		}
	}
}