import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReminderApp {

    /*
     * All reminders use the Europe/Athens timezone.
     * You can delete it or use a different timezone.
    */
    private static final ZoneId GREEK_TIME =
            ZoneId.of("Europe/Athens");

    /*
     * Background scheduler used to keep the application running
     * and trigger reminders at the configured times.
    */
    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {

        /*Reads the JSON folder for birthday dates */
        try {
            ObjectMapper mapper = new ObjectMapper();

            File jsonFile = new File("birthdays.json");

            List<Birthday> birthdays = mapper.readValue(
                    jsonFile,
                    new TypeReference<List<Birthday>>() {}
            );

            System.out.println(
                    "Loaded " + birthdays.size() + " birthdays."
            );

            /*
             * Each birthday is checked twice:
             *
             * 1. checkStartupReminder()
             *    Immediately notifies the user if today is the birthday.
             *
             * 2. scheduleBirthday()
             *    Schedules all remaining reminder times for that birthday.
            */
            for (Birthday birthday : birthdays) {

                checkStartupReminder(birthday);

                scheduleBirthday(birthday);
            }

            System.out.println("Reminder app is running.");

        } catch (Exception e) {
            //Erors are logged in for debugging
            e.printStackTrace();
        }
    }

    private static void scheduleBirthday(Birthday birthday) {

        LocalDate originalBirthday =
                LocalDate.parse(birthday.birthday);

        MonthDay birthdayMonthDay =
                MonthDay.from(originalBirthday);

        ZonedDateTime now =
                ZonedDateTime.now(GREEK_TIME);

        LocalDate today =
                now.toLocalDate();

        // For getting the current year and presenting the dates for the reminder correctly
        LocalDate birthdayThisYear =
                birthdayMonthDay.atYear(now.getYear());

        for (String timeText : birthday.reminderTimes) {

            LocalTime reminderTime =
                    LocalTime.parse(timeText);

            ZonedDateTime reminderDateTime;

            // if birthday is coming up
            if (birthdayThisYear.isAfter(today)) {

                reminderDateTime =
                        ZonedDateTime.of(
                                birthdayThisYear,
                                reminderTime,
                                GREEK_TIME
                        );

            }

            // if birthday is today
            else if (birthdayThisYear.equals(today)) {

                ZonedDateTime todayReminder =
                        ZonedDateTime.of(
                                today,
                                reminderTime,
                                GREEK_TIME
                        );

                // missed times
                if (!todayReminder.isAfter(now)) {
                    System.out.println(
                            "Skipping passed reminder for "
                                    + birthday.name
                                    + " at "
                                    + reminderTime
                    );

                    continue;
                }

                reminderDateTime = todayReminder;

            }

            // missed birthday
            else {

                LocalDate birthdayNextYear =
                        birthdayMonthDay.atYear(
                                now.getYear() + 1
                        );

                reminderDateTime =
                        ZonedDateTime.of(
                                birthdayNextYear,
                                reminderTime,
                                GREEK_TIME
                        );
            }

            long delayMillis =
                    Duration.between(
                            now,
                            reminderDateTime
                    ).toMillis();

            System.out.println(
                    "Scheduled "
                            + birthday.name
                            + " for "
                            + reminderDateTime
            );

            // The delay to reach the day expires and the triggerReminder function is called
            scheduler.schedule(
                    () -> triggerReminder(birthday),
                    delayMillis,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    private static void triggerReminder(Birthday birthday) {

        //Used mainly for debugging in the console
        String message =
                "Today is " + birthday.name + "'s birthday!";

        System.out.println(message);

        if (birthday.showPopup) {
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(
                            null,
                            message,
                            "Birthday Reminder",
                            JOptionPane.INFORMATION_MESSAGE
                    )
            );
        }

        if (birthday.sendEmail) {
            EmailService.sendBirthdayEmail(birthday.name);
        }
    }

    /*
     * Runs once when the application starts, if you don't want to wait for the 
     * specific times set in the JSON.
     *
     * It happens if today's month and day match a stored birthday,
     * the reminder is triggered immediately.
    */
    private static void checkStartupReminder(Birthday birthday) {

        ZonedDateTime now =
                ZonedDateTime.now(GREEK_TIME);

        LocalDate today =
                now.toLocalDate();

        LocalDate originalBirthday =
                LocalDate.parse(birthday.birthday);

        MonthDay birthdayMonthDay =
                MonthDay.from(originalBirthday);

        MonthDay todayMonthDay =
                MonthDay.from(today);

        /*
         * Comparing MonthDay objects ignores the current year,
         * allowing the birthday to repeat automatically every year.
        */
        if (birthdayMonthDay.equals(todayMonthDay)) {

            System.out.println(
                    "Startup birthday reminder for " + birthday.name
            );

            triggerReminder(birthday);
        }
    }
}