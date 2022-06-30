import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

public class LandingPage {
    static Connection con;
    static String aadhar;
    static HashMap<String, String> hashMap;
    static int amount = 0;
    static int newBalance = 0;
    static int recAccount = 0;
    static BufferedReader bufferedReader;

    public LandingPage(String aadhaar, Connection connection, BufferedReader bufferedReader) {
        this.aadhar = aadhaar;
        System.out.println(aadhar);
        this.con = connection;
        this.bufferedReader = bufferedReader;
        hashMap = new HashMap<>();

    }

    public static void main() throws Exception {
        int ch = 0;
        while (true) {
            fetchUserData();
            System.out.println("---------------Account----------------");
            System.out.println("\nName : " + hashMap.get("name"));
            System.out.println("Account no : " + hashMap.get("acc_no"));
            System.out.println("Balance : " + hashMap.get("amount"));
            System.out.println("\n\t\t\t1.Withdraw\n\t\t\t2.deposit\n\t\t\t3.Transaction\n\t\t\t4.Edit Profile\n\t\t\t5.Exit");
            try {
                System.out.print("Enter your options :");
                ch = Integer.parseInt(bufferedReader.readLine());
            } catch (Exception e) {
                continue;
            }
            switch (ch) {
                case 1:
                    if (verifyamount()) {
                        if (Integer.parseInt(hashMap.get("amount")) > amount && amount > 0) {
                            widthdraw(amount);
                        } else {
                            System.out.println("Enter valid amount");
                        }
                    }
                    break;
                case 2:
                    if (verifyamount()) {
                        deposit(Integer.parseInt(hashMap.get("amount")), amount);
                    }
                    break;
                case 3:
                    transaction();
                    break;

                case 4:
                    editprofile();
                    break;
                case 5:
                    //TODO:Edit;
                case 6:
                    System.exit(0);


            }
        }
    }

    static String pass = "";
    static int type = 0;
    static String val = "";

    private static void editprofile() throws IOException, SQLException {
//        System.out.println("Enter your password: ");
//        pass = (bufferedReader.readLine());
//        if (pass.equals(hashMap.get("password"))) {
//            System.out.println("correct");
//        } else {
//            editprofile();
//        }
        try {
            System.out.println("\n\t\t\t1.EditName\n\t\t\t2.EditPhone\n\t\t\t3.Aadhar\n\t\t\t4.DOB\n\t\t\t5.Gender\n\t\t\t6.Email\n\t\t\t7.password\n\t\t\t8.exit");
            System.out.println("Enter your password: ");
            type = Integer.parseInt((bufferedReader.readLine()));
            val = (type == 1) ? "name" : (type == 2) ? "phone" : (type == 3) ? "aadhar" : (type == 4) ? "dob" : (type == 5) ? "gender" : (type == 6) ? "email" : (type == 1) ? "password" : "";
            if (val.equals("")) {
                System.out.println("Please Enter correct option: ");
                editprofile();
            }
            if (type == 8) {
                System.exit(0);
            }
            PreparedStatement stmt = con.prepareStatement("update users set " + val + "=? where acc_no=?");


            if (type == 5) {
                System.out.println("\t\t\t1.male\n\t\t\t2.female\n\t\t\t3.others");
                System.out.print("Enter your option : ");
                int newtype = Integer.parseInt((bufferedReader.readLine()));

                stmt.setInt(1, newtype);

            } else {

                System.out.println("Enter new value : ");
                String newval = (bufferedReader.readLine());

                stmt.setString(1, newval);

            }


            stmt.setString(2, hashMap.get("acc_no"));

            if (stmt.executeUpdate() == 1) {
                System.out.println("changed Successfully...!");
            }
            editprofile();
        } catch (Exception e) {
            editprofile();
        }
    }


    static int newB = 0;

    private static void transaction() throws Exception {
        try {
            System.out.println("Enter receiver account number: ");
            recAccount = Integer.parseInt(bufferedReader.readLine());
        } catch (Exception e) {
            transaction();
        }
        if (verifyaccount(recAccount)) {
            if (verifyamount()) {
                PreparedStatement stmt = con.prepareStatement("update  users set amount=? where acc_no=" + Integer.parseInt(hashMap.get("acc_no")));
                stmt.setInt(1, Integer.parseInt(hashMap.get("amount")) - amount);
                stmt.executeUpdate();
                stmt = con.prepareStatement("select amount from  users where acc_no=?");
                stmt.setInt(1, recAccount);

                ResultSet rs = stmt.executeQuery();
                int cost = 0;
                while (rs.next()) {
                    cost = rs.getInt(1);
                }
                newB = cost + amount;
                System.out.println(newB);
                stmt = con.prepareStatement("update  users set amount=? where acc_no=" + recAccount);
                stmt.setInt(1, newB);
                stmt.executeUpdate();
            }
        }
    }

    private static boolean verifyaccount(int recAccount) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("select acc_no from users where acc_no=?");
        stmt.setInt(1, recAccount);
        ResultSet rs = stmt.executeQuery();
        return !rs.next() ? false : true;

    }

    private static void deposit(int amount, int amount1) throws SQLException {
        newBalance = amount + amount1;
        System.out.println("Deposit succesfully and your current balance: " + newBalance);
        PreparedStatement stmt = con.prepareStatement("update users set amount=? where acc_no=?");
        stmt.setInt(1, newBalance);
        stmt.setInt(2, Integer.parseInt(hashMap.get("acc_no")));
        if (stmt.executeUpdate() == 1) {
            System.out.println("update");
        } else {
            System.out.println("not");
        }
    }

    private static void widthdraw(int amount) throws SQLException {
        newBalance = Integer.parseInt(hashMap.get("amount")) - amount;
        System.out.println("widthdraw succesfully and your current balance: " + newBalance);
        PreparedStatement stmt = con.prepareStatement("update users set amount=? where acc_no=?");
        stmt.setInt(1, newBalance);
        stmt.setInt(2, Integer.parseInt(hashMap.get("acc_no")));
        if (stmt.executeUpdate() == 1) {
            System.out.println("update");
        } else {
            System.out.println("not");
        }
    }

    public static void fetchUserData() throws Exception {
        //map, dict
        PreparedStatement stmt = con.prepareStatement("select * from users where aadhar=?");
        stmt.setString(1, aadhar);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            hashMap.put("acc_no", rs.getString(1));
            hashMap.put("name", rs.getString(2));
            hashMap.put("aadhar", rs.getString(3));
            hashMap.put("phone", rs.getString(4));
            hashMap.put("amount", String.valueOf(rs.getInt(5)));
            hashMap.put("gender", rs.getString(6));
            hashMap.put("dob", rs.getString(7));
            hashMap.put("email", rs.getString(8));
            hashMap.put("password", rs.getString(9));
        }
    }

    public static boolean verifyamount() throws Exception {
        while (true) {
            try {
                System.out.println("Enter the Amount : ");
                amount = Integer.parseInt(bufferedReader.readLine());
                return true;
            } catch (Exception e) {
                System.out.println("Renter.");
                continue;
            }
        }
    }
}


