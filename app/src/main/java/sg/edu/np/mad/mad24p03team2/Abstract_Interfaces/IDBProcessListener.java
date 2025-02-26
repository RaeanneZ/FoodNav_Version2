package sg.edu.np.mad.mad24p03team2.Abstract_Interfaces;

import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;

/**
 * IDBProcessListener
 * For all classes to implement so they can register with the Database-Controller class for
 * response after sending CRUD operations request
 */
public interface IDBProcessListener {
    // Function to be implemented
    public void afterProcess(Boolean isValidUser, Boolean isValidPwd);
    public void afterProcess(Boolean executeStatus, Class<? extends AsyncTaskExecutorService> returnClass);

    public void afterProcess(Boolean executeStatus, String msg, Class<? extends AsyncTaskExecutorService> returnClass);
}
