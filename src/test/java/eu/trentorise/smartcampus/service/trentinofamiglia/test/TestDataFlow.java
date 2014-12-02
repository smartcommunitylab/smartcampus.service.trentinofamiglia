package eu.trentorise.smartcampus.service.trentinofamiglia.test;

import it.sayservice.platform.core.bus.service.dataflow.ServiceDataFlow;
import it.sayservice.platform.core.common.exception.ServiceException;
import it.sayservice.platform.servicebus.test.DataFlowTestHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.google.protobuf.Message;

import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetAllattamentoDataFlow;
import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetBabyLittleHomeDataFlow;
import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetDistrettiDataFlow;
import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetDossierDataFlow;
import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetEventiDataFlow;
import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetEventiGardaDataFlow;
import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetFamilyTrentinoDataFlow;
import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetManifestazioniDataFlow;
import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetNewMediaDataFlow;
import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetOrganizzazioniDataFlow;
import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetPersoneAuditDataFlow;
import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetProgrammiDataFlow;
import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetStruttureRicettiveDataFlow;

public class TestDataFlow extends TestCase {
	
	public void testEventi() throws Exception {
		doTest("smartcampus.service.trentinofamiglia", "GetEventi", new GetEventiDataFlow(), null);
	}
	public void testDistretti() throws Exception {
		doTest("smartcampus.service.trentinofamiglia", "GetDistretti", new GetDistrettiDataFlow(), Collections.<String,Object>singletonMap("idDistretto", "16342"));
	}
	public void testAllattamento() throws Exception {
		doTest("smartcampus.service.trentinofamiglia", "GetAllattamento", new GetAllattamentoDataFlow(), null);
	}
	public void testBabyLittleHome() throws Exception {
		doTest("smartcampus.service.trentinofamiglia", "GetBabyLittleHome", new GetBabyLittleHomeDataFlow(), null);
	}
	public void testDossier() throws Exception {
		doTest("smartcampus.service.trentinofamiglia", "GetDossier", new GetDossierDataFlow(), null);
	}
	public void testEventiInGarda() throws Exception {
		doTest("smartcampus.service.trentinofamiglia", "GetEventiGarda", new GetEventiGardaDataFlow(), null);
	}
	public void testFamilyTrentino() throws Exception {
		doTest("smartcampus.service.trentinofamiglia", "GetFamilyTrentino", new GetFamilyTrentinoDataFlow(), null);
	}
	public void testManifestazioni() throws Exception {
		doTest("smartcampus.service.trentinofamiglia", "GetManifestazioni", new GetManifestazioniDataFlow(), null);
	}
	public void testNewMedia() throws Exception {
		doTest("smartcampus.service.trentinofamiglia", "GetNewMedia", new GetNewMediaDataFlow(), null);
	}
	public void testOrganizzazioni() throws Exception {
		doTest("smartcampus.service.trentinofamiglia", "GetOrganizzazioni", new GetOrganizzazioniDataFlow(), null);
	}
	public void testPersoneAudit() throws Exception {
		doTest("smartcampus.service.trentinofamiglia", "GetPersoneAudit", new GetPersoneAuditDataFlow(), null);
	}
	public void testProgrammi() throws Exception {
		doTest("smartcampus.service.trentinofamiglia", "GetProgrammi", new GetProgrammiDataFlow(), Collections.<String,Object>singletonMap("idDistretto", "16342"));
	}
	public void testStruttureRicettive() throws Exception {
		doTest("smartcampus.service.trentinofamiglia", "GetStruttureRicettive", new GetStruttureRicettiveDataFlow(), null);
	}

	private void doTest(String service, String method, ServiceDataFlow df, Map<String,Object> params) throws ServiceException {
		DataFlowTestHelper helper = new DataFlowTestHelper("test");
		Map<String, Object> parameters = params == null ? new HashMap<String, Object>() : params;
		Map<String, Object> out1 = helper.executeDataFlow(service, method, df, parameters);
		List<Message> data1 = (List<Message>)out1.get("data");
		for (Message msg: data1) {
			System.err.println(msg);
			System.err.println("----------------------------------");
		}
		Assert.assertTrue(data1 != null && data1.size() > 0);
		System.err.println(data1.size());
	}
}
