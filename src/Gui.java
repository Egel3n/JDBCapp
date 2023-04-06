import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.ArrayList;


public class Gui extends JFrame {

    JTextField txtName;
    JTextField txtNumber;
    JTextField txtSearchBar;

    JLabel lblName;
    JLabel lblNumber;
    JLabel lblSearch;
    JButton btnInsert;
    JButton btnDelete;
    JButton btnUpdate;
    JTable tblData;
    DefaultTableModel model;
    JScrollPane sclPane;

    Gui()  {
        //TextFields
        txtName = new JTextField();
        txtName.setBounds(100,50,150,25);
        txtNumber = new JTextField();


        //Number Kısmına Char girilmesini engelleyen key adapter
        txtNumber.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String value = txtNumber.getText();
                int l = value.length();
                if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') {
                    txtNumber.setEditable(true);

                } else {
                    txtNumber.setEditable(false);
                }
            }
        });
        txtNumber.setBounds(100,90,150,25);
        txtSearchBar = new JTextField();
        txtSearchBar.addKeyListener(new Searcher());
        txtSearchBar.setBounds(100,150,340,25);
        add(txtSearchBar);
        add(txtName);
        add(txtNumber);


        //Labels
        lblName = new JLabel("Name:");
        lblName.setBounds(40,50,50,25);
        lblNumber = new JLabel("Number:");
        lblNumber.setBounds(40,90,50,25);
        lblSearch = new JLabel("Search:");
        lblSearch.setBounds(40,150,50,25);
        add(lblSearch);
        add(lblName);
        add(lblNumber);


        //Buttons
        btnInsert = new JButton("Insert");
        btnInsert.setBounds(270,70,90,25);
        btnInsert.addActionListener(new ButtonPressed());
        add(btnInsert);
        btnDelete = new JButton("Delete");
        btnDelete.setBounds(450,350,90,25);
        btnDelete.addActionListener(new ButtonPressed());
        add(btnDelete);
        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(450,300,90,25);
        btnUpdate.addActionListener(new ButtonPressed());
        add(btnUpdate);


        //JTable
        model =new DefaultTableModel();
        tblData = new JTable(model);
        model.addColumn("Name");
        model.addColumn("Number");

        sclPane = new JScrollPane(tblData);
        sclPane.setBounds(40,200,400,200);
        add(sclPane);
        createTable();


        //GUI settings
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        setLayout(null);
        setSize(700,600);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("My DataBase Application");
    }




    class ButtonPressed implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            SqlDBHelper sqlhelper = new SqlDBHelper();
            PreparedStatement statement;
            Connection conn = sqlhelper.getConnection();


            if(e.getSource() == btnInsert){
                try {
                    String SQL = "insert into studentdata values(?,?)";
                    statement = conn.prepareStatement(SQL);
                    statement.setString(1,txtName.getText());
                    statement.setInt(2,Integer.parseInt(txtNumber.getText()));
                    statement.executeUpdate();
                    createTable();
                } catch (SQLException ex) {
                   sqlhelper.printErrors(ex);
                }
            }
            else if (e.getSource() == btnDelete){

                try {
                    String SQL = "delete from studentdata where name=?";
                    statement = conn.prepareStatement(SQL);
                    statement.setString(1,tblData.getValueAt(tblData.getSelectedRow(),0).toString());
                    statement.executeUpdate();
                    createTable();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
            else if (e.getSource() == btnUpdate){

                try {
                    String SQL = "update studentdata set name = ? ,number = ? where name = ?";
                    statement = conn.prepareStatement(SQL);
                    statement.setString(1,txtName.getText());
                    statement.setInt(2,Integer.parseInt(txtNumber.getText()));
                    statement.setString(3,tblData.getValueAt(tblData.getSelectedRow(),0).toString());
                    statement.executeUpdate();
                    createTable();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    //Creates a Student ArrayList
    public ArrayList<StudentSQL> getStudents() throws SQLException{
        SqlDBHelper sqlhelper = new SqlDBHelper();
        Connection conn = sqlhelper.getConnection();
        Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery("select * from studentdata");
        ArrayList<StudentSQL> students = new ArrayList<>();
        while(result.next()){
            students.add(new StudentSQL(
                    result.getString("Name"),
                    result.getInt("Number")
            ));
        }
        return students;
    }

    public void createTable() {
        SqlDBHelper sqlhelper = null;
        try {
            sqlhelper = new SqlDBHelper();
            Connection conn = sqlhelper.getConnection();
            model = (DefaultTableModel) tblData.getModel();
            model.setRowCount(0);
            ArrayList<StudentSQL> students = getStudents();
            for (StudentSQL student : students) {
                Object[] row = {student.getName(), student.getNumber()};
                model.addRow(row);
            }
        } catch (SQLException ex) {
            sqlhelper.printErrors(ex);
        }
    }

    class Searcher extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            String searchkey = txtSearchBar.getText();
            TableRowSorter<DefaultTableModel> tableRowSorter = new TableRowSorter<>(model);
            tblData.setRowSorter(tableRowSorter);
            tableRowSorter.setRowFilter(RowFilter.regexFilter(searchkey));
        }
    }
}
