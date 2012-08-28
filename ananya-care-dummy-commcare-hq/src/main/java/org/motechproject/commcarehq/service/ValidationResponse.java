package org.motechproject.commcarehq.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;

public enum ValidationResponse {

    SUCCESS {
        @Override
        public void sendResponse(HttpServletResponse response) {
            sendResponseToClient(response, successResponse(), 200);
        }
    },

    MISSING {
        @Override
        public void sendResponse(HttpServletResponse response) {
            sendResponseToClient(response, errorResponse("MISSING", "MISSING XML", false), 500);
        }
    },

    MALFORMED {
        @Override
        public void sendResponse(HttpServletResponse response) {
            sendResponseToClient(response, errorResponse("MALFORMED", "MALFORMED XML", false), 500);
        }
    },

    INVALID {
        @Override
        public void sendResponse(HttpServletResponse response) {
            sendResponseToClient(response, errorResponse("INVALID", "INVALID XML", false), 500);
        }
    },

    INTERNAL {
        @Override
        public void sendResponse(HttpServletResponse response) {
            sendResponseToClient(response, errorResponse("INTERNAL", "INTERNAL ERROR", true), 500);
        }
    };

    public abstract void sendResponse(HttpServletResponse response);

    protected Document errorResponse(String errorCode, String errorMessage, boolean retry) {
        Document document = responseDocument();
        Element documentElement = document.getDocumentElement();
        documentElement.setAttribute("success", "false");

        Element errorElement = document.createElement("error");
        documentElement.appendChild(errorElement);
        errorElement.setAttribute("errorCode", errorCode);
        errorElement.setAttribute("errorMessage", errorMessage);
        errorElement.setAttribute("retry", Boolean.toString(retry));
        return document;
    }


    protected Document successResponse() {
        Document document = responseDocument();
        Element documentElement = document.getDocumentElement();
        documentElement.setAttribute("success", "true");
        return document;
    }

    private Document responseDocument() {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element documentElement = document.createElement("response");
            document.appendChild(documentElement);
            return document;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private String toString(Document doc) {

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();;
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter stringWriter = new StringWriter();
            StreamResult result = new StreamResult(stringWriter);
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
            String xmlString = stringWriter.toString();
            return xmlString;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void sendResponseToClient(HttpServletResponse response, Document responseDocument, int responseCode) {
        try {
            response.setStatus(responseCode);
            response.getOutputStream().print(toString(responseDocument));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
