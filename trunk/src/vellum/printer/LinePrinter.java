/*
 * Apache Software License 2.0
 * Supported by iPay (Pty) Ltd, BizSwitch.net
 * Contributed (2013) by Evan Summers via https://code.google.com/p/vellum
 */

package vellum.printer;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author evan.summers
 */
public class LinePrinter implements Printer {
    List<String> lineList = new LinkedList();

    public LinePrinter() {
    }

    public void printf(String format, Object ... args) {
        lineList.add(String.format(format, args));
    }

    public void printlnf(String format, Object ... args) {
        lineList.add(String.format(format, args));
    }

    public void println() {
        lineList.add(null);
    }

    public void println(Object object) {
        lineList.add(object.toString());
    }

    public void print(Object object) {
        lineList.add(object.toString());
    }


    public List<String> getLineList() {
        return lineList;
    }

    public void flush() {
    }

    public void close() {
    }


}
