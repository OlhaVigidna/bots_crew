import models.Degree;
import models.Department;
import models.Lector;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Helper {
    private Session session;
    private Scanner scanner = new Scanner(System.in);
    private Scanner scannerForInt = new Scanner(System.in);

    private String firstOption = "1 - show a head of the department";
    private String secondOption = "2 - show department statistic";
    private String thirdOption = "3 - show the average salary for department";
    private String fourthOption = "4 - show count of employee for department";
    private String fifthOption = "5 - global search by";

    public Helper(Session session) {
        this.session = session;
    }

    public void menu() {
        session.beginTransaction();
        System.out.println("Choose an action:");
        System.out.println(firstOption + "\n" + secondOption + "\n" + thirdOption + "\n" + fourthOption + "\n" + fifthOption);
        int firstChoose = scannerForInt.nextInt();

        switch (firstChoose) {
            case 1: {
                showHeadOfDepartment();
                break;
            }
            case 2: {
                showStatisticOfDepartment();
                break;
            }
            case 3: {
                showAverageSalaryForDepartment();
                break;
            }
            case 4: {
                showCountOfEmployeeForDepartment();
                break;
            }
            case 5: {
                globalSearchBy();
                break;
            }
        }
        session.getTransaction().commit();

        System.out.println("Do you want to continue? /n 1 - yes /n 2 - noy");
        int secondChoose = scannerForInt.nextInt();
        switch (secondChoose) {
            case 1: {
                menu();
                break;
            }
        }

    }

    private void showHeadOfDepartment() {
        String departName = readNameFromConsole();
        Department department = findDepartment(departName);
        printTheHeadOfDepartment(department, departName);

    }

    private void showStatisticOfDepartment() {
        String departName = readNameFromConsole();
        Department department = findDepartment(departName);
        List<Lector> lectors = department.getLectors();
        printStatistic(lectors);
    }

    private void showAverageSalaryForDepartment() {
        String departmentName = readNameFromConsole();
        Department department = findDepartment(departmentName);
        List<Lector> lectors = department.getLectors();
        int averageSalary = salaryAverage(lectors);
        System.out.println("The average salary of " + departmentName + " is " + averageSalary);

    }

    private void showCountOfEmployeeForDepartment() {
        String departmentName = readNameFromConsole();
        Department department = findDepartment(departmentName);
        int size = department.getLectors().size();
        System.out.println(size);
    }

    private int salaryAverage(List<Lector> lectors) {
        int salarySum = 0;
        for (Lector lector : lectors) {
            salarySum = salarySum + lector.getLectorSalary();
        }
        return salarySum / lectors.size();
    }

    private void printStatistic(List<Lector> lectors) {
        List<Object> assistants = lectors.stream().filter(l -> l.getLectorDegree().equals(Degree.ASSISTANT)).collect(Collectors.toList());
        List<Object> professors = lectors.stream().filter(l -> l.getLectorDegree().equals(Degree.PROFESSOR)).collect(Collectors.toList());
        List<Object> associateProfessors = lectors.stream().filter(l -> l.getLectorDegree().equals(Degree.ASSOCIATE_PROFESSOR)).collect(Collectors.toList());
        System.out.println("Assistants - " + assistants.size() + "\nProfessors - " + professors.size() +
                "\nAssociate professors - " + associateProfessors.size());
    }

    private void globalSearchBy() {
        System.out.println("enter pattern");
        String pattern = scanner.nextLine();
        List<Object[]> result = session.createSQLQuery("SELECT * FROM notes_app.lector d WHERE d.lectorName  OR  " +
                "d.lectorSurname LIKE '%" + pattern + "%'").getResultList();
        for (Object[] obj : result) {
            System.out.println(obj[2] + " " + obj[4]);
        }
    }

    private Department findDepartment(String departName) {
        Department result = null;

        Query<Department> query = session.createQuery("SELECT d FROM Department d WHERE d.departmentName=:x", Department.class);
        query.setParameter("x", departName);
        try {
            result = query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("department with this name doesn't exist");
        }

        return result;
    }

    private void printTheHeadOfDepartment(Department department, String departmentName) {

        Lector departmentHead = department.getHeadOfDepartment();

        if (departmentHead != null) {
            System.out.println("Head of " + departmentName + " department is " + departmentHead.getLectorName() +
                    " " + departmentHead.getLectorSurname());
        } else System.out.println("this department doesn't have a head");

    }

    private String readNameFromConsole() {
        System.out.println("Enter name of department");
        return scanner.nextLine();
    }

}

