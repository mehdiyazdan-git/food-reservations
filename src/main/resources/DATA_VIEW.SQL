CREATE VIEW EMPLOYEES_DATA_VIEW AS
    SELECT
    emp.EmployeeCode AS 'کد کارمندی',
    emp.EmployeeFirstName AS 'نام کارمند',
    emp.EmployeeLastName AS 'نام خانوادگی کارمند',
    emp.EmployeeLastName_EN AS 'نام خانوادگی کارمند (انگلیسی)',
    p.FatherName AS 'نام پدر',
    empm.EmployeeNationalID AS 'کد ملی کارمند',
    empm.ManagerNationalID AS 'کد ملی مدیر بالادستی',
    FORMAT(P.BirthDate, 'yyyy/MM/dd', 'fa-IR') AS 'تاریخ تولد',
    rd.Name AS 'محل تولد',
    p.IDNumber AS 'شماره شناسنامه',
    FORMAT(P.IssuanceDate, 'yyyy/MM/dd', 'fa-IR') AS 'تاریخ صدور',
    CASE
        WHEN p.Gender = 1 THEN 'مرد'
        WHEN p.Gender = 2 THEN 'زن'
        ELSE 'نامشخص'
        END AS 'جنسیت',
        CASE
            WHEN p.MaritalStatus = 1 THEN 'مجرد'
            WHEN p.MaritalStatus = 2 THEN 'متاهل'
            ELSE 'نامشخص'
            END
            AS 'وضعیت تاهل',
        emp.EmployeeMobile AS 'شماره موبایل',
        emp.EmployeeEmail AS 'ایمیل کارمند',
        po.Title AS 'عنوان پست',
        ls.EMPTYPE AS 'نوع استخدام',
        ls.WORKLOCCODE AS 'کد محل خدمت',
        ls.WORKLOC AS 'محل خدمت',
        ls.JOB AS ,	empmp.EmployeeDepartmentCode AS 'کد بخش',
    empmp.EmployeeDepartment As 'بخش',
    empm.EmployeeUsername As 'نام کاربری'
    FROM       HCM3.Employee_OUs emp
    LEFT JOIN       Hcm3.Employee_Manager_OUs empm ON emp.EmployeeCode = empm.EmployeeCode
    LEFT JOIN	[OM_Sg3].[HCM3].[Employee_Manager_Posts] empmp on empmp.EmployeeID = emp.EmployeeID
    LEFT JOIN       GNR3.Party p ON emp.EmployeePartyID = p.PartyID
    LEFT JOIN       GNR3.RegionalDivision rd ON p.BirthPlaceRef = rd.RegionalDivisionID
    LEFT JOIN       HCM3.Post po ON emp.EmployeePostRef = po.PostID
    LEFT JOIN       HCM3.Department de ON emp.EmployeeDepartmentRef = de.DepartmentID
    LEFT JOIN       HCM3.vwGetLastEmpStatute ls ON ls.Code = emp.EmployeeCode;
