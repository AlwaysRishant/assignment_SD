package assignment;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
class EmployeeRecord {
    String positionID;
    Date timeIn;
    Date timeOut;

    EmployeeRecord(String positionID, Date timeIn, Date timeOut) {
        this.positionID = positionID;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }
}
class EmployeeDataAnalyzer {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
    public static void main(String[] args) {
        String filename = "C:\\Users\\HP\\OneDrive\\Documents\\NetBeansProjects\\assignment\\src\\data_file\\assignment_file.pdf";  // Update this with your actual file path
        try {
            analyzeEmployeeRecords(filename);
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }
    private static void analyzeEmployeeRecords(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip header
            EmployeeRecord previousRecord = null;
            int recordNumber = 1;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 9) {
                    try {
                        Date timeIn = parseDate(parts[2]);
                        Date timeOut = parseDate(parts[3]);
                        if (timeIn != null && timeOut != null) {
                            EmployeeRecord currentRecord = new EmployeeRecord(parts[0], timeIn, timeOut);
                            if (previousRecord != null && daysBetween(previousRecord.timeOut, currentRecord.timeIn) >= 7) {
                                System.out.println("Employee ID: " + previousRecord.positionID +
                                        " has worked for 7 consecutive days.");
                            }
                            long hoursBetween = hoursBetween(previousRecord.timeOut, currentRecord.timeIn);
                            if (previousRecord != null && hoursBetween > 1 && hoursBetween < 10) {
                                System.out.println("Employee ID: " + previousRecord.positionID +
                                        " has less than 10 hours but more than 1 hour between shifts.");
                            }
                            long hoursInShift = hoursBetween(currentRecord.timeIn, currentRecord.timeOut);
                            if (hoursInShift > 14) {
                                System.out.println("Employee ID: " + currentRecord.positionID +
                                        " has worked for more than 14 hours in a single shift.");
                            }
                            previousRecord = currentRecord;
                        }
                    } catch (ParseException e) {
                        System.err.println("Invalid date format for record " + recordNumber + ": " + e.getMessage());
                    }
                } else {
                    System.err.println("Invalid record format at record " + recordNumber + ": " + line);
                }
                recordNumber++;
            }
        }
    }
    private static Date parseDate(String dateString) throws ParseException {
        if (dateString == null || dateString.trim().isEmpty())
            return null;
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new ParseException("Unparseable date: " + dateString, 0);
        }
    }
    private static long daysBetween(Date startDate, Date endDate) {
        long difference = Math.abs(endDate.getTime() - startDate.getTime());
        return difference / (24 * 60 * 60 * 1000);
    }
    private static long hoursBetween(Date startDate, Date endDate) {
        long difference = Math.abs(endDate.getTime() - startDate.getTime());
        return difference / (60 * 60 * 1000);
    }
}