package my.weatherApp.service;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.sun.org.apache.xerces.internal.dom.TextImpl;
import my.weatherApp.model.Currency;
import my.weatherApp.model.CurrencyRate;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.cbr.web.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class CbParser {

    public static List<CurrencyRate> getCurrencyRate(){
        List<CurrencyRate> currencyRatesList = new ArrayList<>();
        DailyInfo service = new DailyInfo();
        DailyInfoSoap port = service.getDailyInfoSoap();
        XMLGregorianCalendar onDate = null;
        try {
            onDate = getXMLGregorianCalendarNow();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }

        EnumValutesResponse.EnumValutesResult en = port.enumValutes(false);
        GetCursOnDateResponse.GetCursOnDateResult curs = port.getCursOnDate(onDate);

        onDate = port.getLatestDateTime();
        GetCursOnDateXMLResponse.GetCursOnDateXMLResult result = port.getCursOnDateXML(onDate);


        try{
            CurrencyRate usd = getValuteByValuteCh(Currency.USD.toString(), result);
            currencyRatesList.add(usd);
            CurrencyRate eur = getValuteByValuteCh(Currency.EUR.toString(), result);
            currencyRatesList.add(eur);
        } catch (Exception e){

        }
        return currencyRatesList;

    }

    public static CurrencyRate getValuteByValuteCh(String valuteCh, GetCursOnDateXMLResponse.GetCursOnDateXMLResult result) throws Exception{

        CurrencyRate answer = new CurrencyRate();

        List<Object> list = result.getContent();
        ElementNSImpl e = (ElementNSImpl) list.get(0);
        NodeList chCodeList =   e.getElementsByTagName("VchCode");
        int length = chCodeList.getLength();

        boolean isFound = false;
        for (int i = 0; i< length; i++){
            if (isFound) break;

            Node valuteChNode = chCodeList.item(i);
            TextImpl textimpl = (TextImpl)valuteChNode.getFirstChild();
            String chVal = textimpl.getData();

            if (chVal.equalsIgnoreCase(valuteCh)){
                isFound = true;
                Node parent = valuteChNode.getParentNode();
                NodeList nodeList = parent.getChildNodes();
                int paramLength = nodeList.getLength();

                for (int j=0; j<paramLength; j++){
                    Node currentNode = nodeList.item(j);

                    String name = currentNode.getNodeName();
                    Node currentValue = currentNode.getFirstChild();
                    String value = currentValue.getNodeValue();

                    if (name.equalsIgnoreCase("Vcurs")){
                        answer.setByuPrice(value);
                    }
                    if (name.equalsIgnoreCase("Vcurs")){
                        answer.setSalePrice(value);
                    }

                    if (name.equalsIgnoreCase("VchCode")){
                        answer.setName(value);
                    }
                }
            }
        }

        return answer;

    }

    public static XMLGregorianCalendar getXMLGregorianCalendarNow()
            throws DatatypeConfigurationException
    {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar now =
                datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
        return now;
    }


}
