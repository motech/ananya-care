package org.motechproject.commcarehq.service;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.joda.time.DateTime;
import org.motechproject.commcarehq.domain.CareCase;
import org.motechproject.commcarehq.repository.AllCareCases;
import org.motechproject.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;

@Controller
@RequestMapping("/**")
public class EndpointService {

    private AllCareCases allCareCases;

    @Autowired
    public EndpointService(AllCareCases allCareCases) {
        this.allCareCases = allCareCases;
    }

    @RequestMapping(value="/", method= RequestMethod.GET)
    public void index(HttpServletResponse response) {
        try {
            response.getOutputStream().print("hello I am up and running. Relax. :)");
        } catch (IOException e) {
        }
    }

    @RequestMapping(value="/endpoint",method= RequestMethod.POST)
    public void endpoint(@RequestBody String xmlDocument, HttpServletResponse response) {
        ValidationResponse validationResponse = processDocument(xmlDocument);
        validationResponse.sendResponse(response);
    }
    
    

    private CareCase careCase(String xmlDocument) {
        if(StringUtil.isNullOrEmpty(xmlDocument)) {
            throw new IllegalArgumentException();
        }

        DOMParser parser = new DOMParser();

        InputSource inputSource = new InputSource();
        inputSource.setCharacterStream(new StringReader(xmlDocument));

        try {
            parser.parse(inputSource);
            Document document = parser.getDocument();
            return new CareCase(document.getDocumentElement().getAttribute("case_id"), xmlDocument, DateTime.now());

        } catch (IOException ex) {
            throw new MalformedXmlException();
        }
        catch (SAXException ex){
            throw new MalformedXmlException();
        }

    }

    private ValidationResponse processDocument(String xmlDocument) {
        try {
            CareCase careCase = careCase(xmlDocument);
            allCareCases.add(careCase);
        } catch (IllegalArgumentException ex) {
            return ValidationResponse.MISSING;
        }
        catch (MalformedXmlException ex){
            return ValidationResponse.MALFORMED;
        } catch (Exception ex) {
            return ValidationResponse.INTERNAL;
        }

        return ValidationResponse.SUCCESS;
    }
}
