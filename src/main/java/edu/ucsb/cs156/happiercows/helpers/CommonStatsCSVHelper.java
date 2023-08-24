package edu.ucsb.cs156.happiercows.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import edu.ucsb.cs156.happiercows.entities.CommonStats;

public class CommonStatsCSVHelper {

    private static final List<String> HEADERS = Arrays.asList(
            "id", "commonsId", "numCows", "avgHealth", "timestamp"
    );

    // Private constructor to prevent instantiation of utility class.
    private CommonStatsCSVHelper() {}

        /**
     * Flush and close the given output stream and CSV printer.
     * 
     * @param out stream to close
     * @param csvPrinter printer to close
     * @throws IOException if there's an error during flushing or closing
     */
    private static void flushAndClose(ByteArrayOutputStream out, CSVPrinter csvPrinter) throws IOException {
        csvPrinter.flush();
        csvPrinter.close();
        out.flush();
        out.close();
}

    /**
     * Convert the given Iterable of CommonStats into a ByteArrayInputStream suitable for CSV export.
     *
     * @param lines The Iterable collection of CommonStats to be exported.
     * @return A ByteArrayInputStream representation of the CSV data.
     * @throws IOException if there's an error during CSV generation.
     */
    public static ByteArrayInputStream toCSV(Iterable<CommonStats> lines) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT);

        csvPrinter.printRecord(HEADERS);

        for (CommonStats stats : lines) {
            List<String> data = Arrays.asList(
                    String.valueOf(stats.getId()),
                    String.valueOf(stats.getCommonsId()),
                    String.valueOf(stats.getNumCows()),
                    String.valueOf(stats.getAvgHealth()),
                    String.valueOf(stats.getTimestamp())
            );
            csvPrinter.printRecord(data);
        }

        flushAndClose(out, csvPrinter);

        return new ByteArrayInputStream(out.toByteArray());
    }
}
