package eu.trentorise.smartcampus.service.trentinofamiglia.scripts;

import it.sayservice.platform.core.message.Core.Address;
import it.sayservice.platform.core.message.Core.Coordinate;
import it.sayservice.platform.core.message.Core.POI;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import au.com.bytecode.opencsv.CSVReader;

import com.google.protobuf.Message;

import eu.trentorise.smartcampus.service.trentinofamiglia.data.message.Trentinofamiglia.DatiAllattamento;
import eu.trentorise.smartcampus.service.trentinofamiglia.data.message.Trentinofamiglia.DatiAttivita;
import eu.trentorise.smartcampus.service.trentinofamiglia.data.message.Trentinofamiglia.DatiAzione;
import eu.trentorise.smartcampus.service.trentinofamiglia.data.message.Trentinofamiglia.DatiBabyLittleHome;
import eu.trentorise.smartcampus.service.trentinofamiglia.data.message.Trentinofamiglia.DatiManifestazione;
import eu.trentorise.smartcampus.service.trentinofamiglia.data.message.Trentinofamiglia.DatiNewMedia;
import eu.trentorise.smartcampus.service.trentinofamiglia.data.message.Trentinofamiglia.DatiOrganizzazioniDistretto;
import eu.trentorise.smartcampus.service.trentinofamiglia.data.message.Trentinofamiglia.DatiPersonaAudit;
import eu.trentorise.smartcampus.service.trentinofamiglia.data.message.Trentinofamiglia.DatiProgramma;
import eu.trentorise.smartcampus.service.trentinofamiglia.data.message.Trentinofamiglia.DatiProgrammiDistretto;
import eu.trentorise.smartcampus.service.trentinofamiglia.data.message.Trentinofamiglia.DossierFamiglia;
import eu.trentorise.smartcampus.service.trentinofamiglia.data.message.Trentinofamiglia.EventoFamiglia;
import eu.trentorise.smartcampus.service.trentinofamiglia.data.message.Trentinofamiglia.EventoGarda;
import eu.trentorise.smartcampus.service.trentinofamiglia.data.message.Trentinofamiglia.OrganizzazioneAderente;
import eu.trentorise.smartcampus.service.trentinofamiglia.data.message.Trentinofamiglia.OrganizzazioneFamiglia;
import eu.trentorise.smartcampus.service.trentinofamiglia.data.message.Trentinofamiglia.OrganizzazioneFamilyTrentino;
import eu.trentorise.smartcampus.service.trentinofamiglia.data.message.Trentinofamiglia.StrutturaRicettiva;
import eu.trentorise.smartcampus.service.trentinofamiglia.distretti.jaxb.Distretto;
import eu.trentorise.smartcampus.service.trentinofamiglia.distretti.jaxb.Distretto.OrganizzazioniAderenti.Aderente;
import eu.trentorise.smartcampus.service.trentinofamiglia.distretti.jaxb.Distretto.ProgrammiDiLavoro.ProgrammaDiLavoro;
import eu.trentorise.smartcampus.service.trentinofamiglia.distretti.jaxb.Distretto.ProgrammiDiLavoro.ProgrammaDiLavoro.AttivitaDistretto.Attivita;
import eu.trentorise.smartcampus.service.trentinofamiglia.distretti.jaxb.Distretto.ProgrammiDiLavoro.ProgrammaDiLavoro.AttivitaDistretto.Attivita.Azioni.Azione;
import eu.trentorise.smartcampus.service.trentinofamiglia.distretti2014.jaxb.OrganizzazioniEGFList;
import eu.trentorise.smartcampus.service.trentinofamiglia.distretti2014.jaxb.OrganizzazioniEGFList.Organizzazione;
import eu.trentorise.smartcampus.service.trentinofamiglia.distretti2014.jaxb.OrganizzazioniEGFList.Organizzazione.AttivitaEstateGiovaniEFamigliaList.AttivitaEstateGiovaniEFamiglia;
import eu.trentorise.smartcampus.service.trentinofamiglia.dossier.jaxb.Dataroot.DossierPoliticheFamIntEcoDataSet;
import eu.trentorise.smartcampus.service.trentinofamiglia.garda.jaxb.Events;
import eu.trentorise.smartcampus.service.trentinofamiglia.garda.jaxb.Events.Item;
import eu.trentorise.smartcampus.service.trentinofamiglia.jaxb.Dataroot.TABOrg.TABAttività;
import eu.trentorise.smartcampus.service.trentinofamiglia.rss.jaxb.Guid;
import eu.trentorise.smartcampus.service.trentinofamiglia.rss.jaxb.Rss;
import eu.trentorise.smartcampus.service.trentinofamiglia.rss.jaxb.RssItem;

public class TrentinoFamigliaScript {

	public List<Message> parseEstate(String s) throws Exception {
		List<Message> result = new ArrayList<Message>();
		
		JAXBContext jc = JAXBContext.newInstance("eu.trentorise.smartcampus.service.trentinofamiglia.distretti2014.jaxb");
		Unmarshaller u = jc.createUnmarshaller();

		OrganizzazioniEGFList orgList = (OrganizzazioniEGFList) u.unmarshal(new StringReader(s));			
		
		for (Organizzazione org: orgList.getOrganizzazione()) {
			for (AttivitaEstateGiovaniEFamiglia att: org.getAttivitaEstateGiovaniEFamigliaList().getAttivitaEstateGiovaniEFamiglia()) {
				EventoFamiglia.Builder builder = EventoFamiglia.newBuilder();
				
				builder.setTitle(removeCR(att.getNome()));
				builder.setDescription(removeCR(att.getDescrizioneEstesa()));
				builder.setFrom(removeCR(att.getDataDiInizio()));
				builder.setTo(removeCR(att.getDataDiFine()));
				builder.setDays(removeCR(att.getOrariPeriodicitaTurniDiSvolgimento()));
				builder.setOrganization(removeCR(org.getNome()));
				builder.setPlace(removeCR(att.getSede()));
				builder.setId(att.getId());
				builder.setCertified(false);
				
				
				result.add(builder.build());
			}
		}
		return result;
}

	protected String buildDescription(TABAttività ta) {
		StringBuilder sb = new StringBuilder();
		sb.append("<p>" + ta.getDescrizioneX0020SinteticaX0020DellX0027Attività() + "</p>");
		sb.append("<p>Orari/Periodicità/Turni: " + checkLen(ta.getOrariX0020X002FX0020PeriodicitàX002FX0020TurniX0020DiX0020Svolgimento()) + "</p>");
		sb.append("<p>Sede: " + checkLen(ta.getSedeX002FIX0020PressoX0020CuiX0020SiX0020SvolgeràX0020LX0027Attività()) + "</p>");
		sb.append("<p>Attività diurna/residenziale: " + checkLen(ta.getTipoX0020DiX0020AttivitàX0020X0028DiurnaX002FResidenzialeX0029()) + "</p>");
		sb.append("<p>Costo: " + checkLen(ta.getTipoX0020DiX0020Costo()) + "</p>");
		sb.append("<p>Specifiche sul costo: " + checkLen(ta.getSpecificheX0020SulX0020Costo()) + "</p>");
		sb.append("<p>Fascia età: " + checkLen(ta.getFasciaX002FEX0020DiX0020EtàX0020Destinatari()) + "</p>");
		sb.append("<p>Vincoli residenza: " + checkLen(ta.getVincoliX0020DiX0020ResidenzaX0020DeiX0020Destinatari()) + "</p>");
		sb.append("<p>Referenti: " + checkLen(ta.getReferentiX0020CheX0020PossonoX0020FornireX0020InformazioniX0020SuX0020QuestaX0020Attività()) + "</p>");
		sb.append("<p>Modalità e tempi di iscrizione: " + checkLen(ta.getModalitàX0020EX0020TempiX0020DiX0020Iscrizione()) + "</p>");
		sb.append("<p>Scheda: " + checkLen(ta.getScheda()) + "</p>");

		return sb.toString();
	}

	protected String checkLen(String s) {
		if (s.length() <= 1) {
			return "-";
		} else {
			return s;
		}
	}

	public List<Message> parseOrganizzazioni(String s) throws Exception {
		List<Message> result = new ArrayList<Message>();
		try {
			BufferedReader br = new BufferedReader(new StringReader(s));

			int occ = StringUtils.countMatches(s, ",");
			int ocsc = StringUtils.countMatches(s, ";");

			char sep = (occ > ocsc) ? ',' : ';';

			boolean first = true;

			CSVReader csvReader = new CSVReader(br, sep, '"');
			List<String[]> lines = csvReader.readAll();

			for (String words[] : lines) {
				if (first) {
					first = false;
					continue;
				}

				try {
					OrganizzazioneFamiglia.Builder builder = OrganizzazioneFamiglia.newBuilder();
					builder.setOrder("" + Integer.parseInt(words[0].replace("\"", "")));
					builder.setName(words[1].replace("\"", ""));
					builder.setStatus(words[2].replace("\"", ""));
					builder.setLink(words[8].replace("\"", ""));
					POI poi = buildOrganizzazioniPOI(words);
					builder.setPoi(poi);
	
					result.add(builder.build());
				} catch (Exception e) {
					e.printStackTrace();
				}							
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	public List<Message> parseStruttureRicettive() throws Exception {
		List<Message> result = new ArrayList<Message>();
		try {
			InputStream rs = Thread.currentThread().getContextClassLoader().getResourceAsStream("data/vacanze_mare_G.csv");
			InputStreamReader isr = new InputStreamReader(rs, Charset.forName("UTF-8"));

			boolean first = true;

			CSVReader csvReader = new CSVReader(isr, ',', '"');
			List<String[]> lines = csvReader.readAll();

			for (String words[] : lines) {
				if (first) {
					first = false;
					continue;
				}

				try {
					StrutturaRicettiva.Builder builder = StrutturaRicettiva.newBuilder();
					builder.setId(words[0]);
					builder.setName(words[1]);
					builder.setStars(words[2]);
					builder.setLevelFamily(words[3]);
					builder.setRegion(words[4]);
					builder.setTown(words[5]);
					builder.setBookingHow(words[6]);
					builder.setBookingWhere(words[7]);
					builder.setBookingAddress(words[8]);
					builder.setBookingZipCode(words[9]);
					builder.setBookingTown(words[10]);
					builder.setBookingPhone(words[11]);
					builder.setBookingEmail(words[12]);
					builder.setBookingLink(words[13]);
					builder.setGuide(words[14]);
					builder.setLat(Double.parseDouble(words[15]));
					builder.setLon(Double.parseDouble(words[16]));		
				result.add(builder.build());
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	public List<Message> parsePersoneAudit() throws Exception {
		List<Message> result = new ArrayList<Message>();
		String files[] = new String[] { "data/consulenti_audit.csv", "data/valutatori_audit.csv" };
		String types[] = new String[] { "Consulente", "Valutatore" };
		try {
			for (int i = 0; i < files.length; i++) {
				InputStream rs = Thread.currentThread().getContextClassLoader().getResourceAsStream(files[i]);
				InputStreamReader isr = new InputStreamReader(rs, Charset.forName("UTF-8"));

				boolean first = true;

				CSVReader csvReader = new CSVReader(isr, ',', '"');
				List<String[]> lines = csvReader.readAll();

				for (String words[] : lines) {
					if (first) {
						first = false;
						continue;
					}

					DatiPersonaAudit.Builder builder = DatiPersonaAudit.newBuilder();
					builder.setId(words[0]);
					builder.setName(words[1]);
					builder.setDate(words[2]);
					builder.setEmail(words[3]);
					builder.setType(types[i]);

					result.add(builder.build());
				}
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	public List<Message> parseNewMedia() throws Exception {
		List<Message> result = new ArrayList<Message>();
		try {
			InputStream rs = Thread.currentThread().getContextClassLoader().getResourceAsStream("data/new_media_education_G.csv");
			InputStreamReader isr = new InputStreamReader(rs, Charset.forName("UTF-8"));

			boolean first = true;

			CSVReader csvReader = new CSVReader(isr, ',', '"');
			List<String[]> lines = csvReader.readAll();

			for (String words[] : lines) {
				if (first) {
					first = false;
					continue;
				}

				try {
					DatiNewMedia.Builder builder = DatiNewMedia.newBuilder();
					builder.setName(words[0]);
					builder.setContact(words[1]);
					builder.setRole(words[2]);
					builder.setAddress(words[3]);
					builder.setPhone(words[4]);
					builder.setLink(words[5]);
					builder.setEmail(words[6]);
					builder.setLat(Double.parseDouble(words[7]));
					builder.setLon(Double.parseDouble(words[8]));
					result.add(builder.build());
				} catch (Exception e) {
//					e.printStackTrace();
				}
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}	
	
	public List<Message> parseAllattamento() throws Exception {
		List<Message> result = new ArrayList<Message>();
		try {
			InputStream rs = Thread.currentThread().getContextClassLoader().getResourceAsStream("data/allattamento_G.csv");
			InputStreamReader isr = new InputStreamReader(rs, Charset.forName("UTF-8"));

			boolean first = true;

			CSVReader csvReader = new CSVReader(isr, ',', '"');
			List<String[]> lines = csvReader.readAll();

			for (String words[] : lines) {
				if (first) {
					first = false;
					continue;
				}

				try {
					DatiAllattamento.Builder builder = DatiAllattamento.newBuilder();
					builder.setId(words[0]);
					builder.setName(words[1]);
					builder.setAddress(words[2]);
					builder.setTown(words[3]);
					builder.setArea(words[4]);
					builder.setLat(Double.parseDouble(words[5]));
					builder.setLon(Double.parseDouble(words[6]));
					result.add(builder.build());
				} catch (Exception e) {
					e.printStackTrace();
				}


			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}		
	
	public List<Message> parseBabyLittleHome() throws Exception {
		List<Message> result = new ArrayList<Message>();
		try {
			InputStream rs = Thread.currentThread().getContextClassLoader().getResourceAsStream("data/baby_little_home.csv");
			InputStreamReader isr = new InputStreamReader(rs, Charset.forName("UTF-8"));

			boolean first = true;

			CSVReader csvReader = new CSVReader(isr, ',', '"');
			List<String[]> lines = csvReader.readAll();

			for (String words[] : lines) {
				if (first) {
					first = false;
					continue;
				}

				try {
					DatiBabyLittleHome.Builder builder = DatiBabyLittleHome.newBuilder();
					builder.setName(words[0]);
					builder.setPlace(words[1]);
					builder.setAddress(words[2]);
					try {
					builder.setLat(Double.parseDouble(transformLatLong(words[3])));
					builder.setLon(Double.parseDouble(transformLatLong(words[4])));
					} catch (NumberFormatException e) {
						continue;
					}
					builder.setDescription(words[5]);
					result.add(builder.build());
				} catch (Exception e) {
					e.printStackTrace();
				}


			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}			
	
	
	public List<Message> parseFamilyTrentino() throws Exception {
		List<Message> result = new ArrayList<Message>();
		String files[] = new String[] { "data/comuni_family.csv", "data/musei_family.csv", "data/alberghi.csv", "data/pubblici_esercizi.csv", "data/servizi_bambini.csv", "data/associazioni_sportive.csv" };
		String types[] = new String[] { "Comuni", "Musei", "Strutture alberghiere", "Pubblici esercizi", "Servizi per bambini e ragazzi", "Associazioni sportive" };
		try {
			for (int i = 0; i < files.length; i++) {
				InputStream rs = Thread.currentThread().getContextClassLoader().getResourceAsStream(files[i]);
				InputStreamReader isr = new InputStreamReader(rs, Charset.forName("UTF-8"));

				boolean first = true;

				CSVReader csvReader = new CSVReader(isr, ',', '"');
				List<String[]> lines = csvReader.readAll();

				int inc = 0;
				for (String words[] : lines) {
					if (first) {
						first = false;
						if ("Fax".equals(words[3])) {
							inc = 1;
						}
						continue;
					}

					try {
					OrganizzazioneFamilyTrentino.Builder builder = OrganizzazioneFamilyTrentino.newBuilder();
						builder.setName(words[0]);
						builder.setAddress(words[1]);
						builder.setPhone(words[2]);
						builder.setEmail(words[3 + inc]);
						builder.setWeb(words[4 + inc]);
						builder.setLat(Double.parseDouble(transformLatLong(words[5 + inc])));
						builder.setLon(Double.parseDouble(transformLatLong(words[6 + inc])));
						builder.setType(types[i]);
						if (inc != 0) {
							builder.setFax(words[3]);
						}
						result.add(builder.build());
					} catch (Exception e) {
					}
				}
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}	
	
	
	public String getOrganizzazioniURL(Document doc) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList) xPath.evaluate("//*[@id='dataset-resources']/ul/li/a", doc.getDocumentElement(), XPathConstants.NODESET);

		if (nodes.getLength() == 1) {
			String url = ((Element) nodes.item(0)).getAttribute("href");
			return url;
		}

		return "";
	}

	public String getOrganizzazioniCSVURL(Document doc) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();
//		NodeList nodes = (NodeList) xPath.evaluate("//*[@id='content']/div/div[1]/a[1]", doc.getDocumentElement(), XPathConstants.NODESET);
		NodeList nodes = (NodeList) xPath.evaluate("//*[@id='content']/div[3]/section/div[1]/div[1]/ul/li/a", doc.getDocumentElement(), XPathConstants.NODESET);

		if (nodes.getLength() == 1) {
			String url = ((Element) nodes.item(0)).getAttribute("href");
			return url;
		}

		return "";
	}

	public String getEventiURL(Document doc) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList) xPath.evaluate("//*[@id='dataset-resources']/ul/li/a", doc.getDocumentElement(), XPathConstants.NODESET);

		if (nodes.getLength() == 1) {
			String url = ((Element) nodes.item(0)).getAttribute("href");
			return url;
		}

		return "";
	}

	public String getEventiXMLURL(Document doc) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();
//		NodeList nodes = (NodeList) xPath.evaluate("//*[@id='content']/div/div[1]/a[1]", doc.getDocumentElement(), XPathConstants.NODESET);
		NodeList nodes = (NodeList) xPath.evaluate("//*[@id='content']/div[3]/section/div[1]/div[1]/ul/li/a", doc.getDocumentElement(), XPathConstants.NODESET);

		if (nodes.getLength() == 1) {
			String url = ((Element) nodes.item(0)).getAttribute("href");
			return url;
		}

		return "";
	}

	public String getDossierURL(Document doc) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList) xPath.evaluate("//*[@id='dataset-resources']/ul/li/a", doc.getDocumentElement(), XPathConstants.NODESET);

		if (nodes.getLength() == 1) {
			String url = ((Element) nodes.item(0)).getAttribute("href");
			return url;
		}

		return "";
	}

	public String getDossierXMLURL(Document doc) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();
//		NodeList nodes = (NodeList) xPath.evaluate("//*[@id='content']/div/div[1]/a[1]", doc.getDocumentElement(), XPathConstants.NODESET);
		NodeList nodes = (NodeList) xPath.evaluate("//*[@id='content']/div[3]/section/div[1]/div[1]/ul/li/a", doc.getDocumentElement(), XPathConstants.NODESET);

		if (nodes.getLength() == 1) {
			String url = ((Element) nodes.item(0)).getAttribute("href");
			return url;
		}

		return "";
	}

	protected POI buildOrganizzazioniPOI(String[] words) {
		POI.Builder poiBuilder = POI.newBuilder();

		int pos = Integer.parseInt(words[0].replace("\"", ""));

		Address.Builder addressBuilder = Address.newBuilder();
		addressBuilder.setStreet(words[3].replace("\"", ""));
		addressBuilder.setCity(removeSpaces(words[4]).replace("\"", ""));
		addressBuilder.setRegion(removeSpaces(words[5]).replace("\"", ""));
		addressBuilder.setCountry("ITA");
		addressBuilder.setState("Italy");
		addressBuilder.setLang("en");
		addressBuilder.setPostalCode("");

		poiBuilder.setAddress(addressBuilder.build());

		Coordinate.Builder coordBuilder = Coordinate.newBuilder();

		coordBuilder.setLatitude(Double.parseDouble(transformLatLong(words[6].replace("\"", ""))));
		coordBuilder.setLongitude(Double.parseDouble(transformLatLong(words[7].replace("\"", ""))));
		poiBuilder.setCoordinate(coordBuilder.build());

		poiBuilder.setDatasetId("smart");

		poiBuilder.setPoiId(pos + "@smartcampus.service.trentinofamiglia");

		return poiBuilder.build();
	}

	public Message parseDistretto(String s) throws Exception {
		try {
			DatiOrganizzazioniDistretto.Builder builder = DatiOrganizzazioniDistretto.newBuilder();

			JAXBContext jc = JAXBContext.newInstance("eu.trentorise.smartcampus.service.trentinofamiglia.distretti.jaxb");
			Unmarshaller u = jc.createUnmarshaller();

			Distretto distretto = (Distretto) u.unmarshal(new StringReader(s));
			builder.setDescription(removeCR(distretto.getDescrizione()));
			builder.setAlias(removeCR(distretto.getUrlAlias()));
			builder.setTitle(removeCR(distretto.getTitolo()));
			builder.setLat(distretto.getCoordinate().getLatitudine());
			builder.setLon(distretto.getCoordinate().getLongitudine());
			// TODO: change
			builder.setId(removeCR(distretto.getTitolo()));
			for (Aderente aderente : distretto.getOrganizzazioniAderenti().getAderente()) {
				try {
					if (aderente.getNome().length() < 5) {
						continue;
					}
					OrganizzazioneAderente.Builder org = OrganizzazioneAderente.newBuilder();
					org.setAlias(removeCR(aderente.getUrlAlias()));
					org.setAddress(removeCR(aderente.getIndirizzo()));
					org.setDescription(removeCR(aderente.getAbstract()));
					org.setEmail(removeCR(aderente.getEmail()));
					org.setFax(removeCR(aderente.getFax()));
					org.setLink(removeCR(aderente.getSitoWeb()));
					org.setLogo(removeCR(aderente.getLogo()));
					org.setName(removeCR(aderente.getNome()));
					org.setPhone(removeCR(aderente.getTelefono()));
					org.setLat(aderente.getCoordinate().getLatitudine());
					org.setLon(aderente.getCoordinate().getLongitudine());
					org.setId(removeCR(aderente.getNome()));
					builder.addOrganizzazioni(org.build());
				} catch (Exception e) {
					e.printStackTrace();
				}						
			}

			return builder.build();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public Message parseProgrammi(String s) throws Exception {
		try {
			DatiProgrammiDistretto.Builder builder = DatiProgrammiDistretto.newBuilder();
			List<DatiProgramma> result = new ArrayList<DatiProgramma>();

			JAXBContext jc = JAXBContext.newInstance("eu.trentorise.smartcampus.service.trentinofamiglia.distretti.jaxb");
			Unmarshaller u = jc.createUnmarshaller();

			Distretto distretto = (Distretto) u.unmarshal(new StringReader(s));
			builder.setDescription(removeCR(distretto.getDescrizione()));
			builder.setAlias(removeCR(distretto.getUrlAlias()));
			builder.setTitle(removeCR(distretto.getTitolo()));
			builder.setLat(distretto.getCoordinate().getLatitudine());
			builder.setLon(distretto.getCoordinate().getLongitudine());			
			builder.setId(removeCR(distretto.getTitolo()));
			for (ProgrammaDiLavoro programma : distretto.getProgrammiDiLavoro().getProgrammaDiLavoro()) {
				DatiProgramma.Builder prog = DatiProgramma.newBuilder();
				prog.setYear("Anno " + programma.getAnno().replaceAll("[^\\p{Digit}]", ""));
				prog.setLink(programma.getUrlAlias());
				for (Attivita attivita: programma.getAttivitaDistretto().getAttivita()) {
					DatiAttivita.Builder att = DatiAttivita.newBuilder();
					att.setContact(removeCR(attivita.getReferenti()));
					att.setTimes(removeCR(attivita.getTempi()));
					att.setTitle(removeCR(attivita.getTitolo()));
					att.setDescription(removeCR(attivita.getDescrizione()));
					att.setTags(attivita.getTags());
					for (Azione azione: attivita.getAzioni().getAzione()) {
						DatiAzione.Builder azio = DatiAzione.newBuilder();
						azio.setContact(removeCR(azione.getReferenti()));
						azio.setDescription(removeCR(azione.getDescrizione()));
						azio.setGoal(removeCR(azione.getObiettivo()));
						azio.setTimes(removeCR(azione.getTempi()));
						azio.setTitle(removeCR(azione.getTitolo()));
						azio.setTags(removeCR(azione.getTags()));
						att.addAzioni(azio.build());
					}
					prog.addAttivita(att.build());
				}
				result.add(prog.build());
			}

			builder.addAllProgrammi(result);
			
			return builder.build();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}



	public List<Message> parseDossier(String s) throws Exception {
		List<Message> result = new ArrayList<Message>();

		JAXBContext jc = JAXBContext.newInstance("eu.trentorise.smartcampus.service.trentinofamiglia.dossier.jaxb");
		Unmarshaller u = jc.createUnmarshaller();

		eu.trentorise.smartcampus.service.trentinofamiglia.dossier.jaxb.Dataroot dataroot = (eu.trentorise.smartcampus.service.trentinofamiglia.dossier.jaxb.Dataroot) u.unmarshal(new StringReader(s));

		for (DossierPoliticheFamIntEcoDataSet dossier : dataroot.getDossierPoliticheFamIntEcoDataSet()) {
			DossierFamiglia.Builder builder = DossierFamiglia.newBuilder();
			builder.setId(dossier.getIdIntervento());
			if (dossier.getTitolo() != null) {
				builder.setTitle(dossier.getTitolo());
			} else {
				continue;
			}
			if (dossier.getTipologiaX0020Intervento() != null) {
				builder.setType(dossier.getTipologiaX0020Intervento());
			}
			if (dossier.getDescrizione() != null) {
				builder.setDescription(dossier.getDescrizione());
			}
			if (dossier.getChiPuòRichiedere() != null) {
				builder.setWho(dossier.getChiPuòRichiedere());
			}
			if (dossier.getCosaFare() != null) {
				builder.setWhat(dossier.getCosaFare());
			}
			if (dossier.getPerSaperneDiPiù() != null) {
				builder.setMore(dossier.getPerSaperneDiPiù());
			}
			result.add(builder.build());
		}

		return result;
	}

	public List<Message> parseManifestazioni(String s) throws Exception {
		List<Message> result = new ArrayList<Message>();

		JAXBContext jc = JAXBContext.newInstance("eu.trentorise.smartcampus.service.trentinofamiglia.rss.jaxb");
		Unmarshaller u = jc.createUnmarshaller();

		Rss rss = (Rss) u.unmarshal(new StringReader(s));

		for (RssItem item : rss.getChannel().getItem()) {
			DatiManifestazione.Builder builder = DatiManifestazione.newBuilder();
			List<Object> dataList = item.getTitleOrDescriptionOrLink();
			for (Object data : dataList) {
				String name = ((JAXBElement) data).getName().toString();
				Object value = ((JAXBElement) data).getValue();
				if ("title".equals(name)) {
					builder.setTitle(value.toString());
				} else if ("description".equals(name)) {
					builder.setDescription(value.toString());
				} else if ("pubDate".equals(name)) {
					builder.setPubDate(value.toString());
				} else if ("link".equals(name)) {
					builder.setLink(value.toString());
				} else if ("guid".equals(name)) {
					builder.setId(((Guid) value).getValue());
				}
			}

			result.add(builder.build());
		}

		return result;
	}
	
	public List<Message> parseEventiGarda(String s) throws Exception {
		List<Message> result = new ArrayList<Message>();

		JAXBContext jc = JAXBContext.newInstance("eu.trentorise.smartcampus.service.trentinofamiglia.garda.jaxb");
		Unmarshaller u = jc.createUnmarshaller();
		
		Events events = (Events) u.unmarshal(new StringReader(s));
		
		for (Item item: events.getItem()) {
			try {
				EventoGarda.Builder builder = EventoGarda.newBuilder();
				
				builder.setTitle(item.getTitle());
				builder.setDescription(item.getDescription());
				builder.setLink(item.getLink());
				builder.setShortText(item.getTestobreve());
				builder.setPlace(item.getLuogodisvolgimento());
				if (item.getLocalita().getItem() != null) {
					builder.setLocation(item.getLocalita().getItem());
				}
				builder.setCategory(item.getCategoria());
				if (item.getCoordinate().getX() != null) {
					builder.setLat(item.getCoordinate().getX());
				}
				if (item.getCoordinate().getY() != null) {
					builder.setLon(item.getCoordinate().getY());
				}
				builder.setPrice(item.getPrezzo());
				builder.setFree(item.getGratuito());
				
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				builder.setFrom(df.parse(item.getDatainizio()).getTime());
				builder.setTo(df.parse(item.getDatafine()).getTime());
				
				result.add(builder.build());
		} catch (Exception e) {
			e.printStackTrace();
		}					
		}
		
		return result;
	}
	
	protected static String removeSpaces(String s) {
		return s.replaceAll("[\\s]+", " ").trim();
	}

	protected static String removeCR(String s) {
		return s.replaceAll("^[\\n]+", "");
	}

	protected static String transformLatLong(String ll) {
		String s = ll.replaceFirst("\\.", ",").replaceFirst("\\.", "").replaceAll(",", ".");
		return s;
	}

}
