package eu.trentorise.smartcampus.service.trentinofamiglia.test;

import it.sayservice.platform.core.bus.common.AppConfig;
import it.sayservice.platform.servicebus.test.DataFlowTestHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.protobuf.Message;

import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetBabyLittleHomeDataFlow;
import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetDistrettiDataFlow;
import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetDossierDataFlow;
import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetEventiDataFlow;
import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetFamilyTrentinoDataFlow;
import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetOrganizzazioniDataFlow;
import eu.trentorise.smartcampus.service.trentinofamiglia.impl.GetProgrammiDataFlow;

public class TestDataFlow extends TestCase {
	
	public void testRun() throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("test-context.xml");
		AppConfig ac = (AppConfig)context.getBean("appConfig");		
		
		DataFlowTestHelper helper = new DataFlowTestHelper();
		Map<String, Object> parameters = new HashMap<String, Object>();
//		String ids[] = { "16342", "17441", "17442", "17443", "17444", "17445", "17446", "18163", "18167", "18171"}; 
//		for (String id: ids) {
//		parameters.put("idDistretto", id);
//		System.out.println(id);
		Map<String, Object> out1 = helper.executeDataFlow("smartcampus.service.trentinofamiglia", "GetEventi", new GetEventiDataFlow(), parameters);
		List<Message> data1 = (List<Message>)out1.get("data");
		for (Message msg: data1) {
//			System.err.println((((DatiDistretto)msg)).getOrganizzazioniList());
			System.err.println(msg);
			System.err.println("----------------------------------");
			System.err.println(data1.size());
		}
		
//		Map<String, Object> out2 = helper.executeDataFlow("smartcampus.service.trentinofamiglia", "GetOrganizzazioni", new GetOrganizzazioniDataFlow(), parameters);
//		List<Message> data2 = (List<Message>)out2.get("data");
//		for (Message msg: data2) {
//			System.err.println(((OrganizzazioneFamiglia)msg).getName());
//			System.err.println(((OrganizzazioneFamiglia)msg).getStatus());
//			System.err.println(((OrganizzazioneFamiglia)msg).getLink());
//			System.err.println("----------------------------------");
//		}
		
//		Map<String, Object> out2 = helper.executeDataFlow("smartcampus.service.trentinofamiglia", "GetDistretto", new GetDistrettoDataFlow(), parameters);
//		List<Message> data2 = (List<Message>)out2.get("data");
//		for (Message msg: data2) {
////			System.err.println(((DatiDistretto)msg));
//			System.err.println("--------------------------------------------------------------------");			
//			for (OrganizzazioneAderente org: ((DatiDistretto)msg).getOrganizzazioniList()) {
//			System.err.println(org);
//			System.err.println("----------------------------------");
//			}
//		}		
		
//		System.err.println(data1.size());
//		System.err.println(data2.size());
	}
}
