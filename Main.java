import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(System.in));
    PreparedStatement stmt;
    public static void main(String[] args) throws  Exception{
        Class.forName("com.mysql.cj.jdbc.Driver");
        int ch=0;
        Main main=new Main();
        Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/Bank","root","");
        while (true) {
            System.out.println("----------Bank Management System----------");
            System.out.println("1.Create\n2.Signin\n3.Exit");
            System.out.print("Enter your option:");
            try {
                ch = Integer.parseInt(main.bufferedReader.readLine());
                System.out.println(ch);
            }catch (Exception e){
                System.out.println("Please Enter the right choice.");
                continue;
            }
            switch (ch) {
                case 1:
                    main.createUser(con);
                    break;
                case 2:
                    main.login(con);
                    break;
                case 3:
                    System.exit(0);
                default:
                    break;
            }
        }
    }
    private void loginuser(Connection con, String name, int acc_no,String adhar) throws Exception {
        String text =  "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        SecureRandom rnd = new SecureRandom();
        for(int i=0;i<20;i++){
            sb.append(text.charAt(rnd.nextInt(62)));
        }
        stmt= con.prepareStatement("insert into login(name,acc_no,token,active) values(?,?,?,?)");
        stmt.setString(1,name);
        stmt.setInt(2,acc_no);
        stmt.setString(3, sb.toString());
        stmt.setInt(4,1);
        if(stmt.executeUpdate()==1){

//                    LandingPage dashboard=new LandingPage(adhar,con,bufferedReader);
//                       LandingPage.main();

        }
    }
    private void login(Connection con) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your Account number:");
        int acc_no = sc.nextInt();
        System.out.print("Enter Password:");
        String pass = sc.next();
        stmt=con.prepareStatement("select * from users where acc_no=?");
        stmt.setInt(1,acc_no);
        ResultSet rs=stmt.executeQuery();
        String name="";
        String adhar="";

        while (rs.next()) {
            name = rs.getString(2);
            adhar=rs.getString(3);
            if (rs.getString(9).equals(pass)) {
                System.out.println("Password correct");
                LandingPage dashboard=new LandingPage(adhar,con,bufferedReader);
                LandingPage.main();
                break;
            }
            else{
                System.out.println("Incorrect password");
                login(con);

            }
        }
        loginuser(con,name,acc_no,adhar);
    }
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        String salt="qwertyuiopoigfdsacvbnm,237890!@#$%^&*QWERTYUIKJHGFDSCVHJITRESXCVBL:";
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest((password+salt).getBytes());
        StringBuilder builder=new StringBuilder(new BigInteger(1, messageDigest).toString(16));
        return builder.reverse().toString();
    }
    public void createUser(Connection con) throws Exception{
        Pattern pattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
        Pattern regexForPhone = Pattern.compile("[0-9]{10}");
        Pattern regexForAadhar = Pattern.compile("[0-9]{12}");
        stmt=con.prepareStatement("insert into users(name,aadhar,phone,amount,gender,dob,email,password)values(?,?,?,?,?,?,?,?)");
        String name ="",dob="",email="",password="",phone="",adhar="";
        int amount=0,gender=0;
        while (true){
            if (name.length() == 0) {
                System.out.print("Enter the name :");
                name = bufferedReader.readLine().trim();
                continue;
            }
            if (!pattern.matcher(dob).matches()) {
                System.out.print("Enter the Date (YYYY-MM-DD):");
                dob = bufferedReader.readLine();
                continue;
            }
            if (!email.contains("@")) {
                System.out.print("Enter the Email :");
                email = bufferedReader.readLine();
                continue;
            }
            if (password.length() <4) {
                System.out.print("Enter the Password :");
                password = bufferedReader.readLine();
                continue;
            }
            if (!regexForPhone.matcher(phone).matches()) {
                System.out.print("Enter your Phone no :");
                phone = bufferedReader.readLine();
                continue;
            }
            if (!regexForAadhar.matcher(adhar).matches()) {
                System.out.print("Enter your Adhaar no :");
                adhar = bufferedReader.readLine();
                continue;
            }
            if (String.valueOf(amount).contains("-") || String.valueOf(amount).length() == 0 || (String.valueOf(amount).length() == 1 && String.valueOf(amount).equals("0"))) {
                try {
                    System.out.print("Enter your Amount no :");
                    amount = Integer.parseInt(bufferedReader.readLine());
                }catch(Exception e) {
                    System.out.println("Please enter correct value...");
                    continue;
                }
            }
            if (gender !=1 && gender!=2 &&gender != 3) {
                try {
                    System.out.print("Select your Gender \n1.Male\n2.Female\n3.Others\nEnter :");
                    gender = Integer.parseInt(bufferedReader.readLine());
                }catch (Exception e) {
                    System.out.print("Enter the Given options in number only.");
                    continue;
                }
            } else {
                stmt.setString(1, name);
                stmt.setString(2, adhar);
                stmt.setString(3, phone);
                stmt.setInt(4, amount);
                stmt.setInt(5, gender);
                stmt.setString(6, dob);
                stmt.setString(7, email);
                stmt.setString(8, password);
                if (stmt.executeUpdate() == 1) {

//
//                    sendMail(email);
//                    System.out.print("Enter OTP : ");
//                    Scanner sc = new Scanner(System.in);
//                    int gnotp = sc.nextInt();
//
//                    if(gnotp==otp) {
                        LandingPage dashboard=new LandingPage(adhar,con,bufferedReader);
                        LandingPage.main();
//                    }
//                    else {
//                        System.out.println("incorrect OTP.");
//                        createUser(con);
//                    }
                }
                System.out.println("Data Inserted Successfully.");
                break;
            }
        }
    }
//    static int  otp;
//    public static void sendMail(String recp){
//        Properties properties=new Properties();
//        properties.put("mail.smtp.auth","true");
//        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
//        properties.put("mail.smtp.starttls.enable","true");
//        properties.put("mail.smtp.host","smtp.gmail.com");
//        properties.put("mail.smtp.port","587");
//
//        String username="manmade5051@gmail.com";
//        String password="securecooker@5051";
//
//        Session session= Session.getInstance(properties, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(username,password);
//            }
//        });
//
//        otp= (int) Math.floor(Math.random() * 999999) + 100000;
//
//        Message message=prepareMessage(session,username,recp,otp);
//        try {
//            Transport.send(message);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//    public static Message prepareMessage(Session session,String username,String recp,int otp){
//        Message message=new MimeMessage(session);
//        try {
//            message.setFrom(new InternetAddress(username));
//            message.setRecipient(Message.RecipientType.TO,new InternetAddress(recp));
//            message.setSubject("Revolt bank otp verification.");
//            message.setText("Your otp is "+otp);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return message;
//    }
}

