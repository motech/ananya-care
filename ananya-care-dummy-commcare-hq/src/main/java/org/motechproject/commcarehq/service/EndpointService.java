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
        ValidationResponse validationResponse = validateDocument(xmlDocument);

        if(validationResponse == ValidationResponse.SUCCESS) {
            try {
                allCareCases.add(new CareCase(xmlDocument, DateTime.now()));
            } catch(RuntimeException ex) {
                ex.printStackTrace();
                validationResponse = ValidationResponse.INTERNAL;
            }
        }
        validationResponse.sendResponse(response);
    }

    private ValidationResponse validateDocument(String xmlDocument) {
        if(StringUtil.isNullOrEmpty(xmlDocument)) {
            return ValidationResponse.MISSING;
        }

        DOMParser parser = new DOMParser();

        InputSource inputSource = new InputSource();
        inputSource.setCharacterStream(new StringReader(xmlDocument));

        try {
            parser.parse(inputSource);
        } catch (IOException ex) {
            return ValidationResponse.MALFORMED;
        }
        catch (SAXException ex){
            return ValidationResponse.MALFORMED;
        }

        return ValidationResponse.SUCCESS;
    }
}
