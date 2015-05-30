# WorkTimeTracker

Purpose:

To allow the user to keep track of hours worked at a job and store a record of the hours worked in a spreadsheet.

Functionality:

The user taps on the entries next to the clock symbol when clocking in and out, and going and coming from  on break.  
The spreadsheet shows the times of these cases, calculates the hours worked and stores that number in the spreadsheet.
Implemented using standard native Android with Joda Time and Java Excel API libraries.  

Issues Addressed:

Initially I used Google Spreadsheet but there were problems with the spreadsheet feed urls that I could not address.  Since gutting out the Google Spreadsheet library
I don't have to implement AsyncTasks for the network calls, which I believe allows for more maintainable application.
