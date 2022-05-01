// Joseph "Joey" Krueger
// 03/15/2022
// CS 141
// 
//    A: events can be created using user input "ev" and are stored in a file via a 2D jagged array
//    B: files are read into the console of the box they belong, and are clipped off by the maximum
//       length value set int the main method if they are too long
//    C: entire months can be printed into their own file, using out.print statements and passing
//       PrintStream out through the drawMethod. This is triggered by "fp"
//    D: for extra credit, I used a multidimensional jagged array,
//       and incorporated more switch cases into my code
//       to make it cleaner
//       All of this is also seamless; to the best of my knowledge, there are no glitches or bugs!
//

import java.util.Scanner;
import java.util.Calendar;
import java.io.*;
import java.util.regex.Pattern;

public class Calendar1
{
    public static void main(String[] args)
      throws FileNotFoundException
    {        
        Scanner console = new Scanner(System.in);
        Calendar today = Calendar.getInstance();
        final int HEIGHT = 2;  // scalability values; could be implimented to include more
        final int LENGTH = 10; // room for events with long charecters in the future!
        int LEAPYEAR = 0;       
        if(today.get(Calendar.YEAR) % 4 == 0)
        {
            LEAPYEAR = 1; // leapyear as an Integer makes it easier to calculate dayOfYear
        }        
        final String fileName = "calendarEvents.txt";
        loadEvents(fileName, LEAPYEAR, LENGTH);
        menuMethod(console, today, fileName, HEIGHT, LENGTH, LEAPYEAR); 
    }              // everything else passes through the menu
    
    
    public static String[][] eventArray; // global jagged array; extra credit!
    
    
    public static void loadEvents(String fileName, int LEAPYEAR, int LENGTH)
    {
        eventArray = new String[12][]; // this method makes the array jagged
        for(int i = 0; i < 12; i++)
            eventArray[i] = new String[findMonthValue(i + 1, LEAPYEAR)];
        try 
        {
            Scanner input = new Scanner(new File(fileName));
            while(input.hasNext())
            {
                String validInput = input.next();
                int day = dayFromDate(validInput);
                int month = monthFromDate(validInput);
                String event = input.nextLine().trim();
                eventArray[month-1][day-1]
                    = event.substring(0, Math.min(event.length(), LENGTH));
            }
        }
        catch (FileNotFoundException e) 
        {
            System.out.println(e.getMessage()); 
        }                                       // found this code on stack overflow
    } // END loadEvents method
    
    
    
    public static void menuMethod(Scanner console, 
                                  Calendar today,
                                  String fileName, 
                                  int HEIGHT, 
                                  int LENGTH, 
                                  int LEAPYEAR)
      throws FileNotFoundException
    {
        String selection = "";
        int month = 0;
        int day = 0;
        int monthValue = 0;
        int dayOfYear = 0;
        boolean enterQuit = false;
        System.out.println("Welcome"); // Intro
        System.out.println();
        selectionMenu();
        while(!selection.matches("q")) // the entire program runs inside this while loop
        {
            System.out.print("Please type a command: ");
            selection = console.next();            
            switch(selection)
            {
                default:
                    System.out.println("\nThat is not a valid input\n");
                    selectionMenu(); 
                    break;          
                    
                case "e":
                    System.out.print("What date would you like to look at? (type as mm/dd): ");
                    String validInput = (inputConversion(console, LEAPYEAR)); 
                    month = monthFromDate(validInput); 
                    day = dayFromDate(validInput);                         
                    dayOfYear = findDayOfYear(month, day, LEAPYEAR);
                    monthValue = findMonthValue(month, LEAPYEAR);   
                    String monthName = findMonthName(month);
                    drawRow(System.out, 
                            month,      // drawRow is the main method that prints
                            monthName,  // the entire calendar into the console
                            monthValue, // (or into it's own file)
                            HEIGHT, 
                            LENGTH, 
                            day, 
                            dayOfYear);
                    break;
                    
                case "t":             
                    month = today.get(Calendar.MONTH) + 1;
                    day = today.get(Calendar.DATE);
                    dayOfYear = findDayOfYear(month, day, LEAPYEAR);
                    monthValue = findMonthValue(month, LEAPYEAR); 
                    monthName = findMonthName(month);
                    drawRow(System.out, 
                            month, 
                            monthName, 
                            monthValue, 
                            HEIGHT, 
                            LENGTH, 
                            day, 
                            dayOfYear);
                    break;
                
                case "n":
                    if(month != 0) // I could have created a similar method for both "n" and "p"
                    {              // But I felt was too small to be worth it
                        if(month == 12)
                        {
                            month -= 11;
                        } else {
                            month += 1;
                        }
                        monthValue = findMonthValue(month, LEAPYEAR);
                        while(day > monthValue)
                        {
                            day--; // day = 31 in January transitions into day = 28 in February 
                        }
                        dayOfYear = findDayOfYear(month, day, LEAPYEAR);
                        monthName = findMonthName(month);
                        drawRow(System.out, 
                                month, 
                                monthName, 
                                monthValue, 
                                HEIGHT, 
                                LENGTH, 
                                day, 
                                dayOfYear);
                    } else {
                        System.out.println("\nyou have to enter \"e\" or \"t\" first!\n");
                    }
                    break;
                    
                case "p":
                    if(month != 0)
                    {
                        if(month == 1)
                        {
                            month += 11;
                        } else {
                            month -= 1;
                        }
                        monthValue = findMonthValue(month, LEAPYEAR);
                        while(day > monthValue)
                        {
                            day--; // day = 31 in March transitions into day = 28 in February 
                        }
                        dayOfYear = findDayOfYear(month, day, LEAPYEAR);
                        monthName = findMonthName(month);
                        drawRow(System.out, month, monthName, monthValue, HEIGHT, LENGTH, day, dayOfYear);
                    } else {
                        System.out.println("\nyou have to enter \"e\" or \"t\" first!\n");
                    }
                    break;
        
                case "ev":                    
                    System.out.print("What date would you like to"); 
                    System.out.print("create an event for? (type as mm/dd): ");
                    validInput = (inputConversion(console, LEAPYEAR));
                    System.out.print("What is the name of this event? ");
                    System.out.print("(must be less than " + LENGTH + " charecters): ");
                    String event = console.nextLine().trim(); //.trim() gets rid of space
                    String dateAndEvent = validInput + " " + event;
                    File log = new File(fileName); 
                    try // now that string is isolated, it can be written
                    {
                        if(log.exists()==false)
                        {
                            System.out.println("New file created");
                            log.createNewFile();
                        }
                    PrintWriter out = new PrintWriter(new FileWriter(log, true));
                    out.println();  //used the PrintWriter object here, found on StackOverflow
                    out.append(dateAndEvent); //this adds it on without erasing the whole file
                    out.close();
                    } catch(IOException e){
                        System.out.println("Could not write event");
                    }
                    loadEvents(fileName, LEAPYEAR, LENGTH); // this makes sure the new
                    break;                       // event is cut off to "LENGTH" chars 
                
                case "fp":
                    System.out.print("What month would you like to print? (mm) ");
                    month = console.nextInt();
                    day = 0; //because "day" is tied up in selection, we set it to 0
                    dayOfYear = findDayOfYear(month, day + 1, LEAPYEAR);
                    monthValue = findMonthValue(month, LEAPYEAR); 
                    monthName = findMonthName(month);
                    System.out.print("Output file name: "); 
		            String nameOut = console.next();
                    try 
                    {   // the file is drawn, but this time outFile.out, not System.out
                        PrintStream outFile = new PrintStream(new File(nameOut));
                        drawRow(outFile, 
                                month, 
                                monthName, 
                                monthValue, 
                                HEIGHT, 
                                LENGTH, 
                                day, 
                                dayOfYear);
                        outFile.close();
                    }
                    catch (FileNotFoundException e) 
                    {
                        System.out.println(e.getMessage());
                    }
                    break;

                case "q":
                    enterQuit = true;
                    System.out.print("Goodbye!"); 
                    break; 
            } // end switch/case
        } // end while loop         
    } // END menuMethod
    
   
    
    public static void selectionMenu()
    {
        System.out.println("\t\"e\" to enter a date and display the corresponding calendar");
        System.out.println("\t\"t\" to get today's date and display today's calendar");
        System.out.println("\t\"n\" to display the next month");
        System.out.println("\t\"p\" to display the previous month");
        System.out.println("\t\"q\" to quit the program");
        System.out.println("\t\"ev\" to enter a date and create a new event or replace an existing one");
        System.out.println("\t\"fp\" to print a particular month of the calendar to a new file");
        System.out.println();
    }
 
           

    public static String inputConversion(Scanner console, int LEAPYEAR)
    {
        Pattern pattern = Pattern.compile("^[0-9]+$"); // Pattern object determines if testInput uses
        String testInput = "null";                     // convertable integers
        int dayOfYear = 0;                             
        while(dayOfYear == 0)                          // no crashes here!
        {
            testInput = console.next();
            if(testInput.length() == 5)
            {   
                if(pattern.matcher(testInput.substring(0,2) +
                                   testInput.substring(3,5)).matches()) 
                {
                    int month = Integer.parseInt(testInput.substring(0,2));
                    int day = Integer.parseInt(testInput.substring(3,5));
                    dayOfYear = findDayOfYear(month, day, LEAPYEAR);
                    if(dayOfYear == 0)
                    {
                        System.out.println("This date does not exist");
                    }
                } else {   
                    System.out.println("mm/dd must be in integers");
                }
            } else {
                System.out.println("there should be exactly 5 charecters in your input");
            }
        }
        return testInput;
    } // END of inputConversion



    public static int monthFromDate(String validInput) // extracts day value from inputConversion
    {
        int mm = Integer.parseInt(validInput.substring(0,2));
        return mm;
    } 
        
        
        
    public static int dayFromDate(String validInput) // extracts month value from inputConversion
    {  
        int dd = Integer.parseInt(validInput.substring(3,5));        
        return dd;
    } 



    public static void drawRow(PrintStream out,
                               int month, 
                               String monthName, 
                               int monthValue,
                               int HEIGHT,
                               int LENGTH, 
                               int day, 
                               int dayOfYear)
    {                                   
        header(out, monthName);                 // this method prints the calendar          
        weekDay(out, LENGTH);
        line(out, LENGTH);                         
        int countDay = 0;
        int extension = 5;
        for(int j = 1; j <= extension; j++)
        {
            if((countDay + 7) < monthValue)
            {   
                extension = 6; // months like January will print 6 rows 
            } else {
                extension = 5; // days don't get cut off
            }                                   
            countDay = fillInDate(out,
                                  HEIGHT, 
                                  LENGTH, 
                                  countDay, 
                                  day, 
                                  month, 
                                  monthValue, 
                                  dayOfYear); // fills in days where they belong      
            line(out, LENGTH);  
        }
        displayDate(out, month, day);
    } // END of drawRow method
    
    
    
    public static void header(PrintStream out, String monthName) // ASCII art!
    {       
        System.out.println();
        for (int line = 1; line <= 5; line++) 
        {
            for (int j = 1; j <= (-1 * line + 6); j++) 
            {
                out.print("~");
                if(line == 3 && j == 3)
                {
                    out.printf("   %s   ~~~", monthName);
                    } else if(j == line && line != 3) {     
                    int nameLength = monthName.length();    
                    out.print("   ");                
                    for (int i = 1; i <= nameLength; i++)
                    {
                    out.print(" ");
                    }
                    out.print("   ~~~");
                    } else { 
                }                
            }
            out.println();
        }   
    } // END of Header method
    
    
    
    public static void weekDay(PrintStream out, int LENGTH) // prints days of the week
    {
        out.print("   ");
        String weekDay[] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for(int i = 0; i <= 6; i++)
        {
            out.print(weekDay[i]);
            gap(out, LENGTH, 3);
        }
        out.println();
    } // END weekDay method
        
   
    
    public static void gap(PrintStream out, int LENGTH, int lengthCorrection)
    {
        for(int j = 0; j <= (LENGTH - lengthCorrection); j++) 
        {
            out.print(" ");  // this method makes it easier to print spaces
        }   // without having to create a for loop every time
    } // END gap method
    
       
     
    public static void line(PrintStream out, int LENGTH) //this method prints =========
    {
        for(int i = 1; i <= (LENGTH * 7 + 8); i++)
        {
            out.print("=");
        }
        out.println();
    } // END line method


           
    public static int fillInDate(PrintStream out,
                                 int HEIGHT, 
                                 int LENGTH, 
                                 int countDay, 
                                 int day, 
                                 int month,
                                 int monthValue, 
                                 int dayOfYear) 
    {   
        int whileDay = day; 
        while(whileDay > 1)
        {
            whileDay--;  // uses the logic from the last test question on the exam!
            dayOfYear--; // finds the starting dayOfYear of any given month
        }
        dayOfYear += 5; // at +5, January (and every month after it) will print on the correct weekday
        dayOfYear %= 7; // when dayOfYear is divisible by 7, the calender starts printing dates
        int startDate = 0; 
        for(int j = 1; j <= 7; j++)  
        {  
            if(countDay == 0 && startDate != dayOfYear)
            {
                out.print("|");
                gap(out, LENGTH, 1);
                startDate++;
            } else if(countDay < (monthValue)) {     
                countDay++;
                dayGap(out, LENGTH, countDay, day);      
            } else {    
                countDay++;
                out.print("|");
                gap(out, LENGTH, 1);                                                                                                                                                                                        
            } 
        } 
        out.println("|");
        for(int h = HEIGHT; h > 0; h--)  //this for loop lets height be adjusted globally
        {
            space(out, h, HEIGHT, LENGTH, countDay - 6, month, monthValue);
        }
        return countDay;
    } // END of drawRow method  



    public static void dayGap(PrintStream out, int LENGTH, int countDay, int day) 
    {
        if(countDay < 10)   // this method prints spaces between the day      
        {                   // and the next box
            if(countDay == day)
            {
                out.print("|");
                out.print(countDay);
                out.print(" <-");
                gap(out, LENGTH, 5);
            } else {
                out.print("|");
                out.print(countDay);
                gap(out, LENGTH, 2);
            }
        } else {
            if(countDay == day)
            {
                out.print("|");
                out.print(countDay);
                out.print(" <-");
                gap(out, LENGTH, 6);
            } else {
                out.print("|");
                out.print(countDay);
                gap(out, LENGTH, 3);
            }
        }                       
    } // END dayGap

    
    
    public static void space(PrintStream out,
                             int h,
                             int HEIGHT, 
                             int LENGTH, 
                             int countDay, 
                             int month, 
                             int monthValue) 
    {                                        //spacing between date() and line()
        if(h == HEIGHT)   //but also creates the proper amount of room for events
        {
            for(int i = 1; i <= 7; i++)
            {               
                if(countDay >= 1 && countDay <= monthValue)
                {
                    String event = eventArray[month - 1][countDay-1];
                    if(event == null)
                    event = "";
                    out.print("|");
                    out.print(event);
                    gap(out, LENGTH, 1 + event.length());
                    countDay++;
                } else {
                    out.print("|");
                    gap(out, LENGTH, 1);
                    countDay++;
                }
            }
        } else {
            for(int j = 1; j <= 7; j++)
            {
                out.print("|");
                gap(out, LENGTH, 1);//- wordlength
                countDay++;
            }                                   
        }           
        out.println("|");                                                           
    } // END space method
    
    
    
    public static void displayDate(PrintStream out, int month, int day) // prints the selected day at the end
    {
        out.printf("Month: %d\n", month);
        out.printf("Day:   %d\n\n", day);
    } 



    public static String findMonthName(int month) //finds the correct month name
    {
        String monthName = "December";

        switch(month) 
        {               
            case 1: 
                monthName = "January";
                break;
            case 2: 
                monthName = "February";
                break;
            case 3:
                monthName = "March";                  
                break;
            case 4:
                monthName = "April";
                break;
            case 5:
                monthName = "May";
                break;
            case 6:
                monthName = "June";
                break;
            case 7:
                monthName = "July";
                break;
            case 8:
                monthName = "August";
                break;
            case 9:
                monthName = "September";
                break;
            case 10:
                monthName = "October";
                break;
            case 11:
                monthName = "November";
                break;
        }                   
        return monthName;
    } // END FindMonthsName method                  

    public static int findMonthValue(int month, int LEAPYEAR)   // finds # of days of
    {                                                               // any given month
        int monthValue = 31;    // (December)
                                
        switch(month)    
        {                       
            case 1: 
                monthValue = 31;
                break;
            case 2: 
                if(LEAPYEAR == 1)
                {
                    monthValue = 29;        // Leapyear!
                } else {   
                    monthValue = 28;
                }
                break;
            case 3:
                monthValue = 31;
                break;
            case 4:
                monthValue = 30;
                break;
            case 5:
                monthValue = 31;
                break;
            case 6:
                monthValue = 30;
                break;
            case 7:
                monthValue = 31;
                break;
            case 8:
                monthValue = 31;
                break;
            case 9:
                monthValue = 30;
                break;
            case 10:
                monthValue = 31;
                break;
            case 11:
                monthValue = 30;
                break;
        }                   
        return monthValue;
    } // END findMonthsDays method                  

    public static int findDayOfYear(int month, int day, int LEAPYEAR)   // finds dayOfYear
    {                                                                   // of inputted month
        int dayOfYear = 0;  
                                // there may be an easier way to do this...
        switch(month)          
        {                       
            case 1: 
                if(day > 0 && day <= 31)
                { 
                    dayOfYear = day; 
                }               
                break;
            case 2: 
                if(day > 0 && day <= 28 + LEAPYEAR)
                { 
                    dayOfYear = day + 31;
                }               
                break;
            case 3:
                if(day > 0 && day <= 31)
                { 
                    dayOfYear = day + 59 + LEAPYEAR; 
                }               
                break;
            case 4:
                if(day > 0 && day <= 30)
                { 
                    dayOfYear = day + 90 + LEAPYEAR; 
                }               
                break;
            case 5:
                if(day > 0 && day <= 31)
                { 
                    dayOfYear = day + 120 + LEAPYEAR; 
                }               
                break;
            case 6:
                if(day > 0 && day <= 30)
                { 
                    dayOfYear = day + 151 + LEAPYEAR; 
                }               
                break;
            case 7:
                if(day > 0 && day <= 31)
                { 
                    dayOfYear = day + 181 + LEAPYEAR; 
                }               
                break;
            case 8:
                if(day > 0 && day <= 31)
                { 
                    dayOfYear = day + 212 + LEAPYEAR; 
                }               
                break;
            case 9:
                if(day > 0 && day <= 30)
                { 
                    dayOfYear = day + 243 + LEAPYEAR; 
                }               
                break;
            case 10:
                if(day > 0 && day <= 31)
                { 
                    dayOfYear = day + 273 + LEAPYEAR; 
                }               
                break;
            case 11:
                if(day > 0 && day <= 30)
                { 
                    dayOfYear = day + 304 + LEAPYEAR; 
                }               
                break;
            case 12:
                if(day > 0 && day <= 31)
                { 
                    dayOfYear = day + 334 + LEAPYEAR; 
                }    
        }                   
        return dayOfYear;
    } // END findMonthsDays method   
                 
} //end class