package org.motechproject.care.parser;

import org.apache.xerces.parsers.DOMParser;
import org.motechproject.care.domain.Case;
import org.motechproject.care.exception.CaseParserException;
import org.motechproject.care.utils.CaseMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import static org.apache.commons.lang.StringUtils.trimToEmpty;

public class CommcareCaseParser<T> {

    CaseMapper<T> domainMapper;
    private String xmlDoc;
    private String caseAction;

    public CommcareCaseParser(Class<T> clazz, String xmlDocument) {
        domainMapper = new CaseMapper<T>(clazz);
        this.xmlDoc = xmlDocument;
    }

    public T parseCase() throws CaseParserException {
        DOMParser parser = new DOMParser();

        InputSource inputSource = new InputSource();
        inputSource.setCharacterStream(new StringReader(xmlDoc));
        Case ccCase;
        try {
            parser.parse(inputSource);
            ccCase = parseCase(parser.getDocument());
        } catch (IOException ex) {
            throw new CaseParserException(ex, "Exception while trying to parse caseXml");
        } catch (SAXException ex) {
            throw new CaseParserException(ex, "Exception while trying to parse caseXml");
        }

        return domainMapper.mapToDomainObject(ccCase);
    }

    public Case parseCase(Document document) {
        Element item = (Element) document.getElementsByTagName("case").item(0);
        Case ccCase = createCase(item);
        updateAction(ccCase, item);
        return ccCase;
    }

    private Case createCase(Element item) {
        Case ccCase = new Case();

        ccCase.setCase_id(trimToEmpty(item.getAttribute("case_id")));
        ccCase.setApi_key(trimToEmpty(item.getAttribute("api_key")));
        ccCase.setDate_modified(trimToEmpty(item.getAttribute("date_modified")));
        ccCase.setUser_id(trimToEmpty(item.getAttribute("user_id")));
        return ccCase;
    }

    private void updateAction(Case ccCase, Element item) {

        if (getMatchingChildNode(item, "create") != null) {
            setCaseAction(ccCase, "CREATE");
            populateValuesForCreation(ccCase, item);
            populateValuesFor(ccCase, item, "update");

        } else {
            if (getMatchingChildNode(item, "update") != null) {
                setCaseAction(ccCase, "UPDATE");
                populateValuesFor(ccCase, item, "update");
            } else {
                if (getMatchingChildNode(item, "close") != null) {
                    setCaseAction(ccCase, "CLOSE");
                }
            }
        }
        if (getMatchingChildNode(item, "index") != null) {
            populateValuesFor(ccCase, item, "index");
        }
    }

    private void setCaseAction(Case ccCase, String action) {
        this.caseAction = action;
        ccCase.setAction(action);
    }

    private void populateValuesForCreation(Case ccCase, Element item) {
        ccCase.setCase_type(getTextValue(item, "case_type"));
        ccCase.setCase_name(getTextValue(item, "case_name"));
        ccCase.setOwner_id(getTextValue(item, "owner_id"));
    }

    private void populateValuesFor(Case ccCase, Element item, String tagName) {
        Node matchingNode = getMatchingNode(item, tagName);
        if(matchingNode == null) return;
        NodeList childNodes = matchingNode.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (!childNode.getNodeName().contains("text"))
                ccCase.AddFieldvalue(childNode.getNodeName(), trimToEmpty(childNode.getTextContent()));
        }
    }

    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            Node textNode = el.getFirstChild();
            if (textNode != null)
                textVal = textNode.getNodeValue();
        }

        return trimToEmpty(textVal);
    }

    private Node getMatchingChildNode(Element ele, String tagName) {
        return getMatchingNode(ele, tagName);
    }

    private Node getMatchingNode(Element ele, String tagName) {
        Node element = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            element = nl.item(0);
        }
        return element;
    }

    public String getCaseAction() {
        return caseAction;
    }
}