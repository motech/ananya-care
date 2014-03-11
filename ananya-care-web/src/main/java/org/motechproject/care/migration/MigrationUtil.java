package org.motechproject.care.migration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

public class MigrationUtil {
	private static Logger logger = Logger.getLogger(MigrationUtil.class);
	private static final String CSV_EXTENSION = ".csv";
	public static List<String> readFile(String fileName) {
        BufferedReader bufferedReader = null;
        fileName = fileName + CSV_EXTENSION ;
        String line = "";
        String cvsSplitBy = ",";
        List<String> caseIds = new ArrayList<String>();
        logger.info("Reading the migration data from CSV");
        try {
            InputStream inputStream = MigrationUtil.class.getClassLoader().getResourceAsStream(fileName);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            line = bufferedReader.readLine();
            if (line != null) {
                caseIds = Arrays.asList(line.split(cvsSplitBy));
            }
        } catch (FileNotFoundException e) {
            logger.error("Error occurred while read file containing case ids", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("Error occurred while read file containing case ids", e);
            throw new RuntimeException(e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    logger.error("Error occurred while closing readers and streams", e);
                    throw new RuntimeException(e);
                }
            }
        }
        return caseIds;
    }
}
