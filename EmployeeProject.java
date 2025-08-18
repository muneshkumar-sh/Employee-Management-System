//array list
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
//delete emplyee from list
import java.util.Iterator;

//java libraries for GUI
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//for database connectivity
import java.sql.*;


//class for database connectivity
class DatabaseConnection {
    private static final String url = "jdbc:mysql://localhost:3306/employee_db";
    private static final String user = "root";
    private static final String password = "lifeisgood";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static void initializeConnection() throws SQLException {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS employees (" +
                    "id VARCHAR(20) PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "department VARCHAR(50) NOT NULL, " +
                    "status VARCHAR(50) NOT NULL, " +
                    "salary VARCHAR(20) NOT NULL, " +
                    "type VARCHAR(20) NOT NULL)";
            stmt.execute(sql);
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }
}
//employee class
class Emp{
    String id, name, department, status, salary, type;

    Emp(String id, String name, String department, String status, String salary, String type) {
        this.id=id;
        this.name=name;
        this.department=department;
        this.status=status;
        this.salary=salary;
        this.type=type;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Dept: " + department +
                ", Status: " + status + ", Salary: " + salary + ", Type: " + type;
    }
}

//Employee class 
class Employee implements ActionListener{
    ArrayList<Emp> employeeList=new ArrayList<>();
    JFrame jframe;
    JFrame addFrame;
    JButton displayEmployee, addEmployee, searchEmployee, editEmployee, deleteEmployee, Exit;
    Container container;
    JLabel label1;
    JTextField myTextField;
    JPanel panel;
    JButton okButton, AddButton;
    JDialog dialog;
    JLabel labelID, labelName, labelDep, labelStatus, labelSalary;
    JTextField myTextFieldID, myTextFieldName, myTextFieldDep, myTextFieldStatus, myTextFieldSlary;
    JComboBox<String> type;

    //fonts
    Font Buttonfont=new Font("Arial", Font.BOLD, 18);
    Font labelfont=new Font("Segoe UI", Font.BOLD, 22);

    //colors
    Color primaryColor=new Color(44, 62, 80);
    Color buttonColor=new Color(52, 152, 219);
    Color buttonTextColor=Color.WHITE;
    Color backgroundColor=new Color(236, 240, 241);


    // Load employees from database
    void loadEmployeesFromDB() {
        employeeList.clear();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM employees";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Emp emp = new Emp(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("status"),
                        rs.getString("salary"),
                        rs.getString("type")
                );
                employeeList.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(jframe, "Error loading employees: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //method for gui setup
    void GUI() throws SQLException {
        // Initialize database and load employees
        try {
            DatabaseConnection.initializeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        loadEmployeesFromDB();

        //create JFrame and container
        jframe = new JFrame("Employee Management System");
        Container container=jframe.getContentPane();
        //set background color
        container.setBackground(backgroundColor);

        //Create buttons and lalbe
        displayEmployee=createStyledButton("Display Employees");
        addEmployee=createStyledButton("Add Employees");
        searchEmployee=createStyledButton("Search Employees");
        editEmployee=createStyledButton("Edit Employees");
        deleteEmployee=createStyledButton("Delete Employees");
        Exit=createStyledButton("Exit");

        label1=new JLabel("Welcome to Employee Management System", JLabel.CENTER);
        label1.setFont(labelfont);
        label1.setForeground(primaryColor);


        //Add ActionListeners
        displayEmployee.addActionListener(this);
        addEmployee.addActionListener(this);
        searchEmployee.addActionListener(this);
        editEmployee.addActionListener(this);
        deleteEmployee.addActionListener(this);
        Exit.addActionListener(this);

        //to apply pandding
        JPanel addpanding=new JPanel(new BorderLayout(40,40));
        addpanding.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        addpanding.setBackground(backgroundColor);

        //Grid panel with buttons
        panel=new JPanel(new GridLayout(3, 2, 15, 15));
        panel.setBackground(backgroundColor);

        //add component into panel
        panel.add(displayEmployee);
        panel.add(addEmployee);
        panel.add(searchEmployee);
        panel.add(editEmployee);
        panel.add(deleteEmployee);
        panel.add(Exit);

        //add components
        addpanding.add(label1, BorderLayout.NORTH);
        addpanding.add(panel, BorderLayout.CENTER);

        //set layout
        container.setLayout(new BorderLayout(40,40));
        container.add(addpanding);

        //set size
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(800, 550);
        jframe.setLocationRelativeTo(null); //Center on screen
        jframe.setVisible(true);

    }

    //method to create styled buttons
    private JButton createStyledButton(String text){
        JButton button=new JButton(text);
        button.setBackground(buttonColor);
        button.setForeground(buttonTextColor);
        button.setFont(Buttonfont);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    //to display detail
    void showEmployeeTable(String selectedType){
        //Column names
        String[] columnNames = {"ID", "Name", "Department", "Status", "Salary", "Type"};
    
        //Filter employeeList based on type
        ArrayList<Emp>filteredList=new ArrayList<>();
        for(Emp emp : employeeList){
            if(emp.type.equals(selectedType)){
                filteredList.add(emp);
            }
        }
    
        if(filteredList.isEmpty()){
            JOptionPane.showMessageDialog(jframe, "No employees found for type: " + selectedType);
            return;
        }
    
        //Create table data
        String[][] data=new String[filteredList.size()][6];
        for(int i=0; i<filteredList.size(); i++){
            Emp emp=filteredList.get(i);
            data[i][0]=emp.id;
            data[i][1]=emp.name;
            data[i][2]=emp.department;
            data[i][3]=emp.status;
            data[i][4]=emp.salary;
            data[i][5]=emp.type;
        }
    
        //Create JTable
        JTable table=new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
    
        //Create a frame to hold the table
        JFrame tableFrame=new JFrame("Employee List - " + selectedType);
        tableFrame.setSize(800, 400);
        tableFrame.setLocationRelativeTo(null);
        tableFrame.add(scrollPane);
        tableFrame.setVisible(true);
    
    }

    //add employee form
    void form() {
        addFrame=new JFrame("Add Employee");
        Container container=addFrame.getContentPane();
        container.setBackground(new Color(236, 240, 241));

        //Fonts & Colors
        Font titleFont=new Font("Arial", Font.BOLD, 24);
        Font labelFont=new Font("Segoe UI", Font.BOLD, 16);
        Font fieldFont=new Font("Segoe UI", Font.BOLD, 18); // Bold input text
        Color primaryColor=new Color(44, 62, 80);
        Color buttonColor=new Color(52, 152, 219);
        Color backButtonColor=new Color(192, 57, 43); // Red color for back button

        //Title Label
        JLabel titleLabel=new JLabel("ADD EMPLOYEE FORM", JLabel.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        //labels
        labelID=new JLabel("ID: ");
        labelName=new JLabel("Name: ");
        labelDep=new JLabel("Department: ");
        labelStatus=new JLabel("Status: ");
        labelSalary=new JLabel("Salary: ");

        //textFields
        myTextFieldID=new JTextField(20);
        myTextFieldName=new JTextField(20);
        myTextFieldDep=new JTextField(20);
        myTextFieldStatus=new JTextField(20);
        myTextFieldSlary=new JTextField(20);

        //Apply font and style
        JLabel[] labels={labelID, labelName, labelDep, labelStatus, labelSalary};
        JTextField[] fields={myTextFieldID, myTextFieldName, myTextFieldDep, myTextFieldStatus, myTextFieldSlary};

        for (JLabel label : labels) {
            label.setFont(labelFont);
            label.setForeground(primaryColor);
        }
        for (JTextField field : fields) {
            field.setFont(fieldFont);
            field.setBackground(Color.WHITE);
            field.setForeground(Color.BLACK);
            field.setBorder(BorderFactory.createLineBorder(primaryColor, 2));
        }

        //buttons
        AddButton=new JButton("Add");
        AddButton.setFont(new Font("Arial", Font.BOLD, 18));
        AddButton.setBackground(buttonColor);
        AddButton.setForeground(Color.WHITE);
        AddButton.setFocusPainted(false);
        AddButton.setPreferredSize(new Dimension(150, 40));
        AddButton.setBorder(BorderFactory.createLineBorder(primaryColor, 2));

        JButton backButton=new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.setBackground(new Color(192, 57, 43)); // Red color
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.setBorder(BorderFactory.createLineBorder(primaryColor, 2));

        // Create a panel for the buttons
        JPanel buttonPanel=new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setBackground(new Color(236, 240, 241));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50));
        buttonPanel.add(AddButton);
        buttonPanel.add(backButton);

        // Grid panel for form fields
        panel=new JPanel(new GridLayout(5, 2, 15, 15));
        panel.setBackground(new Color(236, 240, 241));

        //add in panel
        panel.add(labelID);
        panel.add(myTextFieldID);
        panel.add(labelName);
        panel.add(myTextFieldName);
        panel.add(labelDep);
        panel.add(myTextFieldDep);
        panel.add(labelStatus);
        panel.add(myTextFieldStatus);
        panel.add(labelSalary);
        panel.add(myTextFieldSlary);

        JPanel addPadding=new JPanel(new BorderLayout(20, 20));
        addPadding.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        addPadding.setBackground(new Color(236, 240, 241));

        // Add components to frame
        addPadding.add(titleLabel, BorderLayout.NORTH);
        addPadding.add(panel, BorderLayout.CENTER);
        addPadding.add(buttonPanel, BorderLayout.SOUTH);

        //set layout
        container.setLayout(new BorderLayout(10, 10));
        //add components in container
        container.add(addPadding);

        //set size
        addFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addFrame.setSize(550, 500);
        addFrame.setLocationRelativeTo(null); // Center the window
        addFrame.setVisible(true);
        //jframe.setVisible(false); // Hide main menu

        //add button action and add employee to list
        AddButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id=myTextFieldID.getText();
                String name=myTextFieldName.getText();
                String dep=myTextFieldDep.getText();
                String status=myTextFieldStatus.getText();
                String salary=myTextFieldSlary.getText();
                String selectedType=(String) type.getSelectedItem();

                if (id.isEmpty() || name.isEmpty() || dep.isEmpty() || status.isEmpty() || salary.isEmpty()) {
                    JOptionPane.showMessageDialog(addFrame, "Please fill all fields.");
                    return;
                }

                try (Connection conn=DatabaseConnection.getConnection()) {
                    String sql="INSERT INTO employees (id, name, department, status, salary, type) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmt=conn.prepareStatement(sql);
                    pstmt.setString(1, id);
                    pstmt.setString(2, name);
                    pstmt.setString(3, dep);
                    pstmt.setString(4, status);
                    pstmt.setString(5, salary);
                    pstmt.setString(6, selectedType);
                    pstmt.executeUpdate();

                    // Add to list
                    Emp emp=new Emp(id, name, dep, status, salary, selectedType);
                    employeeList.add(emp);

                    JOptionPane.showMessageDialog(addFrame, "Employee Added Successfully!");
                    addFrame.dispose();
                    jframe.setVisible(true); // Show main menu after adding
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(addFrame, "Error saving employee: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Back button action
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addFrame.dispose(); // Close the add form
                jframe.setVisible(true); // Show the main menu
            }
        });
    }

    void openEditForm(Emp emp) {
        JFrame editDetailsFrame=new JFrame("Edit Employee Details");
        editDetailsFrame.setSize(550, 650); // Increased height to accommodate extra field and buttons
        editDetailsFrame.setLocationRelativeTo(null);
        editDetailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Container container=editDetailsFrame.getContentPane();
        container.setBackground(new Color(236, 240, 241));

        //Fonts & Colors
        Font titleFont=new Font("Arial", Font.BOLD, 24);
        Font labelFont=new Font("Segoe UI", Font.BOLD, 16);
        Font fieldFont=new Font("Segoe UI", Font.BOLD, 18);
        Color primaryColor=new Color(44, 62, 80);

        //Title Label
        JLabel titleLabel=new JLabel("EDIT EMPLOYEE DETAIL FORM", JLabel.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        //Form fields
        JLabel idLabel=new JLabel("ID:");
        JTextField idField=new JTextField(emp.id);
        JLabel nameLabel=new JLabel("Name:");
        JTextField nameField=new JTextField(emp.name);
        JLabel departmentLabel=new JLabel("Department:");
        JTextField departmentField=new JTextField(emp.department);
        JLabel statusLabel=new JLabel("Status:");
        JTextField statusField=new JTextField(emp.status);
        JLabel salaryLabel=new JLabel("Salary:");
        JTextField salaryField=new JTextField(emp.salary);
        JLabel typeLabel=new JLabel("Type:");
        JComboBox<String> typeField = new JComboBox<>(new String[]{"Manager", "SalesMan", "Developer"});
        typeField.setSelectedItem(emp.type);

        //Apply font and style
        JLabel[] labels={idLabel, nameLabel, departmentLabel, statusLabel, salaryLabel, typeLabel};
        JComponent[] fields={idField, nameField, departmentField, statusField, salaryField, typeField};

        for (JLabel label : labels) {
            label.setFont(labelFont);
            label.setForeground(primaryColor);
        }
        for (Component field : fields) {
            if (field instanceof JTextField) {
                ((JTextField) field).setFont(fieldFont);
                field.setBackground(Color.WHITE);
                field.setForeground(Color.BLACK);
                ((JTextField) field).setBorder(BorderFactory.createLineBorder(primaryColor, 2));
            } else if (field instanceof JComboBox) {
                ((JComboBox<?>) field).setFont(fieldFont);
                field.setBackground(Color.WHITE);
                field.setForeground(Color.BLACK);
            }
        }

        //Buttons
        JButton saveButton = new JButton("Save Changes");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveButton.setBackground(new Color(39, 174, 96)); // Green
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(180, 40));

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backButton.setBackground(new Color(192, 57, 43)); // Red
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(180, 40));

        //Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        //Grid panel
        panel = new JPanel(new GridLayout(6, 2, 15, 15));
        panel.setBackground(new Color(236, 240, 241));

        //Add fields to panel
        panel.add(idLabel);
        panel.add(idField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(departmentLabel);
        panel.add(departmentField);
        panel.add(statusLabel);
        panel.add(statusField);
        panel.add(salaryLabel);
        panel.add(salaryField);
        panel.add(typeLabel);
        panel.add(typeField);

        JPanel contentPanel=new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(236, 240, 241));
        contentPanel.add(panel, BorderLayout.CENTER);

        JScrollPane scrollPane=new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Main container layout
        JPanel mainPanel=new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        container.add(mainPanel);

        // Save action
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame confirmFrame=new JFrame("Confirm Save");
                confirmFrame.setSize(400, 200);
                confirmFrame.setLocationRelativeTo(editDetailsFrame);
                confirmFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                //confirmFrame.setModal(true);

                Container container=confirmFrame.getContentPane();
                container.setLayout(new BorderLayout());
                container.setBackground(new Color(236, 240, 241));

                // Title label
                JLabel confirmLabel=new JLabel("Are you sure you want to save changes?", JLabel.CENTER);
                confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                confirmLabel.setForeground(new Color(44, 62, 80));
                confirmLabel.setBorder(BorderFactory.createEmptyBorder(30, 10, 10, 10));
                container.add(confirmLabel, BorderLayout.NORTH);

                // Button panel
                JPanel btnPanel=new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
                btnPanel.setBackground(new Color(236, 240, 241));

                JButton yesBtn=new JButton("Yes, Save");
                yesBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                yesBtn.setBackground(new Color(39, 174, 96)); // green
                yesBtn.setForeground(Color.WHITE);
                yesBtn.setFocusPainted(false);

                JButton noBtn=new JButton("No, Cancel");
                noBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                noBtn.setBackground(new Color(192, 57, 43)); // red
                noBtn.setForeground(Color.WHITE);
                noBtn.setFocusPainted(false);

                btnPanel.add(yesBtn);
                btnPanel.add(noBtn);
                container.add(btnPanel, BorderLayout.CENTER);

                // Button actions
                yesBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        String newId=idField.getText().trim();
                        String newName=nameField.getText().trim();
                        String newDepartment=departmentField.getText().trim();
                        String newStatus=statusField.getText().trim();
                        String newSalary=salaryField.getText().trim();
                        String newType=(String) typeField.getSelectedItem();

                        try (Connection conn=DatabaseConnection.getConnection()) {
                            String sql="UPDATE employees SET id=?, name=?, department=?, status=?, salary=?, type=? WHERE id=?";
                            PreparedStatement pstmt=conn.prepareStatement(sql);
                            pstmt.setString(1, newId);
                            pstmt.setString(2, newName);
                            pstmt.setString(3, newDepartment);
                            pstmt.setString(4, newStatus);
                            pstmt.setString(5, newSalary);
                            pstmt.setString(6, newType);
                            pstmt.setString(7, emp.id);
                            pstmt.executeUpdate();

                            // Update the employee object
                            emp.id=newId;
                            emp.name=newName;
                            emp.department=newDepartment;
                            emp.status=newStatus;
                            emp.salary=newSalary;
                            emp.type=newType;

                            JOptionPane.showMessageDialog(editDetailsFrame, "Employee details updated successfully!",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                            confirmFrame.dispose();
                            editDetailsFrame.dispose();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(editDetailsFrame, "Error updating employee: " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

                noBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        confirmFrame.dispose();
                    }
                });

                confirmFrame.setVisible(true);
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editDetailsFrame.dispose();
                jframe.setVisible(true);
            }
        });

        editDetailsFrame.setVisible(true);
        //jframe.setVisible(false); // Hide the main menu while editing

    }

    // Method to show employee details while searching
    private void showEmployeeDetailsDialog(Emp emp, JFrame parentFrame) {
        JDialog detailsDialog=new JDialog(parentFrame, "Employee Details", true);
        detailsDialog.setSize(450, 400);
        detailsDialog.setLocationRelativeTo(parentFrame);
        detailsDialog.getContentPane().setBackground(new Color(245, 245, 245));
        detailsDialog.setLayout(new BorderLayout(10, 10));

        //Header Panel
        JPanel headerPanel=new JPanel();
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel headerLabel=new JLabel("EMPLOYEE DETAILS", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        //Details Panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        detailsPanel.setBackground(Color.WHITE);

        //employee details
        addStyledDetail(detailsPanel, "ID:", emp.id);
        addStyledDetail(detailsPanel, "Name:", emp.name);
        addStyledDetail(detailsPanel, "Department:", emp.department);
        addStyledDetail(detailsPanel, "Status:", emp.status);
        addStyledDetail(detailsPanel, "Salary:", "$" + emp.salary);
        addStyledDetail(detailsPanel, "Type:", emp.type);

        //Button Panel
        JPanel buttonPanel=new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton closeButton=new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setBackground(new Color(52, 152, 219));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> detailsDialog.dispose());
        buttonPanel.add(closeButton);

        //Add components to dialog
        detailsDialog.add(headerPanel, BorderLayout.NORTH);
        detailsDialog.add(new JScrollPane(detailsPanel), BorderLayout.CENTER);
        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);

        detailsDialog.setVisible(true);
    }

    //method to add styled detail rows
    void addStyledDetail(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowPanel.setMaximumSize(new Dimension(400, 30));

        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelLbl.setForeground(new Color(70, 70, 70));
        labelLbl.setPreferredSize(new Dimension(120, 20));

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueLbl.setForeground(Color.BLACK);

        //Special styling for salary
        if (label.equals("Salary:")) {
            valueLbl.setForeground(new Color(0, 100, 0));
            valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        }

        rowPanel.add(labelLbl);
        rowPanel.add(valueLbl);
        panel.add(rowPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    //method for employee type
    void Employeetype(String EmpType){
        //Create a stylish dialog
        dialog=new JDialog(jframe, "Select Employee Type", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(jframe);
        dialog.getContentPane().setBackground(new Color(236, 240, 241));

        //Title label
        JLabel title=new JLabel("Select Employee Type", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(52, 73, 94));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //ComboBox to select employee
        String[] EmployeeType={"Manager", "SalesMan", "Developer"};
        type=new JComboBox<>(EmployeeType);
        type.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        type.setBackground(Color.WHITE);
        type.setForeground(Color.BLACK);
        type.setBorder(BorderFactory.createLineBorder(new Color(44, 62, 80), 1));

        //Panel for checkbox and title
        JPanel panel2=new JPanel(new GridLayout(2, 1, 10, 10));
        panel2.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        panel2.setBackground(new Color(236, 240, 241));
        panel2.add(title);
        panel2.add(type);

        //OK Button
        JButton okButton=new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 16));
        okButton.setBackground(new Color(52, 152, 219));
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        //Panel for button
        JPanel buttonPanel=new JPanel();
        buttonPanel.setBackground(new Color(236, 240, 241));
        buttonPanel.add(okButton);

        //Add panels to dialog
        dialog.setLayout(new BorderLayout());
        dialog.add(panel2, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.dispose(); // close dialog
        
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String selected=(String) type.getSelectedItem();
                dialog.dispose();
        
                if (EmpType.equals("add")){
                    form(); // Add new employee
                } 
                else if(EmpType.equals("display")){
                    showEmployeeTable(selected); //Show JTable instead of plain text
                }
                
            }
        });
        dialog.setVisible(true);
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource()==Exit){
            JFrame exitFrame=new JFrame("Exit Confirmation");
            exitFrame.setSize(400, 200);
            exitFrame.setLocationRelativeTo(jframe);
            exitFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            Container container=exitFrame.getContentPane();
            container.setLayout(new BorderLayout());
            container.setBackground(new Color(236, 240, 241));

            //Title
            JLabel exitLabel=new JLabel("Are you sure you want to exit?", JLabel.CENTER);
            exitLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            exitLabel.setForeground(new Color(192, 57, 43));
            exitLabel.setBorder(BorderFactory.createEmptyBorder(30, 10, 10, 10));
            container.add(exitLabel, BorderLayout.NORTH);

            //Buttons
            JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
            buttonPanel.setBackground(new Color(236, 240, 241));

            JButton yesBtn=new JButton("Yes, Exit");
            yesBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            yesBtn.setBackground(new Color(231, 76, 60));
            yesBtn.setForeground(Color.WHITE);
            yesBtn.setFocusPainted(false);
            yesBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JButton noBtn=new JButton("No, Stay");
            noBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            noBtn.setBackground(new Color(41, 128, 185));
            noBtn.setForeground(Color.WHITE);
            noBtn.setFocusPainted(false);
            noBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            buttonPanel.add(yesBtn);
            buttonPanel.add(noBtn);
            container.add(buttonPanel, BorderLayout.CENTER);

            //Button Actions
            yesBtn.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae){
                    System.exit(0);
                }
            });

            noBtn.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae){
                    exitFrame.dispose();
                }
            });

            exitFrame.setVisible(true);
        }

        else if(e.getSource()==displayEmployee){
            //class employee type method
            Employeetype("display");
        }

        else if(e.getSource()==addEmployee){
            //class employee type method
            Employeetype("add");
         
        }

        else if(e.getSource()==searchEmployee){
            JFrame searchFrame=new JFrame("Search Employee");
            searchFrame.setSize(500, 250);
            searchFrame.setLocationRelativeTo(jframe);
            searchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            Container searchContainer=searchFrame.getContentPane();
            searchContainer.setBackground(new Color(236, 240, 241));
            searchContainer.setLayout(new BorderLayout());

            //Title label
            JLabel titleLabel=new JLabel("Search Employee by ID", JLabel.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titleLabel.setForeground(new Color(41, 128, 185));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
            searchContainer.add(titleLabel, BorderLayout.NORTH);

            //Center panel for input
            JPanel centerPanel=new JPanel(new GridLayout(2, 1, 10, 10));
            centerPanel.setBackground(new Color(236, 240, 241));
            centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 10, 60));

            JTextField searchField=new JTextField();
            searchField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            searchField.setBackground(Color.WHITE);
            searchField.setForeground(Color.BLACK);
            searchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            centerPanel.add(searchField);

            JButton searchBtn=new JButton("Search");
            searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            searchBtn.setBackground(new Color(41, 128, 185));
            searchBtn.setForeground(Color.WHITE);
            searchBtn.setFocusPainted(false);
            searchBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
            searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            centerPanel.add(searchBtn);

            searchContainer.add(centerPanel, BorderLayout.CENTER);

            // Action
            searchBtn.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae){
                    String searchID=searchField.getText().trim();
                    boolean found = false;

                    for(Emp emp : employeeList){
                        if(emp.id.equals(searchID)){
                            // Create attractive details dialog instead of plain message
                            showEmployeeDetailsDialog(emp, searchFrame);
                            found = true;
                            break;
                        }
                    }

                    if(!found){
                        JOptionPane.showMessageDialog(searchFrame,
                                "Employee not found.",
                                "Not Found",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            searchFrame.setVisible(true);
        }

        else if(e.getSource()==editEmployee){
            JFrame editFrame=new JFrame("Edit Employee");
            editFrame.setSize(500, 250);
            editFrame.setLocationRelativeTo(jframe);
            editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
            Container searchContainer=editFrame.getContentPane();
            searchContainer.setBackground(new Color(236, 240, 241));
            searchContainer.setLayout(new BorderLayout());
        
            //Title label
            JLabel titleLabel=new JLabel("Edit Employee Detail by ID", JLabel.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titleLabel.setForeground(new Color(41, 128, 185));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
            searchContainer.add(titleLabel, BorderLayout.NORTH);
        
            //Center panel for input
            JPanel centerPanel=new JPanel(new GridLayout(2, 1, 10, 10));
            centerPanel.setBackground(new Color(236, 240, 241));
            centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 10, 60));
        
            JTextField editField=new JTextField();
            editField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            editField.setBackground(Color.WHITE);
            editField.setForeground(Color.BLACK);
            editField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            centerPanel.add(editField);
        
            JButton ediButton=new JButton("Edit");
            ediButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
            ediButton.setBackground(new Color(41, 128, 185));
            ediButton.setForeground(Color.WHITE);
            ediButton.setFocusPainted(false);
            ediButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
            ediButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            centerPanel.add(ediButton);
        
            searchContainer.add(centerPanel, BorderLayout.CENTER);

            editFrame.setVisible(true);
            
            //Action edit button
            ediButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    String editID=editField.getText().trim();
                    boolean found=false;
                    for (Emp emp : employeeList) {
                        if (emp.id.equals(editID)) {
                            JOptionPane.showMessageDialog(editField, emp.toString(), "Employee Found", JOptionPane.INFORMATION_MESSAGE);
                            //class employee type method
                            openEditForm(emp);
                            found=true;
                            break;
                        }
                    }
                    if (!found) {
                        JOptionPane.showMessageDialog(editField, "Employee not found.", "Not Found", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

        else if(e.getSource()==deleteEmployee){
            JFrame deleteFrame=new JFrame("Delete Employee");
            deleteFrame.setSize(500, 250);
            deleteFrame.setLocationRelativeTo(jframe);
            deleteFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            Container searchContainer=deleteFrame.getContentPane();
            searchContainer.setBackground(new Color(236, 240, 241));
            searchContainer.setLayout(new BorderLayout());

            //Title label
            JLabel titleLabel=new JLabel("Delete Employee Detail by ID", JLabel.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titleLabel.setForeground(new Color(41, 128, 185));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
            searchContainer.add(titleLabel, BorderLayout.NORTH);

            //Center panel for input
            JPanel centerPanel=new JPanel(new GridLayout(2, 1, 10, 10));
            centerPanel.setBackground(new Color(236, 240, 241));
            centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 10, 60));

            JTextField deletField=new JTextField();
            deletField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            deletField.setBackground(Color.WHITE);
            deletField.setForeground(Color.BLACK);
            deletField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            centerPanel.add(deletField);

            JButton deleteButton=new JButton("Delete");
            deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
            deleteButton.setBackground(new Color(41, 128, 185));
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);
            deleteButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
            deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            centerPanel.add(deleteButton);

            searchContainer.add(centerPanel, BorderLayout.CENTER);

            deleteFrame.setVisible(true);

            //Action delete button
            deleteButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    String deleteid=deletField.getText().trim();
                    boolean found=false;
                    Emp employeeToDelete = null;

                    // Find the employee first
                    for(Emp emp : employeeList){
                        if(emp.id.equals(deleteid)){
                            employeeToDelete = emp;
                            found=true;
                            break;
                        }
                    }

                    if(found){
                        //Create confirmation dialog with employee details
                        JFrame confirmFrame=new JFrame("Confirm Delete");
                        confirmFrame.setSize(870, 250); // Increased size to show details
                        confirmFrame.setLocationRelativeTo(null);
                        confirmFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        //confirmFrame.setModal(true);

                        Container container=confirmFrame.getContentPane();
                        container.setLayout(new BorderLayout());
                        container.setBackground(new Color(236, 240, 241));

                        //Employee details label
                        JTextArea detailsArea = new JTextArea(employeeToDelete.toString());
                        detailsArea.setEditable(false);
                        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
                        detailsArea.setBackground(new Color(236, 240, 241));
                        detailsArea.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                        //Title label
                        JLabel confirmLabel=new JLabel("Are you sure you want to delete this employee?", JLabel.CENTER);
                        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                        confirmLabel.setForeground(new Color(44, 62, 80));
                        confirmLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

                        //Button panel
                        JPanel btnPanel=new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
                        btnPanel.setBackground(new Color(236, 240, 241));

                        JButton yesBtn=new JButton("Confirm Delete");
                        yesBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                        yesBtn.setBackground(new Color(192, 57, 43)); // red
                        yesBtn.setForeground(Color.WHITE);
                        yesBtn.setFocusPainted(false);

                        JButton noBtn=new JButton("Cancel");
                        noBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                        noBtn.setBackground(new Color(41, 128, 185)); // blue
                        noBtn.setForeground(Color.WHITE);
                        noBtn.setFocusPainted(false);

                        btnPanel.add(noBtn);
                        btnPanel.add(yesBtn);

                        //Add components to container
                        JPanel detailsPanel=new JPanel(new BorderLayout());
                        detailsPanel.add(detailsArea, BorderLayout.CENTER);
                        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                        container.add(confirmLabel, BorderLayout.NORTH);
                        container.add(detailsPanel, BorderLayout.CENTER);
                        container.add(btnPanel, BorderLayout.SOUTH);

                        //Button actions
                        yesBtn.addActionListener(new ActionListener(){
                            public void actionPerformed(ActionEvent ae) {
                                try (Connection connection = DatabaseConnection.getConnection()) {
                                    String sql="DELETE FROM employees WHERE id=?";
                                    PreparedStatement pstmt = connection.prepareStatement(sql);
                                    pstmt.setString(1, deleteid);
                                    pstmt.executeUpdate();

                                    //Remove from list
                                    employeeList.removeIf(emp -> emp.id.equals(deleteid));

                                    JOptionPane.showMessageDialog(deleteFrame, "Employee deleted successfully.",
                                            "Deleted", JOptionPane.INFORMATION_MESSAGE);
                                    confirmFrame.dispose();
                                    deleteFrame.dispose();
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(deleteFrame, "Error deleting employee: " + ex.getMessage(),
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        });

                        noBtn.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent ae) {
                                confirmFrame.dispose();
                            }
                        });

                        confirmFrame.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(deletField, "Employee not found.", "Not Found", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            deleteFrame.setVisible(true);
        }
    }

}

//Main class
public class EmployeeProject{
    public static void main(String[] args) {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            Employee employeeProject = new Employee();
            try {
                employeeProject.GUI();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}