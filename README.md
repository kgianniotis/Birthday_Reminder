# Birthday_Reminder

A custom JAVA application that helps you remember birthdays and other important dates. The application can display desktop pop-up notifications and send email reminders when configured dates and times are reached.

It runs locally on your Windows PC and does not require an external server. Because of this, your computer needs to be turned on and the application needs to be running.

## Architecture

The application read the birthdays form the JSON file, through the `ReminderApp.java` and calculates the upcoming reminders and sents notifications to the computer through Java Swing pop-up windows and emails through Gmail SMTP and Jakarta Mail.

Windows Task Scheduler is used to automatically start the application whenever the user logs in their computer.
This is done by creating a new task, using the applications `start-reminder.bat` file.


## Requirements
- Java 17 or newer
- Apache Maven
- Windows OS
- Gmail account for emails to be setn


## Download and Date Setup

1. Download or clone the project from Github and unzip it
2. Open the program from an editor of your choise
3. Go to `birthdays.json`. and input the required birthdays and days, using the templates as reference.
4. Go to `start-reminder.bat` and change the cd to the location of the program folder.
5. From the root of the program run <span style="color:red">mvn clean compile</span> and <span style="color:red">mvn exec:java</span>. 
6. If you modify Java source files yuo will need to run <span style="color:red">mvn clean compile</span>. If you only modify `birthdays.json`, you can just restart the application, without compiling again.

## Reminder Program Behaviour

When the application starts, it checks whether any birthday occurs on the current day.

If a birthday is today, an immediate startup reminder is triggered.

For example, if the configured reminder times are:

```text
00:00
12:00
18:00
```

and the computer is started at `10:00`, the behaviour will be:

```text
10:00 → Immediate startup reminder
12:00 → Scheduled reminder
18:00 → Scheduled reminder
```

However, reminder times that have already passed are ignored for that day.

For example, if the application starts at `13:00`:

```text
00:00 → Skipped
12:00 → Skipped
13:00 → Immediate startup reminder
18:00 → Scheduled reminder
```

## Email Service Setup

Jakarta Mail is the Java library used for creating and sending emails. It works like an email client library, by building the email using the information provided by the application and prepares it to be sent.

Gmail SMTP is the actual email delivery service. Jakarta Mail connects to Gmail’s SMTP server, authenticates with your Gmail account, and sends the prepared email through Google’s mail system.

1. Go to `EmailService.java` and change the USERNAME and RECIPIENT variables to your own desired ones. 
2. For the APP_PASSWORD variable got to your Google Account's App Passwords and create a new 16 character password. For security purposes, don't hardcode it inside the program.
3. Open Powershell and enter <span style="color:red">setx BIRTHDAY_EMAIL_APP_PASSWORD "your-16-character-app-password"</span>.
4. Close and reopen Powershell.
5. To verify that the password has been saved, enter <span style="color:red">echo $env:BIRTHDAY_EMAIL_APP_PASSWORD"</span>.

## Task Scheduler Setup

1. Open the Task Scheduler. If you are having troubles finding it, press the Windows + R keys and type <span style="color:red">taskschd.msc</span>.
2. On the Actions panel, click Create Task.
3. On the General Tab, give the Task and appropriate name.
4. On the Triggers Tab, press New and choose an appropriate Begin the Task method.
5. On the Actions Tab, select the Start a program Action and select the start-reminder.bat file from the program folder. 
6. From the Settigns Tab, make sure that at the bottom, the <span style="color:yellow">If the task is already running, the the following rule applies: </span> is set to <span style="color:red">Do not start a new instance</span>.

## Logging

When the application is launched through `start-reminder.bat`, console output is stored in `birthday-reminder.log`, which can be found inside the application folder.

The log can be used to check whether the program started correctly and which reminders were scheduled.

## Important Clarifications

- The application is set to Europe/Athens timezone, so it will handle the appropriate daylight-saving time in Greece.
- Your normal Gmail password will not work. Google needs a seperate App Password and a 2-Factor Authentication.

## Future Work

As mentioned above, the computer needs to be running in order for the panels to be displayed and the emails to be sent. 

This could be improved by migrating the application to an always-on server or a cloud environment.

Another upgrade would be the rework of the program into a mobile application with a Firebase database, where users can store birthdays there and more easily retrive them and receive notifications.
