package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.skillbox.socialnet.entity.locationrelated.Currency;
import ru.skillbox.socialnet.repository.CurrencyRepository;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoursesService {

    private final CurrencyRepository currencyRepository;

    public void downloadCourses() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse("http://www.cbr.ru/scripts/XML_daily.asp");
        doc.getDocumentElement().normalize();

        //Считать список валют
        NodeList nodeList = doc.getElementsByTagName("Valute");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                String valCode = elem.getElementsByTagName("CharCode").item(0).getTextContent();
                if (valCode.equalsIgnoreCase("usd") || valCode.equalsIgnoreCase("eur")) {
                    saveCourse(valCode, elem.getElementsByTagName("Value").item(0).getTextContent());
                }
            }
        }
    }

    private void saveCourse(String valCode, String value) {
        Optional<Currency> optCurrency = currencyRepository.findByName(valCode);
        if (optCurrency.isPresent()) {
            return;
        }
        Currency currency = new Currency();
        currency.setName(valCode);
        currency.setPrice(value);
        DateFormat df = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
        currency.setUpdateTime(df.format(Calendar.getInstance().getTime()));
        currencyRepository.save(currency);
    }

}
