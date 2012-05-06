package org.motechproject.care.utils;

import org.antlr.stringtemplate.StringTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringTemplateHelper {

    public static StringTemplate getStringTemplate(String templateFilePath) {
        InputStream resourceAsStream = StringTemplateHelper.class.getResourceAsStream(templateFilePath);
        String template = getText(resourceAsStream);
        return new StringTemplate(template);
    }

    private static String getText(InputStream in) {
        String text = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            text = sb.toString();
        } catch (Exception ex) {

        } finally {
            try {

                in.close();
            } catch (Exception ex) {
            }
        }
        return text;
    }
}
