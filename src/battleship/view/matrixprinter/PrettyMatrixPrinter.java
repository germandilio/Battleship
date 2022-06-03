package battleship.view.matrixprinter;

import static java.lang.String.format;

public final class PrettyMatrixPrinter {
    private static final char BORDER_KNOT = '+';
    private static final char HORIZONTAL_BORDER = '-';
    private static final char VERTICAL_BORDER = '|';
    private static final String DEFAULT_AS_NULL = "   ";

    private static final Printer<Object> DEFAULT_PRINTER = Object::toString;

    /**
     * Prints the table with DEFAULT_PRINTER.
     *
     * @param table table to print
     * @return String
     */
    public static String print(Object[][] table) {
        return print(table, DEFAULT_PRINTER);
    }

    /**
     * Prints the table with specified printer.
     *
     * @param table   table to print
     * @param printer printer
     * @param <T>     type of data in table
     * @return String
     * @throws IllegalArgumentException if no tabular data provided or no instance of Printer provided
     */
    public static <T> String print(T[][] table, Printer<T> printer) throws IllegalArgumentException {
        if (table == null)
            throw new IllegalArgumentException("No tabular data provided");
        if (table.length == 0)
            return "";
        if (printer == null)
            throw new IllegalArgumentException("No instance of Printer provided");

        final int[] widths = new int[getMaxColumns(table)];
        adjustColumnWidths(table, widths, printer);
        return printPreparedTable(table, widths, getHorizontalBorder(widths), printer);
    }

    private static <T> String printPreparedTable(T[][] table, int[] widths, String horizontalBorder, Printer<T> printer) {
        final int lineLength = horizontalBorder.length();
        StringBuilder sb = new StringBuilder();
        // add numbers of x coordinate
        sb.append(getRowFromLength(lineLength));
        sb.append("  ").append(horizontalBorder).append('\n');
        int counter = 0;
        for (final T[] row : table) {
            if (row != null) {
                // add numbers of y coordinate
                if (Integer.toString(counter).length() == 1) {
                    sb.append(counter).append(" ");
                } else {
                    sb.append(counter);
                }
                counter++;

                sb.append(getRow(row, widths, lineLength, printer)).append('\n');
                sb.append("  ").append(horizontalBorder).append('\n');
            }
        }
        return sb.toString();
    }

    private static String getRowFromLength(int lineLength) {
        StringBuilder builder = new StringBuilder(lineLength);
        builder.append("  ");
        for (int i = 0; i < lineLength / 4; i++) {
            builder.append("  ").append(i).append(" ");
        }
        builder.append('\n');
        return builder.toString();
    }

    private static <T> String getRow(T[] row, int[] widths, int lineLength, Printer<T> printer) {
        final StringBuilder builder = new StringBuilder(lineLength).append(VERTICAL_BORDER);
        final int maxWidths = widths.length;
        for (int i = 0; i < maxWidths; i++) {
            builder.append(padRight(getCellValue(safeGet(row, i), printer), widths[i])).append(VERTICAL_BORDER);
        }
        return builder.toString();
    }

    private static String getHorizontalBorder(int[] widths) {
        final StringBuilder builder = new StringBuilder(256);
        builder.append(BORDER_KNOT);
        for (final int w : widths) {
            builder.append(String.valueOf(HORIZONTAL_BORDER).repeat(Math.max(0, w)));
            builder.append(BORDER_KNOT);
        }
        return builder.toString();
    }

    private static int getMaxColumns(Object[][] rows) {
        int max = 0;
        for (final Object[] row : rows) {
            if (row != null && row.length > max) {
                max = row.length;
            }
        }
        return max;
    }

    private static <T> void adjustColumnWidths(T[][] rows, int[] widths, Printer<T> printer) {
        for (final T[] row : rows) {
            if (row != null) {
                for (int c = 0; c < widths.length; c++) {
                    final String cv = getCellValue(safeGet(row, c), printer);
                    final int l = cv.length();

                    if (widths[c] < l) {
                        widths[c] = l;
                    }
                }
            }
        }
    }

    private static String padRight(String s, int n) {
        return format("%1$-" + n + "s", s);
    }

    private static <T> T safeGet(T[] array, int index) {
        return index < array.length ? array[index] : null;
    }

    private static <T> String getCellValue(T value, Printer<T> printer) {
        return value == null ? DEFAULT_AS_NULL : printer.print(value);
    }
}
