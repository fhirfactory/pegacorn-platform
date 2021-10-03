/*
 * Copyright (c) 2020 ACT Health
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.util;

import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

@ApplicationScoped
public class DateUtility {

    private String DD_MMM_YY = "dd-MMM-yy";
    private String DD_MMM_YYYY = "dd-MMM-yyyy";
    private String D_M_YYYY_H_MM_SS = "d/M/yyyy h:mm:ss";
    private String YYYY_MM_DD_T_HH_MM_SS_Z_INPUT = "yyyy-MM-dd'T'HH:mm:ssz";
    private String YYYY_MM_DD_T_HH_MM_SS_Z_OUTPUT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private String YYYY_MM_DD_T_HH_MM_SS_ZOFFSET = "yyyy-MM-dd'T'HH:mm:ss[+-]hh:mm";
    private String DD_MM_YYYY = "dd/MM/yyyy";
    private String DEFAULT_TIME_ZONE = "Australia/Sydney";

    private DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_Z_INPUT);
    private DateTimeFormatter TIMESTAMP_OUTPUT_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_Z_OUTPUT);
    private String TIMESTAMP_DISPLAY_FORMAT = YYYY_MM_DD_T_HH_MM_SS_ZOFFSET;

    public String timestampFormatter( LocalDateTime callTime, DateTimeFormatter timestampFormatter ) {

        String timestampString = callTime.atZone(getZoneId()).format(timestampFormatter);
        int indexThirdLastCharacter = timestampString.length() - 3;
        if (timestampString.charAt(indexThirdLastCharacter) != ':') {
            timestampString = timestampString.substring(0, indexThirdLastCharacter + 1) + ":" +
                    timestampString.substring(indexThirdLastCharacter + 1);
        }
        return(timestampString);
    }

    public Date getDateFrom(String timestamp, DateTimeFormatter timestampFormatter) {
        if (StringUtils.isBlank(timestamp)) {
            return new Date(2999, 12, 31, 23, 59, 59);
        }

        Date date = getDateFrom(ZonedDateTime.parse(timestamp, timestampFormatter));
//        getLogger().debug(".getDateFrom(" + timestamp + ")=" + date);
        return date;
    }

    public Date getDateFrom(ZonedDateTime dateToConvert) {
        return Date.from(dateToConvert.toInstant());
    }

    public ZoneId getZoneId() {
//        return ZoneId.systemDefault(); // Returned GMT
        return ZoneId.of(DEFAULT_TIME_ZONE);
    }
    /**
     * Validates the provided timestamp against the supplied formatter.
     *
     * @param timestamp
     * @param formatter
     * @return
     */
    public static boolean isValidTimestamp(String timestamp, DateTimeFormatter formatter) {
        try {
            ZonedDateTime.parse(timestamp, formatter);
        } catch (DateTimeParseException e) {
            return false;
        }

        return true;
    }

    /**
     * Returns true if the from timestamp is before the to timestamp.
     *
     * @param from
     * @param to
     * @param formatter
     * @return
     */
    public boolean isFromBeforeTo(String from, String to, DateTimeFormatter formatter) {
        ZonedDateTime fromTimestamp = ZonedDateTime.parse(from, formatter);
        ZonedDateTime toTimestamp = ZonedDateTime.parse(to, formatter);

        return (fromTimestamp.isBefore(toTimestamp));
    }

    public String getDD_MMM_YY() {
        return DD_MMM_YY;
    }

    public String getDD_MMM_YYYY() {
        return DD_MMM_YYYY;
    }

    public String getD_M_YYYY_H_MM_SS() {
        return D_M_YYYY_H_MM_SS;
    }

    public String getYYYY_MM_DD_T_HH_MM_SS_Z_INPUT() {
        return YYYY_MM_DD_T_HH_MM_SS_Z_INPUT;
    }

    public String getYYYY_MM_DD_T_HH_MM_SS_Z_OUTPUT() {
        return YYYY_MM_DD_T_HH_MM_SS_Z_OUTPUT;
    }

    public String getYYYY_MM_DD_T_HH_MM_SS_ZOFFSET() {
        return YYYY_MM_DD_T_HH_MM_SS_ZOFFSET;
    }

    public String getDD_MM_YYYY() {
        return DD_MM_YYYY;
    }

    public String getDEFAULT_TIME_ZONE() {
        return DEFAULT_TIME_ZONE;
    }

    public String getDefaultPegacornInternalDate() {
        return (getYYYY_MM_DD_T_HH_MM_SS_ZOFFSET());
    }

    public DateTimeFormatter getTIMESTAMP_FORMATTER() {
        return TIMESTAMP_FORMATTER;
    }

    public DateTimeFormatter getTIMESTAMP_OUTPUT_FORMATTER() {
        return TIMESTAMP_OUTPUT_FORMATTER;
    }

    public String getTIMESTAMP_DISPLAY_FORMAT() {
        return TIMESTAMP_DISPLAY_FORMAT;
    }
}
