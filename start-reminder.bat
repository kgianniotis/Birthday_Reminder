@echo off
cd /d "C:\Users\path\to\application"
echo ======================================== >> birthday-reminder.log
echo Started: %date% %time% >> birthday-reminder.log

mvn exec:java >> birthday-reminder.log 2>&1