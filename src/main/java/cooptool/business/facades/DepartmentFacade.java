package cooptool.business.facades;

import cooptool.models.daos.DepartmentDAO;
import cooptool.models.objects.Department;

import java.util.List;

public class DepartmentFacade {

    private static DepartmentFacade departmentFacade = null;

    private final DepartmentDAO departmentDAO = DepartmentDAO.getInstance();

    public static DepartmentFacade getInstance(){
        if(departmentFacade == null){
            departmentFacade = new DepartmentFacade();
        }
        return departmentFacade;
    }

    public Department getDepartment(){
        return null;
    }

    public List<Department> getAllDepartments() {
        return departmentDAO.getAllDepartments();
    }
}