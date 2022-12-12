import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class SIS {
    private static String fileSep = File.separator;
    private static String lineSep = System.lineSeparator();
    private static String space   = " ";

    private List<Student> studentList = new ArrayList<>();

    public SIS(){ processOptics(); }

    private void processOptics() {
        // TODO

        try(Stream<Path> files = Files.list(Paths.get("input"))){
            //Put content of every text file into the line array. Every element has 4 elements for 4 lines.
            files.forEach(f -> {
                List<String> lines = new ArrayList<>();
                try(Stream<String> line = Files.lines(f)){
                    line.forEach(lines::add);
                } catch(IOException ioe){
                    ioe.printStackTrace();
                }

                //For every text file which has 4 lines, do operations inside map.
                Stream.of(lines).map(s -> {
                    //Splite the first line by spaces
                    //info0 can have as many name as possible, so by starting from the end, related attributes are taken.
                    //From index 0 to some index, I added those elements to the name array.
                    List<String> info0 = Arrays.asList(s.get(0).split(space));
                    String no = info0.get(info0.size()-1);
                    String surname = info0.get(info0.size()-2);
                    List<String> temp = info0.subList(0, info0.size()-2);
                    String[] names = new String[temp.size()];
                    names = temp.toArray(names);

                    //Other 3 lines are taken
                    List<String> info1 = Arrays.asList(s.get(1).split(space));
                    String info2 = s.get(2);
                    String info3 = s.get(3);

                    //Calculate the grade by choosing True answers.
                    double questionCount = info3.length();
                    double correct = (int) info3.chars()
                            .filter(c -> c == 'T')
                            .count()*(100/questionCount);
                    //Create the course with parameters obtained above.
                    Course course = new Course(Integer.parseInt(info1.get(1)), Integer.parseInt(info1.get(0)),
                            info2, Integer.parseInt(info1.get(2)), correct);

                    //Create the student with parameters obtained above.
                    Student student = new Student(names, surname, Integer.parseInt(no));
                    //Add that course to student courses
                    student.getTakenCourses().add(course);
                    return student;
                }).forEach(studentList::add); //Add each student to the list, by doing this, for 1 course and 1 student,
                                              //this list has 3 elements for MT1, MT2 and final. I handled this on the other questions.

            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public double getGrade(int studentID, int courseCode, int year){
        // TODO

        double average = 0;

        Student [] tmp = new Student[studentList.size()];
        tmp = studentList.toArray(tmp); //tmp array holds the studentList, I actually do not need this but I did not want to use studentList directly.
        List<Course> courses = Stream.of(tmp)
                .filter(t -> t.getStudentID() == studentID) // ID match
                .map(Student::getTakenCourses)// get all courses
                .collect(Collectors.toList())//make list
                .stream().flatMap(Collection::stream)//make new stream, because I could not combined the two parts above and below this line
                .filter(c -> c.getCourseCode() == courseCode)// courseCode match
                .filter(c -> c.getYear() == year) //year match
                .collect(Collectors.toList());

        //Calculate grade with respect to the exam type.
        if(courses.get(0).getExamType().equals("Final")) average += courses.get(0).getGrade()*0.5;
        else average += courses.get(0).getGrade()*0.25;
        if(courses.get(1).getExamType().equals("Final")) average += courses.get(1).getGrade()*0.5;
        else average += courses.get(1).getGrade()*0.25;
        if(courses.get(2).getExamType().equals("Final")) average += courses.get(2).getGrade()*0.5;
        else average += courses.get(2).getGrade()*0.25;

        return average;
    }

    public void updateExam(int studentID, int courseCode, String examType, double newGrade){
        // TODO
        Student [] tmp = new Student[studentList.size()];
        tmp = studentList.toArray(tmp);// Same tmp as the previous method

        Optional<Course> course = Stream.of(tmp).filter(t -> t.getStudentID() == studentID)//ID match
                .map(Student::getTakenCourses)//get all courses
                .collect(Collectors.toList())
                .stream().flatMap(Collection::stream)//new stream
                .filter(c -> c.getCourseCode() == courseCode) //courseCode match
                .filter(c -> c.getExamType().equals(examType)) //examType match
                .max(Comparator.comparing(Course::getYear)); // get the most recent course by year

        //set the obtained course's grade with newGrade
        course.get().setGrade(newGrade);

    }

    public void createTranscript(int studentID){
        // TODO

        Student [] tmp = new Student[studentList.size()];
        tmp = studentList.toArray(tmp); //same tmp
        List<Double> total = new ArrayList<>(); // total credit*coefficient list
        List<Integer> credits = new ArrayList<>(); //total credit list

        //Get all courses of the student with input ID, so if s/he takes 7 courses, there is 21 elements in this list
        //This list is for printing all courses, including the ones that are taken more than 1 time.
        List<Course> courseList = Stream.of(tmp)
                .filter(t -> t.getStudentID() == studentID)
                .map(Student::getTakenCourses)
                .collect(Collectors.toList())
                .stream().flatMap(Collection::stream)
                .collect(Collectors.toList());

        //Get most recent courses by mapping with courseCode and choosing the one with max year.
        //If student takes 7 courses and takes 1 one them twice, there are 6 elements.
        //This list is for calculating cgpa.
        Map<Integer, Course> recent = courseList.stream().collect(Collectors.toMap(Course::getCourseCode, Function.identity(),
                BinaryOperator.maxBy(Comparator.comparing(Course::getYear))));

        //Map to list conversion to do stream operations easily
        //getValue gives the Course variable, so this list has all unique courses which means excluding previous ones of the same course.
        List<Course> recentCourses = new ArrayList<>();
        recent.entrySet().stream().collect(Collectors.toList());
        recent.entrySet().stream().map(r->{return r.getValue();}).forEach(recentCourses::add);

        //fill the credit list to calculate cgpa
        recentCourses.stream().map(Course::getCredit).forEach(credits::add);

        //map for printing all courses
        Map<Integer, List<Course>> years = courseList.stream().sorted(Comparator.comparingInt(Course::getCourseCode))
                .collect(Collectors.groupingBy(Course::getYear));

        //map for calculating cgpa
        Map<Integer, List<Course>> yearRecent = recentCourses.stream().sorted(Comparator.comparingInt(Course::getCourseCode))
                .collect(Collectors.groupingBy(Course::getYear));

        //As mentioned before, courseList has 21 element for 7 courses since it includes all 3 three exams.
        //This variable are for controlling the print, not to print them more than necessary times.
        //They had to be atomic so that they can be modified inside map.
        AtomicInteger k= new AtomicInteger(0);
        AtomicInteger y= new AtomicInteger(0);
        AtomicBoolean flag = new AtomicBoolean(true);

        //sorted years from past to present
        years.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(g -> g.getValue().stream()
                .map(c -> {
                    //Obtain necessary variables and calculate letter by using getGrade() method.
                    int courseCode = c.getCourseCode();
                    k.getAndIncrement();
                    int year = c.getYear();
                    int credit = c.getCredit();
                    double grade = getGrade(studentID, courseCode, year);
                    String letter;
                    if(grade >= 0 && grade < 49.5) letter = "FF";
                    else if(grade >= 49.5 && grade < 59.5) letter = "FD";
                    else if(grade >= 59.5 && grade < 64.5) letter = "DD";
                    else if(grade >= 64.5 && grade < 69.5) letter = "DC";
                    else if(grade >= 69.5 && grade < 74.5) letter = "CC";
                    else if(grade >= 74.5 && grade < 79.5) letter = "CB";
                    else if(grade >= 79.5 && grade < 84.5) letter = "BB";
                    else if(grade >= 84.5 && grade < 89.5) letter = "BA";
                    else letter = "AA";

                    //print every course and year one time, flags and variables handle this.
                    //If year has changed, print.
                    int k1 = k.get();
                    flag.set(y.get() == year);
                    y.set(year);
                    int y1 = y.get();

                    //Every year is printed once as supposed to.
                    if(!flag.get()){
                        System.out.println(year);
                        flag.set(false);
                    }

                    //Print every course once.
                    if(k1%3==0) System.out.println(courseCode + space + letter);

                    return 0;
                }).forEach(total::remove));//When I removed this forEach, print operation did not work. So I made a meaningless remove operation
                                           //on an empty list so that the print works.

        //sorted by courseCode
        yearRecent.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(g -> g.getValue().stream()
                .map(c -> {
                    //Calculate coefficients by grades and return coefficient*credit to calculate cgpa.
                    int courseCode = c.getCourseCode();
                    int year = c.getYear();
                    int credit = c.getCredit();
                    double grade = getGrade(studentID, courseCode, year), coefficient;
                    if(grade >= 0 && grade < 49.5) coefficient = 0;
                    else if(grade >= 49.5 && grade < 59.5) coefficient = 0.5;
                    else if(grade >= 59.5 && grade < 64.5) coefficient = 1;
                    else if(grade >= 64.5 && grade < 69.5) coefficient = 1.5;
                    else if(grade >= 69.5 && grade < 74.5) coefficient = 2;
                    else if(grade >= 74.5 && grade < 79.5) coefficient = 2.5;
                    else if(grade >= 79.5 && grade < 84.5) coefficient = 3;
                    else if(grade >= 84.5 && grade < 89.5) coefficient = 3.5;
                    else coefficient = 4;

                    return coefficient*credit;
                }).forEach(total::add));//Fill the total list for every course that will contribute to cgpa.


        //Summation of these two lists for credits and coefficient*credit
        Double sumCoef = total.stream()
                .reduce(0.0, Double::sum);

        Integer sumCredits = credits.stream()
                .reduce(0, Integer::sum);

        //print with 2 digit precision.
        float cgpa = (float) (sumCoef/sumCredits);
        double rounded = Math.round(cgpa*100.0)/100.0;
        System.out.println("CGPA:" + space + rounded);

    }

    public void findCourse(int courseCode){
        // TODO
        Student [] tmp = new Student[studentList.size()];
        tmp = studentList.toArray(tmp);
        List<Integer> courses = new ArrayList<>();

        //Fill the courses list with years of every course. This list contains only years.
        Stream.of(tmp).forEach(s -> s.getTakenCourses().stream()
                .filter(c -> c.getCourseCode() == courseCode)
                .forEach(c -> courses.add(c.getYear())));

        //Group by every year's count
        Map<Integer, Long> years = courses.stream()
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        //Key is the year, value is the count. Since my studentList includes 3 elements for a course(MT1, MT2, final), I divided the value by 3.
        years.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(c -> {System.out.println(c.getKey() + space + c.getValue()/3);});

    }

    public void createHistogram(int courseCode, int year){
        // TODO
        Student [] tmp = new Student[studentList.size()];
        tmp = studentList.toArray(tmp);
        List<Double> courses = new ArrayList<>();

        //get every grade belongs to that course by year and courseCode, this includes same element for 3 times because of my studentList
        Stream.of(tmp).forEach(s -> s.getTakenCourses().stream()
                .filter(c -> c.getCourseCode() == courseCode)
                .filter(c -> c.getYear() == year)
                .forEach(c -> courses.add(getGrade(s.getStudentID(), c.getCourseCode(), c.getYear()))));

        //Return the related interval with given grade.
        Function<Double, String> grades = g -> {
            if(g >= 1 && g < 10)
                return "1-10";
            else if(g >= 10 && g < 20)
                return "10-20";
            else if(g >= 20 && g < 30)
                return "20-30";
            else if(g >= 30 && g < 40)
                return "30-40";
            else if(g >= 40 && g < 50)
                return "40-50";
            else if(g >= 50 && g < 60)
                return "50-60";
            else if(g >= 60 && g < 70)
                return "60-70";
            else if(g >= 70 && g < 80)
                return "70-80";
            else if(g >= 80 && g < 90)
                return "80-90";
            else return "90-100";
        };

        //Group those grades by every interval defined above, and count them.
        Map<String, Long> histogram = courses.stream()
                .collect(Collectors.groupingBy(grades, Collectors.counting()));

        //Check for every interval, if there is a grade on that interval, print the count, else print 0 for that interval.
        //I needed to divide the value by 3 since tmp list has the same element for 3 times.
        if(histogram.containsKey("0-10")) System.out.println("0-10"+ space + histogram.get("0-10")/3);
        else System.out.println("0-10 0");
        if(histogram.containsKey("10-20")) System.out.println("10-20"+ space + histogram.get("10-20")/3);
        else System.out.println("10-20 0");
        if(histogram.containsKey("20-30")) System.out.println("20-30"+ space + histogram.get("20-30")/3);
        else System.out.println("20-30 0");
        if(histogram.containsKey("30-40")) System.out.println("30-40"+ space + histogram.get("30-40")/3);
        else System.out.println("30-40 0");
        if(histogram.containsKey("40-50")) System.out.println("40-50"+ space + histogram.get("40-50")/3);
        else System.out.println("40-50 0");
        if(histogram.containsKey("50-60")) System.out.println("50-60"+ space + histogram.get("50-60")/3);
        else System.out.println("50-60 0");
        if(histogram.containsKey("60-70")) System.out.println("60-70"+ space + histogram.get("60-70")/3);
        else System.out.println("60-70 0");
        if(histogram.containsKey("70-80")) System.out.println("70-80"+ space + histogram.get("70-80")/3);
        else System.out.println("70-80 0");
        if(histogram.containsKey("80-90")) System.out.println("80-90"+ space + histogram.get("80-90")/3);
        else System.out.println("80-90 0");
        if(histogram.containsKey("90-100")) System.out.println("90-100"+ space + histogram.get("90-100")/3);
        else System.out.println("90-100 0");


    }
}